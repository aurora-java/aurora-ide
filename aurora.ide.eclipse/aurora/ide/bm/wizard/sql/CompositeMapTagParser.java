/*
 * Created on 2008-7-10
 */
package aurora.ide.bm.wizard.sql;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import aurora.ide.api.composite.map.CommentCompositeMap;

import uncertain.composite.CompositeMap;
import uncertain.util.TagParseHandle;
import uncertain.util.TagProcessor;
import uncertain.util.UnixShellTagProcessor;

public class CompositeMapTagParser {

	TagProcessor processor;

	/**
	 * @param processor
	 */
	public CompositeMapTagParser(TagProcessor processor) {
		this.processor = processor;
	}

	public CompositeMapTagParser() {
		processor = new UnixShellTagProcessor();
	}

	void appendString(int index, TagProcessor processor, TagParseHandle handle) {
		String tag = processor.getTagString();
		if (tag != null)
			if (tag.length() > 0)
				handle.ProcessTag(index, tag);
	}

	private CompositeMap parse(Reader reader, ParameterHandle handle)
			throws IOException {

		int index = 0;
		int tag_begin = 0;
		char tag_chr = processor.getStartingEscapeChar();

		int ch;

		processor.setEscapeState(false);

		while ((ch = reader.read()) != -1) {
			char chr = (char) ch;
			if (!processor.isEscapeState()) {
				if (tag_chr == chr) {
					processor.setEscapeState(true);
					tag_begin = index;
				}
			} else {
				if (processor.accept(chr)!=0) {
					processor.setEscapeState(false);
					appendString(tag_begin, processor, handle);
				}
			}
			index++;
		}

		if (processor.isEscapeState())
			appendString(tag_begin, processor, handle);

		return handle.getParameters();

	}

	public void clear() {
		processor.clear();
		processor = null;
	}

	public class ParameterHandle implements TagParseHandle {
		String bm_uri = "http://www.aurora-framework.org/schema/bm";
		String bm_pre = "bm";
		CompositeMap parameters = new CommentCompositeMap(bm_pre, bm_uri, "parameters");

		public String ProcessTag(int index, String tag) {
			CompositeMap child = new CommentCompositeMap(bm_pre, bm_uri, "parameter");
			if (tag.startsWith("@")) {
				child.put("name", tag.substring(1));
			} else {
				child.put("inputPath", tag);
			}
			if(parameters.getChild(child)== null)
				parameters.addChild(child);
			return tag;
		}

		public int ProcessCharacter(int index, char ch) {
			return ch;
		}

		public CompositeMap getParameters() {
			return parameters;
		}

	}

	public static void main(String[] args){
		UnixShellTagProcessor pr = new UnixShellTagProcessor();
		CompositeMapTagParser parser = new CompositeMapTagParser(pr);
		CompositeMap s = parser
				.parse(new String(
						"begin"
								+ "insert into  emp ( empno, ename, deptno, creation_date, created_by ) "
								+ "values ( emp_s.nextval, ${/session/@user_id}, ${@deptno}, sysdate, ${/session/@user_id})"
								+ "returning empno into ${@empno};" + "end;"));
		System.out.println(s.toXML());
	}

	public CompositeMap parse(String str) {
		if (str == null)
			return null;
		try {
			return parse(new StringReader(str), new ParameterHandle());
		} catch (IOException ex) {
			return null;
		}
	}
}
