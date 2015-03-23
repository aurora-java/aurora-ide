package aurora.ide.search.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import aurora.ide.helpers.CompositeMapLocatorParser;

public class NoNamespaceMapParser extends CompositeMapLocatorParser {

	private Map namespaceMapping;

	public NoNamespaceMapParser(Map namespaceMapping) {
		this.namespaceMapping = namespaceMapping;
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {
		if (null != this.namespaceMapping && "".equals(namespaceURI)
				&& "".equals(localName)) {
			String[] prefix = getPrefix(rawName);
			if (prefix != null) {
				String namespace = "";
				for (Iterator iterator = namespaceMapping.keySet().iterator(); iterator
						.hasNext();) {
					Object key = iterator.next();
					if (prefix[0].equals(this.namespaceMapping.get(key))) {
						namespace = (String) key;
						break;
					}
				}
				super.startElement(namespace, prefix[1], rawName, atts);
				return;
			}
		}
		super.startElement(namespaceURI, rawName, rawName, atts);
	}

	@Override
	public void startDocument() {
		super.startDocument();
		if (null != namespaceMapping)
			this.setUri_mapping(namespaceMapping);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		super.endElement(uri, localName, qName);
	}

	private String[] getPrefix(String rawName) {
		if (rawName != null) {
			String[] split = rawName.split(":");
			if (split.length == 2) {
				return split;
			}
		}

		return null;
	}

	@Override
	public CompositeMap parseStream(InputStream stream) throws SAXException,
			IOException {
		SAXParserFactory parser_factory = SAXParserFactory.newInstance();
		parser_factory.setNamespaceAware(false);
		parser_factory.setValidating(false);

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
		root.setNamespaceMapping(namespaceMapping);
		return root;

	}

}
