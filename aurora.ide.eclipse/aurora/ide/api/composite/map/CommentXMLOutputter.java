package aurora.ide.api.composite.map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.XMLOutputter;
import uncertain.util.XMLWritter;

public class CommentXMLOutputter extends XMLOutputter {

	public static final String CDATA_END = "]]>";
	public static final String CDATA_BEGIN = "<![CDATA[";
	public static final String DEFAULT_INDENT = "    ";

	/** whether print new line for each XML part */
	boolean mUseNewLine;

	/** space predicates each tag */
	String mIndentString;

	/** whether create CDATA tag for text */
	boolean mGenerateCdata = true;

	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	public static XMLOutputter default_inst = new CommentXMLOutputter(
			DEFAULT_INDENT, true);

	public static XMLOutputter defaultInstance() {
		return default_inst;
	}

	public CommentXMLOutputter() {
		mIndentString = DEFAULT_INDENT;
		mUseNewLine = true;
	}

	/** Creates new XMLOutputter */
	public CommentXMLOutputter(String _indent, boolean _new_line) {
		mIndentString = _indent;
		mUseNewLine = _new_line;
	}

	String getIndentString(int level) {
		StringBuilder pre_indent = new StringBuilder();
		if (mIndentString != null)
			for (int i = 0; i < level; i++)
				pre_indent.append(mIndentString);
		return pre_indent.toString();
	}

	static void getAttributeXML(Map map, StringBuilder attribs) {
		Iterator it = map.entrySet().iterator();
		HashMap strings = new HashMap();
		List keyList = new ArrayList();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				strings.put(key.toString(), value.toString());
				keyList.add(key.toString());
			}
		}
		Object[] keys = keyList.toArray();
		// Arrays.sort(keys);
		keys = sortKey(keys);
		for (int i = 0; i < keys.length; i++) {
			attribs.append(" ").append(
					XMLWritter.getAttrib(keys[i].toString(),
							strings.get(keys[i]).toString()));
		}
		// Iterator it = map.entrySet().iterator();
		// while(it.hasNext()){
		// Map.Entry entry = (Map.Entry)it.next();
		// Object key = entry.getKey();
		// Object value = entry.getValue();
		// if( value != null)
		// attribs.append(" ").append(XMLWritter.getAttrib(key.toString(),
		// value.toString() ) );
		// }
	}

	static private Object[] sortKey(Object[] keys) {
		if (keys == null)
			return null;
		List keyColumnList = new ArrayList();
		keyColumnList.add("id");
		keyColumnList.add("name");
		Arrays.sort(keys);
		List list = new LinkedList();
		for (int i = 0; i < keys.length; i++) {
			String keyStr = keys[i].toString();
			if (keyColumnList.contains(keyStr)) {
				list.add(0, keyStr);
			} else {
				list.add(keyStr);
			}
		}
		return list.toArray();

	}

	void getChildXML(int level, List childs, StringBuilder buf, Map namespaces,
			Map prefix_mapping) {
		if (childs == null)
			return;
		Iterator it = childs.iterator();
		while (it.hasNext()) {
			CompositeMap map = (CompositeMap) it.next();
			buf.append(toXMLWithPrefixMapping(level, map, namespaces,
					prefix_mapping));
		}
	}

	static Map addRef(Map namespaces, String uri, CompositeMap map) {
		if (uri == null)
			return namespaces;
		if (namespaces == null)
			namespaces = new HashMap();
		Integer new_count;
		Integer count = (Integer) namespaces.get(map.getNamespaceURI());
		if (count != null) {
			new_count = new Integer(count.intValue() + 1);
		} else
			new_count = new Integer(1);
		namespaces.put(((CommentCompositeMap) map).namespace_uri, new_count);
		return namespaces;
	}

	static void subRef(Map map, String uri) {
		if (uri == null)
			return;
		Integer count = (Integer) map.get(uri);
		if (count != null) {
			int value = count.intValue() - 1;
			if (value <= 0)
				map.remove(uri);
			else
				map.put(uri, new Integer(value));
		}
	}

	/**
	 * return XML form of map CompositeMap, object stored in Map will be added
	 * as attributes by calling Object.toString(), childs will be added as sub
	 * elements
	 * 
	 * @return string of XML
	 */

	public String toXML(CompositeMap map) {
		return toXML(map, false);
	}

	public String toXML(CompositeMap map, boolean namespace_in_root) {
		if (namespace_in_root) {
			Map prefix_mapping = CompositeUtil.getPrefixMapping(map);
			return toXMLWithPrefixMapping(0, map, null, prefix_mapping);
		} else
			return toXMLWithPrefixMapping(0, map, null, null);
	}

	/**
	 * Append xml namespace declare to StringBuilder
	 * 
	 * @param buf
	 *            Target StringBuilder
	 * @param prefix_mapping
	 *            namespace url -> prefix
	 * @return processed buf
	 */
	static StringBuilder appendNamespace(StringBuilder buf, Map prefix_mapping) {
		Iterator it = prefix_mapping.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String namespace = (String) entry.getKey();
			String prefix = (String) entry.getValue();
			buf.append(" xmlns:").append(prefix).append("=\"")
					.append(namespace).append("\"");
		}
		return buf;
	}

	/**
	 * internal method
	 * 
	 * @param namespaces
	 *            a Map of existing namespace: namespace -> Integer of ref count
	 * @param prefix_mapping
	 *            a Map of namespace -> prefix mapping
	 * @return string of XML
	 */
	String toXMLWithPrefixMapping(int level, CompositeMap map, Map namespaces,
			Map prefix_mapping) {

		StringBuilder attribs = new StringBuilder();
		StringBuilder childs = new StringBuilder();
		StringBuilder xml = new StringBuilder();
		String indent_str = getIndentString(level);
		String namespace_uri = map.getNamespaceURI();
		StringBuilder xmlns_declare = null;

		boolean need_new_line_local = mUseNewLine;

		if (prefix_mapping == null) {
			if (namespace_uri != null) {
				boolean uri_exists = false;
				if (namespaces != null) {
					uri_exists = (namespaces.get(namespace_uri) != null);
				}
				if (!uri_exists) {
					String xmlns = "xmlns";
					if (map.getPrefix() != null)
						xmlns = "xmlns:" + map.getPrefix();
					attribs.append(" ").append(
							XMLWritter.getAttrib(xmlns, namespace_uri));
				}
				namespaces = addRef(namespaces, namespace_uri, map);
			}
		}
		String comment = getComment(level, map);
		if (comment != null) {
			xml.append(comment);
		}
		getAttributeXML(map, attribs);

		String endElementComment = getEndElementComment(level + 1, map);
		if (map.getChilds() == null || map.getChilds().size() == 0) {
			if (endElementComment != null) {
				childs.append(LINE_SEPARATOR).append(endElementComment);
			}
			if (map.getText() != null) {
				need_new_line_local = false;
				if (mGenerateCdata)
					childs.append(CDATA_BEGIN).append(map.getText())
							.append(CDATA_END);
				else
					childs.append(XMLWritter.escape(map.getText()));
			}
		} else {
			getChildXML(level + 1, map.getChilds(), childs, namespaces,
					prefix_mapping);
			if (endElementComment != null) {
				childs.append(endElementComment).append(LINE_SEPARATOR);
			}
		}

		if (prefix_mapping == null) {
			subRef(namespaces, namespace_uri);
		}

		String elm = null;
		if (prefix_mapping == null) {
			elm = map.getRawName();
		} else {
			elm = map.getName();
			if (namespace_uri != null) {
				String prefix = (String) prefix_mapping.get(namespace_uri);
				elm = prefix + ":" + elm;
			}
			if (level == 0) {
				xmlns_declare = new StringBuilder();
				appendNamespace(xmlns_declare, prefix_mapping);
			}
		}
		xml.append(indent_str).append('<').append(elm);
		if (xmlns_declare != null)
			xml.append(xmlns_declare);
		xml.append(attribs);
		if (childs.length() > 0) {
			xml.append('>');
			if (need_new_line_local)
				xml.append(LINE_SEPARATOR);
			xml.append(childs);
			if (need_new_line_local)
				xml.append(indent_str);
			xml.append(XMLWritter.endTag(elm));
		} else
			xml.append("/>");
		if (mUseNewLine)
			xml.append(LINE_SEPARATOR);
		return xml.toString();
	}

	private String getComment(int level, CompositeMap map) {
		if (map instanceof CommentCompositeMap) {
			StringBuilder xml = new StringBuilder();
			String indent_str = getIndentString(level);
			if (((CommentCompositeMap) map).getComment() != null) {
				String[] comms = ((CommentCompositeMap) map).getComment()
						.split("-->");
				for (int i = 0; i < comms.length; i++) {
					String comm = comms[i];
					String fullComm = "<!--" + comm + "-->";
					xml.append(indent_str).append(fullComm)
							.append(LINE_SEPARATOR);
				}
			} else {
				return null;
			}
			return xml.toString();
		}
		return "";
	}

	private String getEndElementComment(int level, CompositeMap map) {
		if (map instanceof CommentCompositeMap) {
			StringBuilder xml = new StringBuilder();
			String indent_str = getIndentString(level);
			if (((CommentCompositeMap) map).getEndElementComment() != null) {
				String[] comms = ((CommentCompositeMap) map)
						.getEndElementComment().split("-->");
				for (int i = 0; i < comms.length; i++) {
					String comm = comms[i];
					String fullComm = "<!--" + comm + "-->";
					if (i > 0) {
						xml.append(LINE_SEPARATOR);
					}
					xml.append(indent_str).append(fullComm);

				}
			} else {
				return null;
			}
			return xml.toString();
		}
		return "";
	}

	public static void saveToFile(File target_file, CompositeMap map)
			throws IOException {
		saveToFile(target_file, map, "UTF-8");
	}

	public static void saveToFile(File target_file, CompositeMap map,
			String encoding) throws IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(target_file);
			String xml_decl = "<?xml version=\"1.0\" encoding=\"" + encoding
					+ "\"?>\n";
			os.write(xml_decl.getBytes());
			String content = defaultInstance().toXML(map, true);
			os.write(content.getBytes(encoding));
			os.flush();
		} finally {
			if (os != null)
				os.close();
		}
	}

	public boolean isUseNewLine() {
		return mUseNewLine;
	}

	public void setUseNewLine(boolean useNewLine) {
		this.mUseNewLine = useNewLine;
	}

	public String getIndentString() {
		return mIndentString;
	}

	public void setIndentString(String indentString) {
		this.mIndentString = indentString;
	}

	public boolean isGenerateCdata() {
		return mGenerateCdata;
	}

	public void setGenerateCdata(boolean generateCdata) {
		this.mGenerateCdata = generateCdata;
	}

}
