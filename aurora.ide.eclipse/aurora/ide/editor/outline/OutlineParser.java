package aurora.ide.editor.outline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.Region;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class OutlineParser extends DefaultHandler2 {

	private OutlineTree root;
	private OutlineTree tree;
	private int offset = 0;
	private String source = null;
	private Stack<OutlineTree> stack = new Stack<OutlineTree>();

	public OutlineParser(String source) {
		Assert.isNotNull(source);
		this.setSource(source);
		root = new OutlineTree();
		stack.add(root);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tree = new OutlineTree();
		stack.peek().add(tree);
		stack.push(tree);
		tree.setText(qName);
		tree.setOther(getValue(attributes));
		offset = source.indexOf("<" + qName, offset) + 1;
		tree.setStartRegion(new Region(offset, qName.length()));
		int loc = qName.indexOf(":");
		String name = qName;
		if (loc >= 0) {
			name = qName.substring(loc + 1);
		}
		if ("script".equalsIgnoreCase(name) || "style".equalsIgnoreCase(name)) {
			tree.setImage("script");
		}
		int end = source.indexOf("/>", offset);
		if (end > source.indexOf("<", offset) || end < 0) {
			offset += qName.length();
		} else {
			int start = source.lastIndexOf("<", offset);
			int length = source.indexOf("/>", offset) + 2 - start;
			if (length > 0) {
				tree.setRegion(new Region(start, length));
			}
		}
	}

	@Override
	public void comment(char[] ch, int start, int length) throws SAXException {
		offset = source.indexOf("-->", offset);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ("script".equalsIgnoreCase(stack.peek().getText())) {
			Parser p = new Parser();
			AstRoot ast = null;
			try {
				ast = p.parse(new String(ch, start, length), "", 0);
			} catch (EvaluatorException e) {
				return;
			}
			for (Node node : ast.getStatements()) {
				if (node instanceof VariableDeclaration) {
					createVariableNode((VariableDeclaration) node);
				} else if (node instanceof FunctionNode) {
					createFunctionNode((FunctionNode) node);
				}
			}
		} else if ("style".equalsIgnoreCase(stack.peek().getText())) {
			stack.peek().setImage("script");
		}
	}

	private void createVariableNode(VariableDeclaration node) {
		offset = source.indexOf("var", offset);
		for (VariableInitializer v : node.getVariables()) {
			String name = v.getTarget().getString();
			int start = source.indexOf(name, offset);
			int length = v.toSource().length();
			OutlineTree t = new OutlineTree();
			t.setText(name);
			t.setOther("");
			t.setImage("variable");
			t.setRegion(new Region(start, length + 1));
			t.setStartRegion(new Region(start, name.length()));
			t.setEndRegion(new Region(start, name.length()));
			stack.peek().add(t);
			offset += length;
		}
	}

	private void createFunctionNode(FunctionNode node) {
		offset = source.indexOf("function", offset);
		String name = node.getName();
		int lineno = node.getEndLineno() - node.getBaseLineno();
		int len = node.getLength() + lineno;
		OutlineTree t = new OutlineTree();
		t.setStartRegion(new Region(source.indexOf(name, offset), name.length()));
		t.setEndRegion(new Region(t.getStartRegion().getOffset(), t.getStartRegion().getLength()));
		t.setRegion(new Region(offset, len));
		t.setText(name);
		String param = "";
		for (int i = 0; i < node.getParams().size() - 1; i++) {
			param += node.getParams().get(i).getString() + ", ";
		}
		if (node.getParams().size() > 0) {
			param += node.getParams().get(node.getParams().size() - 1).getString();
		}
		if ("".equals(param)) {
			param = " ";
		}
		t.setOther("(" + param + ")");
		t.setImage("method");
		stack.peek().add(t);
		offset += len;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (stack.peek().getRegion() == null) {
			offset = source.indexOf("</" + qName, offset) + 2;
		} else {
			offset = source.indexOf(qName, offset);
		}
		stack.pop().setEndRegion(new Region(offset, qName.length()));
		offset += qName.length();
	}

	@Override
	public void endDocument() throws SAXException {
		source = null;
		stack.clear();
	}

	public OutlineTree getTree() {
		return root;
	}

	private String getValue(Attributes attributes) {
		String[] values = { "id", "name", "type", "field" };
		for (int i = 0; i < attributes.getLength(); i++) {
			for (String s : values) {
				if (s.equalsIgnoreCase(attributes.getQName(i))) {
					return "(" + attributes.getValue(attributes.getQName(i)) + ")";
				}
			}
		}
		return "";
	}

	public void parser() throws ParserConfigurationException, SAXException, IOException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
		InputStream is = new ByteArrayInputStream(source.getBytes("utf-8"));
		try {
			parser.parse(is, this);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
