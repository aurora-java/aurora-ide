package aurora.ide.editor.textpage.format;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aurora.ide.helpers.SystemException;

/*
 # Originally written by Einar Lielmanis et al.,
 # Conversion to python by Einar Lielmanis, einar@jsbeautifier.org,
 # MIT licence, enjoy.
 #
 # Python is not my native language, feel free to push things around.
 #
 # Use either from command line (script displays its usage when run
 # without any parameters),
 #
 #
 # or, alternatively, use it as a module:
 #
 #   import jsbeautifier
 #   res = jsbeautifier.beautify("your javascript string")
 #   res = jsbeautifier.beautify_file("some_file.js")
 #
 #  you may specify some options:
 #
 #   opts = jsbeautifier.default_options()
 #   opts.indent_size = 2
 #   res = jsbeautifier.beautify("some javascript", opts)
 #
 #
 # Here are the available options: (read source)*/

class BeautifierOptions {
	int indent_size = 4;
	String indent_char = " ";
	boolean preserve_newlines = true;
	int max_preserve_newlines = 10;
	boolean jslint_happy = false;
	String brace_style = "collapse";
	boolean keep_array_indentation = false;
	int indent_level = 0;
	public BeautifierOptions() {
	}
	/**
	 * @param indentSize
	 * @param indentChar
	 * @param preserveNewlines
	 * @param maxPreserveNewlines
	 * @param jslintHappy
	 * @param braceStyle
	 * @param keepArrayIndentation
	 * @param indentLevel
	 */
	public BeautifierOptions(int indentSize, String indentChar, boolean preserveNewlines, int maxPreserveNewlines,
			boolean jslintHappy, String braceStyle, boolean keepArrayIndentation, int indentLevel) {
		super();
		indent_size = indentSize;
		indent_char = indentChar;
		preserve_newlines = preserveNewlines;
		max_preserve_newlines = maxPreserveNewlines;
		jslint_happy = jslintHappy;
		brace_style = braceStyle;
		keep_array_indentation = keepArrayIndentation;
		indent_level = indentLevel;
	}

}

class BeautifierFlags {
	public String previous_mode = "BLOCK";
	public String mode;
	boolean var_line = false;
	boolean var_line_tainted = false;
	boolean var_line_reindented = false;
	boolean in_html_comment = false;
	boolean if_line = false;
	boolean in_case = false;
	boolean eat_next_space = false;
	int indentation_baseline = -1;
	int indentation_level = 0;
	int ternary_depth = 0;
	public BeautifierFlags(String mode) {
		this.mode = mode;
	}

}
public class JSBeautifier {
	public BeautifierOptions opts = new BeautifierOptions();
	public BeautifierFlags flags = new BeautifierFlags("BLOCK");
	public JSBeautifier() {
		blank_state();
	}
	public JSBeautifier(BeautifierOptions opts) {
		this.opts = opts;
		blank_state();
	}
	public void blank_state() {
		set_mode("BLOCK");
	}
	public List flag_store = new LinkedList();

	boolean wanted_newline = false;
	boolean just_added_newline = false;
	boolean do_block_just_closed = false;

	String indent_string = "    ";
	String last_word = ""; // last TK_WORD seen
	String last_type = "TK_START_EXPR"; // last token type
	String last_text = ""; // last token text
	String last_last_text = ""; // pre-last token text

	String input = null;
	StringBuffer output = new StringBuffer();// formatted javascript gets built
												// here

	String[] whitespace = new String[]{"\n", "\r", "\t", " "};
	String wordchar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_$";
	String digits = "0123456789";
	String[] punct = "+ - * / % & ++ -- = += -= *= /= %= == === != !== > < >= <= >> << >>> >>>= >>= <<= && &= | || ! !! , : ? ^ ^= |= ::"
			.split(" ");

	// Words which always should start on a new line
	String[] line_starters = "continue,try,throw,return,var,if,switch,case,default,for,while,break,function".split(",");

	int parser_pos = 0;
	int n_newlines = 0;

	public BeautifierOptions default_options() {
		return new BeautifierOptions();
	}

	public String beautify_file(String file_name, BeautifierOptions opts) throws SystemException {
		File file = new File(file_name);
		BufferedReader br = null;
		StringBuffer content = new StringBuffer();
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			while ((line = br.readLine()) != null) {
				content.append(line).append("\r\n");
			}
		} catch (Throwable e) {
			throw new SystemException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw new SystemException(e);
				}
			}
		}
		return beautify(content.toString(), opts);
	}

	public void usage() {

		System.out.println("Javascript beautifier (http://jsbeautifier.org/)"
				+ "Usage: jsbeautifier.py [options] <infile>" + "    <infile> can be '-', which means stdin."
				+ "Input options:" + " -i,  --stdin                      read input from stdin" + "Output options:"
				+ " -s,  --indent-size=NUMBER         indentation size. (default 4)."
				+ " -c,  --indent-char=CHAR           character to indent with. (default space)."
				+ " -d,  --disable-preserve-newlines  do not preserve existing line breaks."
				+ " -j,  --jslint-happy               more jslint-compatible output"
				+ " -b,  --brace-style=collapse       brace style (collapse, expand, end-expand)"
				+ " -k,  --keep-array-indentation     keep array indentation." + "Rarely needed options:"
				+ " -l,  --indent-level=NUMBER        initial indentation level. (default 0)."
				+ " -h,  --help, --usage              prints this help statement.");
	}
	public String beautify(String s, BeautifierOptions opts) {
		if (opts != null)
			this.opts = opts;
		if (!isIn(opts.brace_style, new String[]{"expand", "collapse", "end-expand"})) {
			throw new RuntimeException("opts.brace_style must be 'expand', 'collapse' or 'end-expand'.");
		}
		blank_state();
		input = s;
		parser_pos = 0;
		while (true) {
			String[] token = get_next_token();
			String token_text = token[0];
			String token_type = token[1];
//			System.out.println("token_text:" + token_text + " token_type:" + token_type + " flags.mode:" + flags.mode);
			if ("TK_EOF".equals(token_type)) {
				break;
			}
			if ("TK_START_EXPR".equals(token_type)) {
				handle_start_expr(token_text);
			} else if ("TK_END_EXPR".equals(token_type)) {
				handle_end_expr(token_text);
			} else if ("TK_START_BLOCK".equals(token_type)) {
				handle_start_block(token_text);
			} else if ("TK_END_BLOCK".equals(token_type)) {
				handle_end_block(token_text);
			} else if ("TK_WORD".equals(token_type)) {
				handle_word(token_text);
			} else if ("TK_SEMICOLON".equals(token_type)) {
				handle_semicolon(token_text);
			} else if ("TK_STRING".equals(token_type)) {
				handle_string(token_text);
			} else if ("TK_EQUALS".equals(token_type)) {
				handle_equals(token_text);
			} else if ("TK_OPERATOR".equals(token_type)) {
				handle_operator(token_text);
			} else if ("TK_BLOCK_COMMENT".equals(token_type)) {
				handle_block_comment(token_text);
			} else if ("TK_INLINE_COMMENT".equals(token_type)) {
				handle_inline_comment(token_text);
			} else if ("TK_COMMENT".equals(token_type)) {
				handle_comment(token_text);
			} else if ("TK_UNKNOWN".equals(token_type)) {
				handle_unknown(token_text);
			}
			last_last_text = last_text;
			last_type = token_type;
			last_text = token_text;
		}
		String str = output.toString().replaceAll("[\n ]+$", "");
		return str;
	}

	public void trim_output(boolean eat_newlines) {
		while (output != null
				&& output.length() > 0
				&& (" ".equals(output.substring(output.length() - 1))
						|| indent_string.equals(output.substring(output.length() - 1))
						|| (eat_newlines && isEq(output.substring(output.length() - 1), " "))
						|| isEq(output.substring(output.length() - 1), indent_string) || (eat_newlines && isIn(output
						.substring(output.length() - 1), new String[]{"\n", "\r"})))) {
			output.deleteCharAt(output.length() - 1);
		}
	}

	public boolean is_array(String mode) {
		if ("[EXPRESSION]".equals(mode) || "[INDENDED-EXPRESSION]".equals(mode)) {
			return true;
		}
		return false;
	}
	public boolean is_expression(String mode) {
		if ("[EXPRESSION]".equals(mode) || "[INDENDED-EXPRESSION]".equals(mode) || "EXPRESSION".equals(mode)) {
			return true;
		}
		return false;
	}

	public void append_newline(boolean ignore_repeated) {
		flags.eat_next_space = false;
		if (opts.keep_array_indentation && is_array(flags.mode))
			return;

		flags.if_line = false;
		trim_output(false);
		if (output.length() == 0) {
			// no newline on start of file
			return;
		}
		if (!isEq(output.substring(output.length() - 1), "\n") || !ignore_repeated) {
			just_added_newline = true;
			output.append('\n');
		}
		for (int i = 0; i < flags.indentation_level; i++) {
			output.append(indent_string);
		}
		if (flags.var_line && flags.var_line_reindented) {
			if (" ".equals(opts.indent_char))
				// var_line always pushes 4 spaces, so that the variables would
				// be one under another
				output.append("    ");
			else
				output.append(indent_string);
		}
	}

	public void append(String s) {
		if (" ".equals(s)) {
			// make sure only single space gets drawn
			if (flags.eat_next_space) {
				flags.eat_next_space = false;
			} else if (output != null && !output.toString().endsWith(" ") && !output.toString().endsWith("\n")
					&& !output.toString().endsWith(indent_string)) {
				output.append(" ");
			}
		} else {
			just_added_newline = false;
			flags.eat_next_space = false;
			output.append(s);
		}
	}

	public void indent() {
		flags.indentation_level = flags.indentation_level + 1;
	}

	public void remove_indent() {
		if (output != null && output.toString().endsWith(indent_string)) {
			output.deleteCharAt(output.length() - 1);
		}
	}

	public void set_mode(String mode) {
		BeautifierFlags prev = new BeautifierFlags("BLOCK");
		if (flags != null) {
			flag_store.add(flags);
			prev = flags;
		}
		flags = new BeautifierFlags(mode);
		if (flag_store.size() == 1) {
			flags.indentation_level = opts.indent_level;
		} else {
			flags.indentation_level = prev.indentation_level;
			if (prev.var_line && prev.var_line_reindented) {
				flags.indentation_level = flags.indentation_level + 1;
			}
		}
		flags.previous_mode = prev.mode;
	}

	public void restore_mode() {
		do_block_just_closed = "DO_BLOCK".equals(flags.mode);
		if (flag_store.size() > 0) {
			flags = (BeautifierFlags) flag_store.remove(flag_store.size() - 1);
		}
	}

	public String[] get_next_token() {
		n_newlines = 0;
		if (parser_pos >= input.length()) {
			return new String[]{"", "TK_EOF"};
		}
		wanted_newline = false;
		String c = input.substring(parser_pos, parser_pos + 1);
		parser_pos += 1;

		boolean keep_whitespace = opts.keep_array_indentation && is_array(flags.mode);

		if (keep_whitespace) {
			// slight mess to allow nice preservation of array indentation and
			// reindent that correctly
			// first time when we get to the arrays:
			// var a = [
			// ....'something'
			// we make note of whitespace_count = 4 into
			// flags.indentation_baseline
			// so we know that 4 whitespaces in original source match
			// indent_level of reindented source
			//
			// and afterwards, when we get to
			// 'something,
			// .......'something else'
			// we know that this should be indented to indent_level + (7 -
			// indentation_baseline) spaces

			int whitespace_count = 0;
			while (isIn(c, whitespace)) {
				if ("\n".equals(c)) {
					trim_output(false);
					output.append("\n");
					just_added_newline = true;
					whitespace_count = 0;
				} else if ("\t".equals(c)) {
					whitespace_count += 4;
				} else if ("\r".equals(c)) {

				} else {
					whitespace_count += 1;
				}

				if (parser_pos >= input.length()) {
					return new String[]{"", "TK_EOF"};
				}
				c = input.substring(parser_pos, parser_pos + 1);
				parser_pos += 1;
			}
			if (flags.indentation_baseline == -1) {
				flags.indentation_baseline = whitespace_count;
			}
			if (just_added_newline) {
				for (int i = 0; i < flags.indentation_level + 1; i++) {
					output.append(indent_string);
				}
				if (flags.indentation_baseline != -1) {
					for (int i = 0; i < whitespace_count - flags.indentation_baseline; i++) {
						output.append(" ");
					}
				}
			}
		} else { // not keep_whitespace
			while (isIn(c, whitespace)) {
				if ("\n".equals(c)) {
					if (opts.max_preserve_newlines == 0 || opts.max_preserve_newlines > n_newlines) {
						n_newlines += 1;
					}
				}
				if (parser_pos >= input.length()) {
					return new String[]{"", "TK_EOF"};
				}
				c = input.substring(parser_pos, parser_pos + 1);
				parser_pos += 1;
			}
			if (opts.preserve_newlines && n_newlines > 1) {
				for (int i = 0; i < n_newlines; i++) {
					append_newline(i == 0);
					just_added_newline = true;
				}
			}
			wanted_newline = n_newlines > 0;

		}
		if (wordchar.indexOf(c) != -1) {
			// for auroar
			if ("$".equals(c)) {
				if (parser_pos < input.length()) {
					if ("{".equals(input.substring(parser_pos, parser_pos + 1))) {
						c = c + "{";
						parser_pos += 1;
						if (parser_pos < input.length()) {
							while ((wordchar + "@/}").indexOf(input.substring(parser_pos, parser_pos + 1)) != -1) {
								c = c + input.substring(parser_pos, parser_pos + 1);
								parser_pos += 1;
								if ((parser_pos == input.length())
										|| "}".equals(input.substring(parser_pos - 1, parser_pos)))
									break;
							}
						}
						return new String[]{c, "TK_WORD"};
					}
				}
			}
			// end
			if (parser_pos < input.length()) {
				while (wordchar.indexOf(input.substring(parser_pos, parser_pos + 1)) != -1) {
					c = c + input.substring(parser_pos, parser_pos + 1);
					parser_pos += 1;
					if (parser_pos == input.length())
						break;
				}
			}
			// small and surprisingly unugly hack for 1E-10 representation
			Pattern p = Pattern.compile("^[0-9]+[Ee]$");
			Matcher m = p.matcher(c);

			if (parser_pos != input.length() && "+-".indexOf(input.substring(parser_pos, parser_pos + 1)) != -1
					&& m.find()) {
				String sign = input.substring(parser_pos, parser_pos + 1);
				parser_pos += 1;
				String[] t = get_next_token();
				c += sign + t[0];
				return new String[]{c, "TK_WORD"};
			}
			if ("in".equals(c)) { // in is an operator, need to hack
				return new String[]{c, "TK_OPERATOR"};
			}
			if (wanted_newline && !"TK_OPERATOR".equals(last_type) && !"TK_EQUALS".equals(last_type) && !flags.if_line
					&& (opts.preserve_newlines || !"var".equals(last_text))) {
				append_newline(true);
			}
			return new String[]{c, "TK_WORD"};
		}
		if ("([".indexOf(c) != -1) {
			return new String[]{c, "TK_START_EXPR"};
		}
		if (")]".indexOf(c) != -1) {
			return new String[]{c, "TK_END_EXPR"};
		}
		if ("{".equals(c)) {
			return new String[]{c, "TK_START_BLOCK"};
		}
		if ("}".equals(c)) {
			return new String[]{c, "TK_END_BLOCK"};
		}
		if (";".equals(c)) {
			return new String[]{c, "TK_SEMICOLON"};
		}
		if ("/".equals(c)) {
			String comment = "";
			// boolean inline_comment = true;
			String comment_mode = "TK_INLINE_COMMENT";
			if ("*".equals(input.substring(parser_pos, parser_pos + 1))) { // peek
				// /*
				// ..
				// */
				// comment
				parser_pos += 1;
				if (parser_pos < input.length()) {
					while (!("*".equals(input.substring(parser_pos, parser_pos + 1)) && parser_pos + 1 < input.length() && "/"
							.equals(input.substring(parser_pos + 1, parser_pos + 2)))
							&& parser_pos < input.length()) {
						c = input.substring(parser_pos, parser_pos + 1);
						comment += c;
						if ("\r\n".indexOf(c) != -1) {
							comment_mode = "TK_BLOCK_COMMENT";
						}
						parser_pos += 1;
						if (parser_pos >= input.length())
							break;
					}
				}
				parser_pos += 2;
				return new String[]{"/*" + comment + "*/", comment_mode};
			}
			if ("/".equals(input.substring(parser_pos, parser_pos + 1))) { // peek
				// //
				// comment
				comment = c;
				while ("\r\n".indexOf(input.substring(parser_pos, parser_pos + 1)) == -1) {
					comment += input.substring(parser_pos, parser_pos + 1);
					parser_pos += 1;
					if (parser_pos >= input.length())
						break;
				}
				parser_pos += 1;
				if (wanted_newline)
					append_newline(true);
				return new String[]{comment, "TK_COMMENT"};
			}
		}
		if ("'".equals(c)
				|| "\"".equals(c)
				|| ("/".equals(c) && ((isEq(last_type, "TK_WORD") && isIn(last_text, new String[]{"return", "do"})) || isIn(
						last_type, new String[]{"TK_COMMENT", "TK_START_EXPR", "TK_START_BLOCK", "TK_END_BLOCK",
								"TK_OPERATOR", "TK_EQUALS", "TK_EOF", "TK_SEMICOLON"})))) {
			String sep = c;
			boolean esc = false;
			String resulting_string = c;
			boolean in_char_class = false;
			if (parser_pos < input.length()) {
				if ("/".equals(sep)) {
					// handle regexp
					in_char_class = false;
					while (esc || in_char_class || !isEq(input.substring(parser_pos, parser_pos + 1), sep)) {
						resulting_string += input.substring(parser_pos, parser_pos + 1);
						if (!esc) {
							esc = ("\\".equals(input.substring(parser_pos, parser_pos + 1)));
							if ("[".equals(input.substring(parser_pos, parser_pos + 1))) {
								in_char_class = true;
							} else if ("]".equals(input.substring(parser_pos, parser_pos + 1))) {
								in_char_class = false;
							}
						} else {
							esc = false;
						}
						parser_pos += 1;
						if (parser_pos >= input.length()) {
							// incomplete regex when end-of-file reached
							// bail out with what has received so far
							return new String[]{resulting_string, "TK_STRING"};
						}
					}
				} else {
					// handle string
					while (esc || !sep.equals(input.substring(parser_pos, parser_pos + 1))) {
						resulting_string += input.substring(parser_pos, parser_pos + 1);
						if (!esc) {
							esc = ("\\".equals(input.substring(parser_pos, parser_pos + 1)));
						} else {
							esc = false;
						}
						parser_pos += 1;
						if (parser_pos >= input.length()) {
							// incomplete string when end-of-file reached
							// bail out with what has received so far
							return new String[]{resulting_string, "TK_STRING"};
						}

					}
				}
			}
			parser_pos += 1;
			resulting_string += sep;
			if ("/".equals(sep)) {
				// regexps may have modifiers /regexp/MOD, so fetch those too
				while (parser_pos < input.length()
						&& wordchar.indexOf(input.substring(parser_pos, parser_pos + 1)) != -1) {
					resulting_string += input.substring(parser_pos, parser_pos + 1);
					parser_pos += 1;
				}
			}
			return new String[]{resulting_string, "TK_STRING"};
		}
		if ("#".equals(c)) {
			// she-bang
			if (output.length() == 0 && input.length() > 1 && "!".equals(input.substring(parser_pos, parser_pos + 1))) {
				String resulting_string = c;
				while (parser_pos < input.length() && !"\n".equals(c)) {
					c = input.substring(parser_pos, parser_pos + 1);
					resulting_string += c;
					parser_pos += 1;
				}
				output.append(resulting_string.trim() + "\n");
				append_newline(true);
				return get_next_token();
			}

			// Spidermonkey-specific sharp variables for circular references
			// https://developer.mozilla.org/En/Sharp_variables_in_JavaScript
			// http://mxr.mozilla.org/mozilla-central/source/js/src/jsscan.cpp
			// around line 1935
			String sharp = "#";
			if (parser_pos < input.length() && digits.indexOf(input.substring(parser_pos, parser_pos + 1)) != -1) {
				while (true) {
					c = input.substring(parser_pos, parser_pos + 1);
					sharp += c;
					parser_pos += 1;
					if (parser_pos >= input.length() || isEq(c, "#") || isEq(c, "=")) {
						break;
					}
				}
			}
			if (isEq(c, "#") || parser_pos >= input.length()) {
			} else if (isEq(input.substring(parser_pos, parser_pos + 1), "[")
					&& isEq(input.substring(parser_pos + 1, parser_pos + 2), "]")) {
				sharp += "[]";
				parser_pos += 2;
			} else if (isEq(input.substring(parser_pos, parser_pos + 1), "{")
					&& isEq(input.substring(parser_pos + 1, parser_pos + 2), "}")) {
				sharp += "{}";
				parser_pos += 2;
			}
			return new String[]{sharp, "TK_WORD"};
		}
		if (isEq(c, "<") && "<!--".equals(input.substring(parser_pos - 1, parser_pos + 3))) {
			parser_pos += 3;
			flags.in_html_comment = true;
			return new String[]{"<!--", "TK_COMMENT"};
		}
		if (isEq(c, "-") && flags.in_html_comment && "-->".equals(input.substring(parser_pos - 1, parser_pos + 3))) {
			flags.in_html_comment = false;
			parser_pos += 2;
			if (wanted_newline) {
				append_newline(true);
			}
			return new String[]{"-->", "TK_COMMENT"};
		}
		if (isIn(c, punct)) {
			while (parser_pos < input.length() && isIn(c + input.substring(parser_pos, parser_pos + 1), punct)) {
				c += input.substring(parser_pos, parser_pos + 1);
				parser_pos += 1;
				if (parser_pos >= input.length()) {
					break;
				}
			}
			if (isEq(c, "=")) {
				return new String[]{c, "TK_EQUALS"};
			} else {
				return new String[]{c, "TK_OPERATOR"};
			}
		}
		return new String[]{c, "TK_UNKNOWN"};
	}

	public boolean isIn(String str, String[] strs) {
		if (strs == null || str == null)
			return false;
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].equals(str))
				return true;
		}
		return false;
	}
	public boolean isEq(String str1, String str2) {
		if (str1 == null) {
			if (str2 == null)
				return true;
			return false;
		}
		return str1.equals(str2);
	}
	public void handle_start_expr(String token_text) {
		if ("[".equals(token_text)) {
			if ("TK_WORD".equals(last_type) || ")".equals(last_text)) {
				if (isIn(last_text, line_starters)) {
					append(" ");
				}
				set_mode("(EXPRESSION)");
				append(token_text);
				return;
			}

			if (isIn(flags.mode, new String[]{"[EXPRESSION]", "[INDENTED-EXPRESSION]"})) {
				if (isEq(last_last_text, "]") && isEq(last_text, ",")) {
					// ], [ goes to a new line
					if (isEq(flags.mode, "[EXPRESSION]")) {
						flags.mode = "[INDENTED-EXPRESSION]";
						if (!opts.keep_array_indentation) {
							indent();
						}
					}
					set_mode("[EXPRESSION]");
					if (!opts.keep_array_indentation) {
						append_newline(true);
					}
				} else if (isEq(last_text, "[")) {
					if (isEq(flags.mode, "[EXPRESSION]")) {
						flags.mode = "[INDENTED-EXPRESSION]";
						if (!opts.keep_array_indentation) {
							indent();
						}
					}
					set_mode("[EXPRESSION]");

					if (!opts.keep_array_indentation) {
						append_newline(true);
					}
				} else {
					set_mode("[EXPRESSION]");
				}
			} else {
				set_mode("[EXPRESSION]");
			}
		} else {
			set_mode("(EXPRESSION)");
		}

		if (isEq(last_text, ";") || isEq(last_type, "TK_START_BLOCK")) {
			append_newline(true);
		} else if (isIn(last_type, new String[]{"TK_END_EXPR", "TK_START_EXPR", "TK_END_BLOCK"})
				|| isEq(last_text, ".")) {
			// do nothing on (( and )( and ][ and ]( and .(
		} else if (!isIn(last_type, new String[]{"TK_WORD", "TK_OPERATOR"})) {
			append(" ");
		} else if (isEq(last_word, "function") || isEq(last_word, "typeof")) {
			// function() vs function (), typeof() vs typeof ()
			if (opts.jslint_happy) {
				append(" ");
			}
		} else if (isIn(last_text, line_starters) || isEq(last_text, "catch")) {
			append(" ");
		}
		append(token_text);
	}

	public void handle_end_expr(String token_text) {
		if (isEq(token_text, "]")) {
			if (opts.keep_array_indentation) {
				if (isEq(last_text, "}")) {
					remove_indent();
					append(token_text);
					restore_mode();
					return;
				}
			} else {
				if (isEq(flags.mode, "[INDENTED-EXPRESSION]")) {
					if (isEq(last_text, "]")) {
						restore_mode();
						append_newline(true);
						append(token_text);
						return;
					}
				}
			}
		}
		restore_mode();
		append(token_text);
	}

	public void handle_start_block(String token_text) {
		if (isEq(last_word, "do")) {
			set_mode("DO_BLOCK");
		} else {
			set_mode("BLOCK");
		}
		if (isEq(opts.brace_style, "expand")) {
			if (!isEq(last_type, "TK_OPERATOR")) {
				if (isIn(last_text, new String[]{"return", "="})) {
					append(" ");
				} else {
					append_newline(true);
				}
			}
			append(token_text);
			indent();
		} else {
			if (!isIn(last_type, new String[]{"TK_OPERATOR", "TK_START_EXPR"})) {
				if (isEq(last_type, "TK_START_BLOCK")) {
					append_newline(true);
				} else {
					append(" ");
				}
			} else {
				// if( TK_OPERATOR or TK_START_EXPR
				if (is_array(flags.previous_mode) && isEq(last_text, ",")) {
					if (isEq(last_last_text, "}")) {
						append(" ");
					} else {
						append_newline(true);
					}
				}
			}
			indent();
			append(token_text);
		}
	}

	public void handle_end_block(String token_text) {
		restore_mode();
		if (isEq(opts.brace_style, "expand")) {
			if (!isEq(last_text, "{")) {
				append_newline(true);
			}
		} else {
			if (isEq(last_type, "TK_START_BLOCK")) {
				if (just_added_newline) {
					remove_indent();
				} else {
					// {}
					trim_output(false);
				}
			} else {
				if (is_array(flags.mode) && opts.keep_array_indentation) {
					opts.keep_array_indentation = false;
					append_newline(true);
					opts.keep_array_indentation = true;
				} else {
					append_newline(true);
				}
			}
			append(token_text);
		}
	}

	public void handle_word(String token_text) {
		if (do_block_just_closed) {
			append(" ");
			append(token_text);
			append(" ");
			do_block_just_closed = false;
			return;
		}

		if (isEq(token_text, "function")) {

			if (flags.var_line) {
				flags.var_line_reindented = true;
			}
			if ((just_added_newline || isEq(last_text, ";")) && !isEq(last_text, "{")) {
				// make sure there is a nice clean space of at least one blank
				// line
				// before a new function definition
				int have_newlines = n_newlines;
				if (!just_added_newline) {
					have_newlines = 0;
				}
				if (!opts.preserve_newlines) {
					have_newlines = 1;
				}
				for (int i = 0; i < 2 - have_newlines; i++) {
					append_newline(false);
				}
			}
		}
		if (isIn(token_text, new String[]{"case", "default"})) {
			if (isEq(last_text, ":")) {
				remove_indent();
			} else {
				flags.indentation_level -= 1;
				append_newline(true);
				flags.indentation_level += 1;
			}
			append(token_text);
			flags.in_case = true;
			return;
		}
		String prefix = "NONE";

		if (isEq(last_type, "TK_END_BLOCK")) {
			if (!isIn(token_text, new String[]{"else", "catch", "finally"})) {
				prefix = "NEWLINE";
			} else {
				if (isIn(opts.brace_style, new String[]{"expand", "end-expand"})) {
					prefix = "NEWLINE";
				} else {
					prefix = "SPACE";
					append(" ");
				}
			}
		} else if (isEq(last_type, "TK_SEMICOLON") && isIn(flags.mode, new String[]{"BLOCK", "DO_BLOCK"})) {
			prefix = "NEWLINE";
		} else if (isEq(last_type, "TK_SEMICOLON") && is_expression(flags.mode)) {
			prefix = "SPACE";
		} else if (isEq(last_type, "TK_STRING")) {
			prefix = "NEWLINE";
		} else if (isEq(last_type, "TK_WORD")) {
			if (isEq(last_text, "else")) {
				// eat newlines between ...else *** some_op...
				// won"t preserve extra newlines in this place (if( any), but don"t
				// care that much
				trim_output(true);
			}
			prefix = "SPACE";
		} else if (isEq(last_type, "TK_START_BLOCK")) {
			prefix = "NEWLINE";
		} else if (isEq(last_type, "TK_END_EXPR")) {
			append(" ");
			prefix = "NEWLINE";
		}

		if (flags.if_line && isEq(last_type, "TK_END_EXPR")) {
			flags.if_line = false;
		}
		if (isIn(token_text, line_starters)) {
			if (isEq(last_text, "else")) {
				prefix = "SPACE";
			} else {
				prefix = "NEWLINE";
			}
		}
		if (isIn(token_text, new String[]{"else", "catch", "finally"})) {
			if (!isEq(last_type, "TK_END_BLOCK") || isEq(opts.brace_style, "expand")
					|| isEq(opts.brace_style, "end-expand")) {
				append_newline(true);
			} else {
				trim_output(true);
				append(" ");
			}
		} else if (isEq(prefix, "NEWLINE")) {
			if (isEq(token_text, "function")
					&& (isEq(last_type, "TK_START_EXPR") || isIn(last_text, new String[]{"=", ","}))) {
				// no need to force newline on "function" -
				// (function...
			} else if (isEq(token_text, "function") && isEq(last_text, "new")) {
				append(" ");
			} else if (isIn(last_text, new String[]{"return", "throw"})) {
				// no newline between return nnn
				append(" ");
			} else if (!isEq(last_type, "TK_END_EXPR")) {
				if ((!isEq(last_type, "TK_START_EXPR") || !isEq(token_text, "var")) && !isEq(last_text, ":")) {
					// no need to force newline on VAR -
					// for (var x = 0...
					if (isEq(token_text, "if") && isEq(last_word, "else") && !isEq(last_text, "{")) {
						append(" ");
					} else {
						flags.var_line = false;
						flags.var_line_reindented = false;
						append_newline(true);
					}
				}
			} else if (isIn(token_text, line_starters) && !isEq(last_text, ")")) {
				flags.var_line = false;
				flags.var_line_reindented = false;
				append_newline(true);
			}
		} else if (is_array(flags.mode) && isEq(last_text, ",") && isEq(last_last_text, "}")) {
			append_newline(true); // }, in lists get a newline
		} else if (isEq(prefix, "SPACE")) {
			append(" ");
		}
		append(token_text);
		last_word = token_text;

		if (isEq(token_text, "var")) {
			flags.var_line = true;
			flags.var_line_reindented = false;
			flags.var_line_tainted = false;
		}

		if (isEq(token_text, "if")) {
			flags.if_line = true;
		}

		if (isEq(token_text, "else")) {
			flags.if_line = false;
		}
	}

	public void handle_semicolon(String token_text) {
		append(token_text);
		flags.var_line = false;
		flags.var_line_reindented = false;
		if (isEq(flags.mode, "OBJECT")) {
			// OBJECT mode is weird and doesn"t get reset too well.
			flags.mode = "BLOCK";
		}
	}

	public void handle_string(String token_text) {
		if (isIn(last_type, new String[]{"TK_START_BLOCK", "TK_END_BLOCK", "TK_SEMICOLON"})) {
			append_newline(true);
		} else if (isEq(last_type, "TK_WORD")) {
			append(" ");
		}
		append(token_text);
	}

	public void handle_equals(String token_text) {
		if (flags.var_line) {
			// just got an "=" in a var-line, different line breaking rules will
			// apply
			flags.var_line_tainted = true;
		}
		append(" ");
		append(token_text);
		append(" ");
	}

	public void handle_operator(String token_text) {
		boolean space_before = true;
		boolean space_after = true;

		if (flags.var_line && isEq(token_text, ",") && is_expression(flags.mode)) {
			// do not break on comma, for ( var a = 1, b = 2
			flags.var_line_tainted = false;
		}

		if (flags.var_line && isEq(token_text, ",")) {
			if (flags.var_line_tainted) {
				append(token_text);
				flags.var_line_reindented = true;
				flags.var_line_tainted = false;
				append_newline(true);
				return;
			} else {
				flags.var_line_tainted = false;
			}
		}

		if (isIn(last_text, new String[]{"return", "throw"})) {
			// return had a special handling in TK_WORD
			append(" ");
			append(token_text);
			return;
		}

		if (isEq(token_text, ":") && flags.in_case) {
			append(token_text);
			append_newline(true);
			flags.in_case = false;
			return;
		}

		if (isEq(token_text, "::")) {
			// no spaces around the exotic namespacing syntax operator
			append(token_text);
			return;
		}

		if (isEq(token_text, ",")) {
			if (flags.var_line) {
				if (flags.var_line_tainted) {
					// This never happens, as it"s handled previously, right?
					append(token_text);
					append_newline(true);
					flags.var_line_tainted = false;
				} else {
					append(token_text);
					append(" ");
				}
			} else if (isEq(last_type, "TK_END_BLOCK") && !isEq(flags.mode, "(EXPRESSION)")) {
				append(token_text);
				if (isEq(flags.mode, "OBJECT") && isEq(last_text, "}")) {
					append_newline(true);
				} else {
					append(" ");
				}
			} else {
				if (isEq(flags.mode, "OBJECT")) {
					append(token_text);
					append_newline(true);
				} else {
					// EXPR or DO_BLOCK
					append(token_text);
					append(" ");
				}
			}
			// comma handled
			return;
		} else if (isIn(token_text, new String[]{"--", "++", "!"})
				|| (isIn(token_text, new String[]{"+", "-"}) && isIn(last_type, new String[]{"TK_START_BLOCK",
						"TK_START_EXPR", "TK_EQUALS", "TK_OPERATOR"})) || isIn(last_text, line_starters)) {

			space_before = false;
			space_after = false;

			if (isEq(last_text, ";") && is_expression(flags.mode)) {
				// for (;; ++i)
				// ^^
				space_before = true;
			}

			if (isEq(last_type, "TK_WORD") && isIn(last_text, line_starters)) {
				space_before = true;
			}

			if (isEq(flags.mode, "BLOCK") && isIn(last_text, new String[]{"{", ";"})) {
				// { foo: --i }
				// foo(): --bar
				append_newline(true);
			}
		} else if (isEq(token_text, ".")) {
			// decimal digits or object.property
			space_before = false;
		}

		else if (isEq(token_text, ":")) {
			if (flags.ternary_depth == 0) {
				flags.mode = "OBJECT";
				space_before = false;
			} else {
				flags.ternary_depth -= 1;
			}
		} else if (isEq(token_text, "?")) {
			flags.ternary_depth += 1;
		}

		if (space_before) {
			append(" ");
		}

		append(token_text);

		if (space_after) {
			append(" ");
		}
	}

	public void handle_block_comment(String token_text) {

		String[] lines = token_text.replaceAll("\r", "").split("\n");
		//auora custom
//		if (isEq(token_text.substring(0, 3), "/**")) {
		if (isEq(token_text.substring(0, 3), "/**")||isEq(token_text.substring(0, 2), "/*")) {
			// javadoc: reformat and reindent
			append_newline(true);
			append(lines[0]);
			for (int i = 1; i < lines.length; i++) {
				append_newline(true);
				append(" " + lines[i].trim());
			}
		} else {
			// simple block comment: leave intact
			if (lines.length > 1) {
				// multiline comment starts on a new line
				append_newline(true);
				trim_output(false);
			} else {
				// single line /* ... */ comment stays on the same line
				append(" ");
			}
			for (int i = 0; i < lines.length; i++) {
				append(lines[i]);
				append("\n");
			}
			append_newline(true);
		}
	}

	public void handle_inline_comment(String token_text) {
		append(" ");
		append(token_text);
		if (is_expression(flags.mode)) {
			append(" ");
		} else {
			append_newline(true);
		}
	}

	public void handle_comment(String token_text) {
		if (wanted_newline) {
			append_newline(true);
		} else {
			append(" ");
		}
		append(token_text);
		append_newline(true);
	}

	public void handle_unknown(String token_text) {
		if (isIn(last_text, new String[]{"return", "throw"})) {
			append(" ");
		}
		append(token_text);
	}
	public static void main(String[] args) throws SystemException {
		String file_name = "test.js";
		JSBeautifier bf = new JSBeautifier();
		String js = bf.beautify_file(file_name, bf.opts);
		System.out.println(js);
	}
}

