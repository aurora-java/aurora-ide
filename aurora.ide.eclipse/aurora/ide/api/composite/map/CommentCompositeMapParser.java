package aurora.ide.api.composite.map;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeMapParser;
import uncertain.composite.NameProcessor;

public class CommentCompositeMapParser extends CompositeMapParser implements
		LexicalHandler {

	public static final String SAX_NEWlINE = "&#xA;";

	String comment;

	public CommentCompositeMapParser(CompositeLoader composite_loader) {
		super(composite_loader);
		this.composite_loader = composite_loader;
	}

	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {

		// test if this is an xinclude instruction
		if (composite_loader.getSupportXInclude())
			if (localName.equals(INCLUDE_INSTRUCTION) && namespaceURI != null)
				if (namespaceURI.equals(XINCLUDE_URI)) {
					String href_target = atts.getValue(KEY_HREF);
					if (href_target == null)
						throw new SAXException(
								"No 'href' attribute set for an XInclude instruction");
					CompositeMap included;
					try {
						included = getCompositeLoader().load(href_target);
					} catch (IOException ex) {
						throw new SAXException(ex);
					}

					if (current_node == null)
						current_node = included;
					else {
						/*
						 * System.out.println(current_node.getClass());
						 * System.out.println(current_node.getName());
						 */
						current_node.addChild(included);
					}
					return;
				}
		if (name_processor != null)
			localName = name_processor.getElementName(localName);
		CommentCompositeMap node = null;
		if (getCompositeLoader() != null)
			node = (CommentCompositeMap) getCompositeLoader()
					.createCompositeMap((String) uri_mapping.get(namespaceURI),
							namespaceURI, localName);
		else
			node = new CommentCompositeMap(
					(String) uri_mapping.get(namespaceURI), namespaceURI,
					localName);
		addAttribs(node, atts);
		if (comment != null) {
			node.setComment(comment);
			comment = null;
		}

		/*
		 * if(last_locator!=null) node.setLocator(last_locator);
		 */
		if (current_node == null) {
			current_node = node;
		} else {
			current_node.addChild(node);
			push(current_node);
			current_node = node;
		}

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// last_locator = null;
		if (comment != null && current_node instanceof CommentCompositeMap) {
			((CommentCompositeMap) current_node).setEndElementComment(comment);
			comment = null;
		}
		// test if this is an xinclude instruction
		if (getCompositeLoader().getSupportXInclude())
			if (localName.equals(INCLUDE_INSTRUCTION) && uri != null)
				if (uri.equals(XINCLUDE_URI)) {
					return;
				}

		if (node_stack.size() > 0)
			current_node = pop();
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (ch == null)
			return;
		if (0 == length)
			return;
		if (current_node != null) {
			String t = current_node.getText();
			if (t != null)
				t += new String(ch, start, length);
			else
				t = new String(ch, start, length);
			t = handleNewLine(t);
			t = t.replaceAll(SAX_NEWlINE, "\n");
			current_node.setText(t);
		}
	}

	public CompositeMap parseStream(InputStream stream) throws SAXException,
			IOException {

		// using SAX parser shipped with JDK
		SAXParser parser = null;
		try {
			parser = parser_factory.newSAXParser();
		} catch (ParserConfigurationException ex) {
			throw new SAXException("error when creating SAXParser", ex);
		}
		parser.setProperty("http://xml.org/sax/properties/lexical-handler",
				this);

		stream = handleNewLineInAttribute(stream);

		parser.parse(stream, this);

		CompositeMap root = getRoot();
		if (getCompositeLoader().getSaveNamespaceMapping())
			root.setNamespaceMapping(saved_uri_mapping);
		return root;
	}

	private InputStream handleNewLineInAttribute(InputStream stream)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream,
				"UTF-8"));
		String content = "";
		String line = br.readLine();
		while (line != null) {
			content += line + "\r\n";
			line = br.readLine();
		}
		stream = new ByteArrayInputStream(convertNewLine(content, 0).getBytes(
				"UTF-8"));
		return stream;
	}

	public static String convertNewLine(String fileContent, int index) {
		// 匹配双引号间内容
		StringBuilder sb = new StringBuilder(fileContent);
		int leftIndex = sb.indexOf("\"");
		int d = SAX_NEWlINE.length() - 1;
		while (leftIndex != -1) {
			int rightIndex = sb.indexOf("\"", leftIndex + 1);
			if (rightIndex == -1)
				break;
			for (int i = leftIndex + 1; i < rightIndex; i++) {
				if (sb.charAt(i) == '\n') {
					sb.replace(i, i + 1, SAX_NEWlINE);
					i += d;
					rightIndex += d;
				}
			}
			leftIndex = sb.indexOf("\"", rightIndex + 1);
		}
		return sb.toString();
	}

	public static String _convertNewLine(String fileContent, int index) {
		// 匹配双引号间内容
		String pstr = "\"([^\"]*)\"";
		Pattern p = Pattern.compile(pstr);
		String content = fileContent.substring(index);
		Matcher m = p.matcher(content);
		if (m.find()) {
			String text = m.group();
			text = text.replaceAll("\n", SAX_NEWlINE);
			int count = 0;
			int fromIndex = 0;
			while ((fromIndex = text.indexOf(SAX_NEWlINE, fromIndex)) != -1) {
				count++;
				fromIndex = fromIndex + 5;
			}
			fileContent = fileContent.substring(0, m.start() + index) + text
					+ fileContent.substring(m.end() + index);
			index = m.end() + index + 4 * count;
		} else {
			return fileContent;
		}
		return convertNewLine(fileContent, index);
	}

	public void comment(char ch[], int start, int length) throws SAXException {
		if (ch == null)
			return;
		String separator = "-->";
		String now = new String(ch, start, length);
		now = now.replaceAll(SAX_NEWlINE, "\n");
		if (comment != null)
			comment += separator + now;
		else
			comment = now;
	}

	private String handleNewLine(String src) {
		if (src == null)
			return null;
		String result = src.replace("\r", "");
		result = result.replace("\n", "\r\n");
		return result;
	}

	/**
	 * partly supports W3C XInclude specification <xi:include
	 * xmlns:xi="http://www.w3.org/2001/XInclude" href="new_document.xml" />
	 */
	public static final String INCLUDE_INSTRUCTION = "include";
	public static final String XINCLUDE_URI = "http://www.w3.org/2001/XInclude";
	public static final String KEY_HREF = "href";

	CompositeMap current_node = null;

	LinkedList node_stack = new LinkedList();

	// namespace url -> prefix mapping
	Map uri_mapping = new HashMap();

	// save all namespace url -> prefix mapping
	Map saved_uri_mapping;

	// prefix -> namespace mapping
	Map namespace_mapping = new HashMap();

	NameProcessor name_processor;

	CompositeLoader composite_loader;

	// Locator last_locator;

	// boolean support_xinclude = false;

	// the default SAXParserFactory instance
	Locator locator;
	static SAXParserFactory parser_factory = SAXParserFactory.newInstance();
	static {
		try {
			parser_factory.setNamespaceAware(true);
			parser_factory.setValidating(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void push(CompositeMap node) {
		node_stack.addFirst(node);
	}

	CompositeMap pop() {
		CompositeMap node = (CompositeMap) node_stack.getFirst();
		node_stack.removeFirst();
		return node;
	}

	void addAttribs(CompositeMap node, Attributes attribs) {
		// Class nodeCls = node.getClass();
		for (int i = 0; i < attribs.getLength(); i++) {
			String attrib_name = attribs.getQName(i);
			/** @todo Add attribute namespace support */
			// String uri = attribs.getURI(i);
			if (name_processor != null)
				attrib_name = name_processor.getAttributeName(attrib_name);
			node.put(attrib_name, attribs.getValue(i));
		}
	}

	/** handles for SAX */

	public void startDocument() {
		current_node = null;
		// last_locator = null;
		node_stack.clear();
		uri_mapping.clear();
		name_processor = getCompositeLoader().getNameProcessor();
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// do not save empty prefix mapping
		if (prefix == null)
			return;
		if (prefix.length() == 0)
			return;
		uri_mapping.put(uri, prefix);
		namespace_mapping.put(prefix, uri);
		if (getCompositeLoader().getSaveNamespaceMapping()) {
			if (saved_uri_mapping == null)
				saved_uri_mapping = new HashMap();
			saved_uri_mapping.put(uri, prefix);
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		uri_mapping.remove(prefix);
	}

	/** get root CompositeMap parsed */
	public CompositeMap getRoot() {
		return current_node;
	}

	/** get/set CompositeLoader */
	public void setCompositeLoader(CompositeLoader loader) {
		this.composite_loader = loader;
		// this.support_xinclude = loader.getSupportXInclude();
	}

	public CompositeLoader getCompositeLoader() {
		return this.composite_loader;
	}

	public void clear() {
		current_node = null;
		if (node_stack != null)
			node_stack.clear();
		if (uri_mapping != null)
			uri_mapping.clear();
		name_processor = null;
		composite_loader = null;
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
		super.setDocumentLocator(locator);
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	public void endDTD() throws SAXException {
		// TODO Auto-generated method stub

	}

	public void startEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void endEntity(String name) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void startCDATA() throws SAXException {
		// TODO Auto-generated method stub

	}

	public void endCDATA() throws SAXException {
		// TODO Auto-generated method stub

	}

}
