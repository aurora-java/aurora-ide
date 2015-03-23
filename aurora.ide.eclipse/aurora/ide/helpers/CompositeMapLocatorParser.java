/*
 * CompositeMapHandle.java
 *
 * Created on 2002��1��5��, ����2:12
 */

package aurora.ide.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import aurora.ide.api.composite.map.CommentCompositeMap;

import uncertain.composite.CompositeMap;
import uncertain.composite.NameProcessor;

/**
 * 
 * @author jinxiao.lin
 * @version
 */
public class CompositeMapLocatorParser extends DefaultHandler implements
		LexicalHandler {

	private final int lineToCompositeMap = 0;
	private final int compositeMapToLine = 1;
	private int function;
	private CompositeMap targetCompositeMap;
	private int line;
	private boolean serchfinished = false;

	/**
	 * @param composite_loader
	 */
	public CompositeMapLocatorParser() {
		super();
	}

	/**
	 * partly supports W3C XInclude specification <xi:include
	 * xmlns:xi="http://www.w3.org/2001/XInclude" href="new_document.xml" />
	 */
	public static final String INCLUDE_INSTRUCTION = "include";
	public static final String XINCLUDE_URI = "http://www.w3.org/2001/XInclude";
	public static final String KEY_HREF = "href";

	CompositeMap current_node = null;

	LinkedList parentNode_stack = new LinkedList();

	// namespace url -> prefix mapping
	Map uri_mapping = new HashMap();

	// save all namespace url -> prefix mapping
	Map saved_uri_mapping;

	// prefix -> namespace mapping
	Map namespace_mapping = new HashMap();

	NameProcessor name_processor;

	Locator locator;

	String comment;
	// boolean support_xinclude = false;
	// the default SAXParserFactory instance

	private HashMap compositeMapPositions;
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
		parentNode_stack.addFirst(node);
	}

	CompositeMap pop() {
		CompositeMap node = (CompositeMap) parentNode_stack.getFirst();
		parentNode_stack.removeFirst();
		return node;
	}

	void addAttribs(CompositeMap node, Attributes attribs) {
		// Class nodeCls = node.getClass();
		for (int i = 0; i < attribs.getLength(); i++) {
			String attrib_name = attribs.getQName(i);
			if (name_processor != null)
				attrib_name = name_processor.getAttributeName(attrib_name);
			node.put(attrib_name, attribs.getValue(i));
		}
	}

	/** handles for SAX */

	public void startDocument() {
		current_node = null;
		parentNode_stack.clear();
		uri_mapping.clear();
	}

	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {

		int lineNumber = locator.getLineNumber() - 1;
		if (name_processor != null)
			localName = name_processor.getElementName(localName);
		CommentCompositeMap node = new CommentCompositeMap(
				(String) uri_mapping.get(namespaceURI), namespaceURI, localName);
		node.setStartPoint(locator.getLineNumber(), locator.getColumnNumber());
		addAttribs(node, atts);
		if (comment != null) {
			node.setComment(comment);
			comment = null;
		}
		if (!serchfinished && function == lineToCompositeMap) {
			if (lineNumber == line) {
				targetCompositeMap = node;
				serchfinished = true;
			} else if (lineNumber > line) {
				targetCompositeMap = current_node;
				serchfinished = true;
			}
		}
		if (current_node != null) {
			push(current_node);
			current_node.addChild(node);
		}
		current_node = node;

		if (function == compositeMapToLine)
			compositeMapPositions.put(current_node, new Integer(lineNumber));
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (comment != null) {
			((CommentCompositeMap) current_node).setEndElementComment(comment);
			comment = null;
		}
		((CommentCompositeMap) current_node).setEndPoint(
				locator.getLineNumber(), locator.getColumnNumber());
		int lineNumber = locator.getLineNumber() - 1;
		if (!serchfinished && function == lineToCompositeMap) {
			if (lineNumber >= line) {
				targetCompositeMap = current_node;
				serchfinished = true;
			}
		}
		if (parentNode_stack.size() > 0) {
			current_node = pop();
		}
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
		// if( getCompositeLoader().getSaveNamespaceMapping()){
		if (saved_uri_mapping == null)
			saved_uri_mapping = new HashMap();
		saved_uri_mapping.put(uri, prefix);
		// }
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		uri_mapping.remove(prefix);
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
			current_node.setText(t);
		}
	}

	/** get root CompositeMap parsed */
	public CompositeMap getRoot() {
		return current_node;
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
		parser.parse(stream, this);
		CompositeMap root = getRoot();
		root.setNamespaceMapping(saved_uri_mapping);
		return root;
	}

	public int LocateCompositeMapLine(InputStream stream,
			CompositeMap targetCompositeMap) throws SAXException, IOException {
		function = compositeMapToLine;
		this.targetCompositeMap = targetCompositeMap;
		compositeMapPositions = new HashMap();
		parseStream(stream);
		Object lineOBject = getLineObject(compositeMapPositions,
				targetCompositeMap);
		if (lineOBject != null) {
			line = ((Integer) lineOBject).intValue();
		}
		return line;
	}

	private Object getLineObject(HashMap map, CompositeMap object) {
		// return compositeMapPositions.get(targetCompositeMap);
		if (map == null || object == null)
			return null;
		Set enterSet = map.keySet();
		if (enterSet == null)
			return null;
		for (Iterator it = enterSet.iterator(); it.hasNext();) {
			Object childObject = it.next();
			Object childValue = map.get(childObject);
			CompositeMap child = (CompositeMap) childObject;
			if (object.equals(child)) {
				return childValue;
			}
		}
		return null;
	}

	public CompositeMap getCompositeMapFromLine(InputStream stream, int line)
			throws SAXException, IOException {
		function = lineToCompositeMap;
		this.line = line;
		parseStream(stream);
		CompositeMap root = new CommentCompositeMap("root");
		root.addChild(getRoot());
		return targetCompositeMap;
	}

	public void clear() {
		current_node = null;
		if (parentNode_stack != null)
			parentNode_stack.clear();
		if (uri_mapping != null)
			uri_mapping.clear();
		name_processor = null;
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
		super.setDocumentLocator(locator);
	}

	public void comment(char ch[], int start, int length) throws SAXException {
		if (ch == null)
			return;
		String separator = "-->";
		String now = new String(ch, start, length);
		if (comment != null)
			comment += separator + now;
		else
			comment = now;
	}

	private String handleNewLine(String src) {
		if (src == null)
			return null;
		String result = src.replaceAll("\r", "");
		result = result.replaceAll("\n", "\r\n");
		return result;
	}

	public void endCDATA() throws SAXException {
	}

	public void endDTD() throws SAXException {
	}

	public void endEntity(String arg0) throws SAXException {
	}

	public void startCDATA() throws SAXException {
	}

	public void startDTD(String arg0, String arg1, String arg2)
			throws SAXException {
	}

	public void startEntity(String arg0) throws SAXException {
	}

	public void setUri_mapping(Map uri_mapping) {
		this.uri_mapping = uri_mapping;
	}
}