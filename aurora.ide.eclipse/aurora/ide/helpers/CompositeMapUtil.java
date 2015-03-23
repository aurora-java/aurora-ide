/**
 * 
 */
package aurora.ide.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.QualifiedName;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.IType;
import uncertain.schema.Namespace;
import uncertain.schema.Schema;
import uncertain.schema.SchemaManager;
import aurora.ide.api.composite.map.Comment;
import aurora.ide.api.composite.map.CommentCompositeLoader;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentReader;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.builder.BuildContext;
import aurora.ide.search.cache.CacheManager;

public class CompositeMapUtil {

	/**
	 * @param args
	 */
	private CompositeMapUtil instance;

	private CompositeMapUtil() {

	}

	public CompositeMapUtil getInstance() {
		if (instance != null)
			return instance;
		else {
			instance = new CompositeMapUtil();
			return instance;
		}

	}

	public static String getContextFullName(CompositeMap context,
			QualifiedName qn) {
		String text = null;
		String prefix = getContextPrefix(context, qn);
		String localName = qn.getLocalName();
		if (prefix != null)
			text = prefix + ":" + localName;
		else
			text = localName;
		return text;
	}

	public static String getContextPrefix(CompositeMap context, QualifiedName qn) {
		if (qn == null)
			return null;
		String prefix = getContextPrefix(context, qn.getNameSpace());
		if (prefix == null) {
			prefix = qn.getPrefix();
		}
		return prefix;
	}

	public static String getContextPrefix(CompositeMap context, String uri) {
		if (uri == null || context == null) {
			return null;
		}
		Map prefix_mapping = CompositeUtil.getPrefixMapping(context);
		Object uri_ot = prefix_mapping.get(uri);
		if (uri_ot != null)
			return (String) uri_ot;
		else
			return null;
	}

	public static CompositeMap addElement(CompositeMap parent,
			QualifiedName childQN) {
		if (parent == null || childQN == null)
			return null;
		String prefix = getContextPrefix(parent, childQN);
		CompositeMap child = new CommentCompositeMap(prefix,
				childQN.getNameSpace(), childQN.getLocalName());
		parent.addChild(child);
		addArrayNode(parent);
		return child;
	}

	public static boolean addElement(CompositeMap node, CompositeMap childNode) {
		if (node == null || childNode == null)
			return false;
		node.addChild(childNode);
		addArrayNode(node);
		return true;
	}

	public static void addArrayNode(CompositeMap parent) {
		// Element element = LoadSchemaManager.getSchemaManager().getElement(
		// parent);
		Element element = CompositeMapUtil.getElement(parent);
		if (element != null && element.isArray()) {
			QualifiedName qName = parent.getQName();
			if (CompositeUtil.findChild(parent.getParent(), qName) == null) {
				parent.getParent().addChild(parent);
			}
		}
	}

	public static void addElementArray(CompositeMap parentCM) {
		// Element element = LoadSchemaManager.getSchemaManager().getElement(
		// parentCM);
		Element element = CompositeMapUtil.getElement(parentCM);
		if (element != null) {
			List arrays = element.getAllArrays();
			if (arrays != null) {
				Iterator ite = arrays.iterator();
				while (ite.hasNext()) {
					Array array = (Array) ite.next();
					String name = array.getLocalName();
					CompositeMap newCM = new CommentCompositeMap(
							parentCM.getPrefix(), parentCM.getNamespaceURI(),
							name);
					parentCM.addChild(newCM);
				}
			}
		}
	}

	public static List getAvailableChildElements(CompositeMap parent) {
		// Element element = LoadSchemaManager.getSchemaManager().getElement(
		// parent);
		Element element = CompositeMapUtil.getElement(parent);
		if (element == null)
			return null;
		List childElements = new LinkedList();
		// 判断及节点是否是数组
		if (element.isArray()) {
			IType type = element.getElementType();
			// 如果数组成员类型是元素
			if (type instanceof Element) {
				Element arrayType = LoadSchemaManager.getSchemaManager()
						.getElement(type.getQName());
				childElements.add(arrayType);
			}// 判断数组成员类型是否是基类
			else if (type instanceof ComplexType) {
				childElements.addAll(LoadSchemaManager.getSchemaManager()
						.getElementsOfType(type));
			}
		}
		// 如果节点是元素
		else {
			childElements = getChildElements(parent);
		}
		if (childElements != null)
			Collections.sort(childElements);
		return childElements;
	}

	private static List getChildElements(CompositeMap parent) {
		// Element element = LoadSchemaManager.getSchemaManager().getElement(
		// parent);
		Element element = CompositeMapUtil.getElement(parent);

		Set schemaChilds = getSchemaChilds(element,
				LoadSchemaManager.getSchemaManager());
		List availableChilds = new ArrayList();

		if (schemaChilds != null) {
			Iterator ite = schemaChilds.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				final QualifiedName childQN = ele.getQName();
				if (ele.getMaxOccurs() == null) {
					availableChilds.add(ele);
					continue;
				}
				int maxOccurs = Integer.valueOf(ele.getMaxOccurs()).intValue();
				int nowOccurs = getCountOfChildElement(parent, childQN);
				if (nowOccurs < maxOccurs) {
					availableChilds.add(ele);
				}
			}
		}
		return availableChilds;
	}

	public static Set getSchemaChilds(Element element, ISchemaManager manager) {
		Set childs = new HashSet();
		Set childElements = element.getChilds();
		if (childElements == null) {
			return childs;
		}
		for (Iterator cit = childElements.iterator(); cit != null
				&& cit.hasNext();) {
			Object node = cit.next();
			if (!(node instanceof ComplexType))
				continue;
			ComplexType context = (ComplexType) node;
			ComplexType original = manager.getComplexType(context.getQName());
			if (original instanceof Element) {
				Element new_name = (Element) context;
				childs.add(new_name);
			} else {
				childs.addAll(manager.getElementsOfType(original));
			}
		}
		List complexTypes = element.getAllExtendedTypes();
		if (complexTypes == null)
			return childs;
		for (Iterator cit = complexTypes.iterator(); cit != null
				&& cit.hasNext();) {
			ComplexType ct = (ComplexType) cit.next();
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				childs.addAll(getSchemaChilds(new_name, manager));
			}
		}
		return childs;
	}

	public static int getCountOfChildElement(CompositeMap parent,
			QualifiedName childQN) {
		List childs = parent.getChildsNotNull();
		int count = 0;
		Iterator it = childs.iterator();
		for (; it.hasNext();) {
			CompositeMap node = (CompositeMap) it.next();
			if (node.getQName().equals(childQN)) {
				count++;
			}
		}
		return count;
	}

	public static boolean validNextNodeLegalWithAction(CompositeMap parent,
			CompositeMap child) {
		if (!validNextNodeLegal(parent, child)) {
			String warning = "";
			if (parent == null) {
				warning = LocaleMessage.getString("parent.element.is.null");
			} else if (child == null) {
				warning = LocaleMessage.getString("child.element.is.null");
			} else {
				warning = " " + parent.getQName().getLocalName() + " "
						+ LocaleMessage.getString("undefined")
						+ child.getQName().getLocalName() + " "
						+ LocaleMessage.getString("child.element");
			}
			DialogUtil.showWarningMessageBox(warning);
			return false;
		}
		return true;
	}

	public static boolean validNextNodeLegal(CompositeMap parent,
			CompositeMap child) {
		if (parent == null || child == null)
			return false;
		// Element parentElement = LoadSchemaManager.getSchemaManager()
		// .getElement(parent);
		Element parentElement = CompositeMapUtil.getElement(parent);
		return validNextNodeLegal(parentElement, child.getQName());
	}

	public static boolean validNextNodeLegal(Element parent,
			QualifiedName childQN) {
		if (parent == null || childQN == null)
			return false;
		if (parent.isArray()) {
			QualifiedName array = parent.getElementType().getQName();
			if (childQN.equals(array)) {
				return true;
			}
		}
		List childElements = parent.getChildElements(LoadSchemaManager
				.getSchemaManager());
		if (childElements != null) {
			Iterator ite = childElements.iterator();
			while (ite.hasNext()) {
				Object object = ite.next();
				if (!(object instanceof Element))
					continue;
				Element ele = (Element) object;
				if (childQN.equals(ele.getQName()))
					return true;
			}
		}
		// 列示所有数组子节点
		List arrays = parent.getAllArrays();
		if (arrays != null) {
			Iterator ite = arrays.iterator();
			while (ite.hasNext()) {
				Array array = (Array) ite.next();
				if (childQN.equals(array.getQName()))
					return true;
			}
		}
		return false;
	}

	// TODO 未使用
	public Set getMaxOcuss(Element element, SchemaManager manager) {
		Set allChildElements = new HashSet();
		Set childElements = element.getChilds();
		for (Iterator cit = childElements.iterator(); cit != null
				&& cit.hasNext();) {
			Object node = cit.next();
			if (!(node instanceof ComplexType))
				continue;
			ComplexType ct = (ComplexType) node;
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allChildElements.add(new_name);
			} else {
				allChildElements.addAll(manager.getElementsOfType(ct));
			}
		}
		List complexTypes = element.getAllExtendedTypes();
		if (complexTypes == null)
			return allChildElements;
		for (Iterator cit = complexTypes.iterator(); cit != null
				&& cit.hasNext();) {
			ComplexType ct = (ComplexType) cit.next();
			if (ct instanceof Element) {
				Element new_name = (Element) ct;
				allChildElements.addAll(getMaxOcuss(new_name, manager));
			}
		}
		return allChildElements;
	}

	public static String getValueIgnoreCase(CompositeMap cMap, String name) {
		Set keySet = cMap.keySet();
		for (Object object : keySet) {
			if (object instanceof String
					&& ((String) object).equalsIgnoreCase(name)) {
				return cMap.getString(object);
			}
		}
		return null;
	}

	public static String getValueIgnoreCase(Attribute a, CompositeMap cMap) {
		String name = a.getName();
		return getValueIgnoreCase(cMap, name);
	}

	public static String getValueIgnoreCase(Attribute a, BuildContext bc) {
		return CompositeMapUtil.getValueIgnoreCase(a, bc.map);
	}

	public static Namespace getQualifiedName(CompositeMap root, String prefix) {
		Map namespace_mapping = CompositeUtil.getPrefixMapping(root);
		Schema schema = new Schema();
		Namespace[] ns = getNameSpaces(namespace_mapping);
		schema.addNameSpaces(ns);
		Namespace nameSpace = schema.getNamespace(prefix);
		return nameSpace;
	}

	private static Namespace[] getNameSpaces(Map namespaceToPrefix) {
		if (namespaceToPrefix == null)
			return null;

		Namespace[] namespaces = new Namespace[namespaceToPrefix.keySet()
				.size()];
		Iterator elements = namespaceToPrefix.keySet().iterator();
		int i = 0;
		while (elements.hasNext()) {
			Object element = elements.next();
			Namespace namespace = new Namespace();
			namespace.setPrefix(namespaceToPrefix.get(element).toString());
			namespace.setUrl(element.toString());
			namespaces[i] = namespace;
		}
		return namespaces;
	}

	public static void collectAttribueValues(Set set, String attribueName,
			CompositeMap root) {
		String attribueValue = root.getString(attribueName);
		if (attribueValue != null) {
			set.add(attribueValue);
		}
		List childList = root.getChilds();
		if (childList != null) {
			Iterator it = childList.iterator();
			for (; it.hasNext();) {
				CompositeMap child = (CompositeMap) it.next();
				collectAttribueValues(set, attribueName, child);
			}
		}
	}

	public static List getArrayAttrs(CompositeMap arrayData)
			throws ApplicationException {
		if (arrayData == null)
			throw new ApplicationException("CompositeMap data can not be null!");
		// Element element = LoadSchemaManager.getSchemaManager().getElement(
		// arrayData);
		Element element = CompositeMapUtil.getElement(arrayData);
		if (element == null)
			throw new ApplicationException("Can't get element schema from "
					+ arrayData.toXML());
		if (!(element instanceof Array))
			throw new ApplicationException("Type " + element.getQName()
					+ " is not array");
		Array array = (Array) element;
		IType type = array.getElementType();
		if (type == null)
			throw new ApplicationException("Can't get array type from "
					+ array.getQName());
		if (!(type instanceof ComplexType))
			throw new ApplicationException("Type " + type.getQName()
					+ " is not ComplexType");
		ComplexType type_element = (ComplexType) type;
		List attrib_list = type_element.getAllAttributes();
		return attrib_list;
	}

	public static String[] getArrayAttrNames(CompositeMap arrayData)
			throws ApplicationException {
		List attrib_list = getArrayAttrs(arrayData);
		if (attrib_list == null)
			return null;
		String[] column_index = new String[attrib_list.size()];
		int id = 0;
		for (Iterator it = attrib_list.iterator(); it.hasNext();) {
			Attribute attrib = (Attribute) it.next();
			column_index[id++] = attrib.getLocalName();
		}
		return column_index;
	}

	public static int locateNode(String content, CompositeMap node)
			throws ApplicationException {
		int line = -1;
		if (node == null || content == null)
			return -1;
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			line = parser.LocateCompositeMapLine(is, node);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查内容格式" + content + "是否正确.", e);
		} catch (IOException e) {
			throw new ApplicationException("请检查内容格式" + content + "是否正确.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					DialogUtil.logErrorException(e);
				}
			}
		}
		return line;
	}

	public static CompositeMap loaderFromString(String content)
			throws ApplicationException {
		if (content == null)
			return null;
		CompositeMap root = null;
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
			root = parser.parseStream(is);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查内容格式是否正确.", e);
		} catch (IOException e) {
			throw new ApplicationException("请检查内容格式是否正确.", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					DialogUtil.logErrorException(e);
				}
			}
		}
		return root;
	}

	public static String getFullContent(CompositeMap data) {
		if (data == null)
			return null;
		return AuroraResourceUtil.xml_decl
				+ CommentXMLOutputter.defaultInstance().toXML(data, true);
	}

	/**
	 * 
	 * fix Bug : <bm:features><bm:feature/></bm:features>
	 * java.lang.IllegalArgumentException: Specified QName
	 * {{http://www.aurora-framework.org/schema/bm}feature} is not Element but
	 * uncertain.schema.ComplexType
	 * 
	 */
	public static Element getElement(CompositeMap map) {
		ISchemaManager schemaManager = LoadSchemaManager.getSchemaManager();
		try {
			return schemaManager.getElement(map);
		} catch (IllegalArgumentException e) {
			LogUtil.getInstance().logError(e.getMessage(), e);
		}

		return null;
	}

	public static Comment getFileComment(IFile file) {
		try {
			CompositeMap hostMap = CacheManager.getCompositeMap(file);
			if (hostMap instanceof CommentCompositeMap) {
				String comment = ((CommentCompositeMap) hostMap).getComment();
				CommentReader cr = new CommentReader(comment);
				Comment read = cr.read();
				return read;
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		} catch (ApplicationException e) {
			DialogUtil.logErrorException(e);
			e.printStackTrace();
		}
		return null;
	}
	public static CompositeMap loadFile(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			CompositeLoader parser = new CommentCompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			return rootMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public static CompositeMap loadFile(IFile file) {
		InputStream is = null;
		try {
			is = file.getContents(false);

			CompositeLoader parser = new CommentCompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			return rootMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	public static CompositeMap loadFromStream(InputStream is) {
		try {
			CompositeLoader parser = new CommentCompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			return rootMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}

