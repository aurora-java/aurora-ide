package aurora.ide.editor.textpage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import uncertain.composite.XMLOutputter;
import aurora.ide.editor.textpage.scanners.XMLPartitionScanner;
import aurora.ide.editor.textpage.scanners.XMLTagScanner;

public class XMLAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
	private static Set<String> textTagMark = new HashSet<String>(Arrays.asList(
			"script", "sql", "style", "freemarker"));

	@Override
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		try {
			if ("/".equals(c.text)) {
				autoCloseXMLTagBySlash(d, c);
			} else if (">".equals(c.text)) {
				// autoCloseXMLTagByGt(d, c);
			}
			if (c.text != null
					&& (c.text.startsWith("\n") || c.text.startsWith("\r"))) {
				autoInsertChild(d, c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void autoCloseXMLTagBySlash(IDocument d, DocumentCommand c)
			throws Exception {
		if (c.offset <= 0 || d.getChar(c.offset - 1) != '<')
			return;
		String tagName = findOpeningTag(d, 0, c.offset - 1);
		if (tagName == null)
			return;
		int nextChar = c.offset;
		if (c.offset < d.getLength() && d.getChar(c.offset) == '>') {
			nextChar++;
		}
		String tagName2 = findClosedTag(d, nextChar, d.getLength());
		if (tagName.equals(tagName2))
			return;
		c.text = "/" + tagName + (nextChar == c.offset ? ">" : "");
	}

	private String findOpeningTag(IDocument doc, int start, int end)
			throws Exception {
		ITypedRegion[] regions = doc.computePartitioning(start, end - start);
		Stack<String> tagStack = new Stack<String>();
		for (int i = regions.length - 1; i >= 0; i--) {
			String type = regions[i].getType();
			String tagName = getTagName(doc, regions[i]);

			if (XMLPartitionScanner.XML_START_TAG.equals(type)) {
				char c = doc.getChar(regions[i].getOffset()
						+ regions[i].getLength() - 2);
				if (c == '/') {
					// 自闭
					debug("close:" + tagName);
					continue;
				}
				if (tagStack.isEmpty()) {
					debug("find open tag:" + tagName);
					return tagName;
				} else {
					if (tagStack.peek().equals(tagName))
						debug("Pop :" + tagStack.pop());
					else {
						debug("Unmatched tag:" + tagStack.peek() + " != "
								+ tagName);
						return null;
					}
				}
			} else if (XMLPartitionScanner.XML_END_TAG.equals(type)) {
				debug("Push:" + tagName);
				if (tagName == null)
					return null;
				tagStack.push(tagName);
			} else
				continue;
		}
		return null;
	}

	private String findClosedTag(IDocument doc, int start, int end)
			throws Exception {
		while (start < doc.getLength() && doc.getChar(start) != '<')
			start++;
		if (start >= doc.getLength())
			return null;
		ITypedRegion[] regions = doc.computePartitioning(start, end - start);
		Stack<String> tagStack = new Stack<String>();
		debug("--------");
		for (int i = 0; i < regions.length; i++) {
			String type = regions[i].getType();
			String tagName = getTagName(doc, regions[i]);

			if (XMLPartitionScanner.XML_END_TAG.equals(type)) {
				if (tagStack.isEmpty()) {
					debug("find closed tag : " + tagName);
					return tagName;
				} else {
					if (tagStack.peek().equals(tagName))
						debug("Pop :" + tagStack.pop());
					else {
						debug("Unmatched tag:" + tagStack.peek() + " != "
								+ tagName);
						return null;
					}
				}
			} else if (XMLPartitionScanner.XML_START_TAG.equals(type)) {
				char c = doc.getChar(regions[i].getOffset() + 1);
				if (c == '/') {
					debug("xxx find closed tag:" + tagName);
					return tagName;
				}
				c = doc.getChar(regions[i].getOffset() + regions[i].getLength()
						- 2);
				if (c == '/') {
					// 自闭
					debug("close:" + tagName);
					continue;
				}
				debug("Push:" + tagName);
				if (tagName == null)
					return null;
				tagStack.push(tagName);
			} else
				continue;
		}
		return null;
	}

	private String getTagName(IDocument doc, IRegion region) throws Exception {
		XMLTagScanner scanner = new XMLTagScanner(new ColorManager());
		scanner.setRange(doc, region.getOffset(), region.getLength());
		IToken token = Token.EOF;
		while ((token = scanner.nextToken()) != Token.EOF) {
			if (token.getData() instanceof TextAttribute) {
				TextAttribute text = (TextAttribute) token.getData();
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				if (text.getForeground().getRGB()
						.equals(IColorConstants.TAG_NAME)) {
					return doc.get(tokenOffset, tokenLength);
				}
			}
		}
		return null;
	}

	protected void autoCloseXMLTagByGt(IDocument d, DocumentCommand c)
			throws Exception {
		if (c.offset < 2)
			return;
		if (d.getChar(c.offset - 1) == '<') {
			debug("empty tag.");
			return;
		}
		int ltIndex = c.offset - 1;
		char ch;
		while (ltIndex >= 0 && (ch = d.getChar(ltIndex)) != '<') {
			if (ch == '>' || ch == '/') {
				debug("invalid char '" + ch + "' before '<'.");
				return;
			}
			ltIndex--;
		}
		if (ltIndex < 0) {
			debug("'<' not found.");
			return;
		}
		String tagName = getTagName(d, new Region(ltIndex, c.offset - ltIndex));
		if (tagName == null) {
			debug("tagName not found.");
			return;
		}
		debug("find tagName:" + tagName);
		String restStr = d.get(c.offset, d.getLength() - c.offset);
		if (restStr == null || restStr.trim().length() == 0) {
			debug("nothing after caretOffset , close directly.");
			d.replace(c.offset, 0, "></" + tagName + ">");
			c.offset += 1;
			c.text = "";
			return;
		}
		int endTagIndex = restStr.indexOf("</" + tagName + ">");
		if (endTagIndex == -1)
			endTagIndex = restStr.indexOf("</" + tagName + " ");
		if (endTagIndex == -1) {
			debug("no end-tag after caretOffset , close directly.");
			d.replace(c.offset, 0, "></" + tagName + ">");
			c.offset += 1;
			c.text = "";
			return;
		}
		String ws = d.get(c.offset, endTagIndex);
		if (ws.trim().length() == 0) {
			debug("there already has a end-tag ,do nothing.");
			return;
		}
		restStr = d.get(c.offset, endTagIndex);
		int lti1 = restStr.indexOf('<');
		int gti1 = restStr.indexOf('>');
		if (lti1 != -1 && gti1 > lti1) {
			debug("a new tag appear before end-tag , end-tag invalid , close directly."
					+ lti1 + " " + gti1);
			d.replace(c.offset, 0, "></" + tagName + ">");
			c.offset += 1;
			c.text = "";
			return;
		}
		if (lti1 != -1) {
			debug("there is a new '<' after caretOffset.");
			return;
		}
		if (gti1 != -1) {
			debug("there is a '>' after caretOffset.");
			return;
		}

	}

	protected void autoInsertChild(IDocument d, DocumentCommand c) {
		if (c.offset > d.getLength() - 2)
			return;
		try {
			if (!"/>".equals(d.get(c.offset, 2)))
				return;
			ITypedRegion region = d.getPartition(c.offset);
			if (!XMLPartitionScanner.XML_START_TAG.equals(region.getType()))
				return;
			String tagName = getTagName(d, region);
			if (tagName == null)
				tagName = "";

			String format = ">%s%s%s</%s>";
			boolean isTextTag = isTextTag(tagName.toLowerCase());
			if (isTextTag) {
				format = "><![CDATA[%s%s%s]]></%s>";
			}
			String resp = String.format(format, c.text,
					XMLOutputter.DEFAULT_INDENT, c.text, tagName);
			d.replace(c.offset, 2, resp);
			c.offset += resp.length()
					- (3 + tagName.length() + c.text.length() + (isTextTag ? 3
							: 0));
			c.text = "";

		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isTextTag(String tagName) {
		String[] parts = tagName.split("[_-]+");
		for (String s : parts) {
			if (textTagMark.contains(s))
				return true;
		}
		return false;
	}

	private void debug(Object o) {
		// System.out.println(o);
	}
}
