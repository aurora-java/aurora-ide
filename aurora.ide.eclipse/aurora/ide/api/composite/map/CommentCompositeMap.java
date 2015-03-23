package aurora.ide.api.composite.map;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import uncertain.composite.CompositeAccessor;
import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.composite.TextParser;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

public class CommentCompositeMap extends CompositeMap {
	/**
	 * 
	 */
	private static final long serialVersionUID = 152040602291601078L;

	protected String globalComment;

	protected String comment;

	protected String endElementComment;

	/**
	 * Creates new CompositeMap
	 */
	public CommentCompositeMap() {
		super(DEFAULT_HASHMAP_SIZE);
	}

	/**
	 * create a CompositeMap with name
	 * 
	 * @param _name
	 *            name of this map
	 */
	public CommentCompositeMap(String _name) {
		super(DEFAULT_HASHMAP_SIZE);
		setName(_name);
	}

	/**
	 * create a CompositeMap with prefix, namespace uri and name for example,
	 * 
	 * <PRE>
	 * <bo:Employee xmlns:bo="http://someurl"/>
	 * </PRE>
	 * 
	 * here prefix="bo", namespace_uri = "http://someurl", name="Employee"
	 * 
	 * @param _prefix
	 *            namespace prefix
	 * @param _uri
	 *            namespace uri
	 * @param _name
	 *            name of this map
	 */
	public CommentCompositeMap(String _prefix, String _uri, String _name) {
		super(DEFAULT_HASHMAP_SIZE);
		setName(_name);
		setPrefix(_prefix);
		setNameSpaceURI(_uri);
	}

	public CommentCompositeMap(int size) {
		super(size);
	}

	/**
	 * same as HashMap( int, float)
	 * 
	 * @see java.util.HashMap
	 */
	public CommentCompositeMap(int size, float load_factor) {
		super(size, load_factor);
	}

	/** constructs from another composite map */
	public CommentCompositeMap(CompositeMap another) {
		this(another.getPrefix(), another.getNamespaceURI(), another.getName());
		copy(another);
	}

	public CommentCompositeMap(String name, Map map) {
		super();
		setName(name);
		putAll(map);
	}

	/** gets comment in XML document */
	public String getComment() {
		return comment;
	}

	/** sets comment ( comment section in XML document ) */
	public void setComment(String t) {
		comment = t;
	}

	/** gets comment in XML document */
	public String getEndElementComment() {
		return endElementComment;
	}

	/** sets comment ( comment section in XML document ) */
	public void setEndElementComment(String t) {
		endElementComment = t;
	}

	public void setStartPoint(int line, int column) {
		getLocationNotNull().setStartPoint(line, column);
	}

	public void setEndPoint(int line, int column) {
		getLocationNotNull().setEndPoint(line, column);
	}

	public boolean equals(Object o) {
		// if (o instanceof CompositeMap) {
		// CompositeMap cm = ((CompositeMap) o);
		// if (this.toXML().equals(cm.toXML())) {
		// return true;
		// }
		// }
		// return false;
		if ((o instanceof CompositeMap) == false) {
			return false;
		}
		if (false == super.equals(o)) {
			return false;
		}
		CompositeMap cm = ((CompositeMap) o);
		if (false == eq(this.getQName(), cm.getQName())) {
			return false;
		}
		if (cm instanceof CommentCompositeMap
				&& false == eq(this.getComment(),
						((CommentCompositeMap) cm).getComment())) {
			return false;
		}
		if (false == eq(this.getText(), cm.getText())) {
			return false;
		}

		List myChilds = this.getChilds();
		List tChilds = cm.getChilds();
		if (myChilds == null)
			return tChilds == null;
		else if (tChilds == null)
			return false;

		if (myChilds.size() != tChilds.size()) {
			return false;
		}
		return myChilds.equals(tChilds);

		// if (o instanceof CompositeMap) {
		// CompositeMap cm = ((CompositeMap) o);
		// if (this.toXML().equals(cm.toXML()) && !super.equals(o)) {
		// System.out.println();
		// super.equals(o);
		// return true;
		// }
		// if (!this.toXML().equals(cm.toXML()) && super.equals(o)) {
		// System.out.println();
		// super.equals(o);
		// return false;
		// }
		// }
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public String toXML() {
		return CommentXMLOutputter.defaultInstance().toXML(this);
	}

	public static final int DEFAULT_HASHMAP_SIZE = 30;

	/** name of this map */
	protected String name;
	protected String prefix;
	protected String namespace_uri;

	protected String text;

	protected CompositeMap parent;

	/**
	 * list of childs, each child is also a CompositeMap
	 */
	protected List childs;

	/** source file where this CompositeMap is parsed from */
	protected File source;
	// protected URL source_url;

	/** Map<url,prefix> of namespace declare */
	protected Map namespace_mapping;

	Location location;

	public void addChilds(Collection another) {
		if (another == null)
			return;
		Iterator it = another.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof CompositeMap) {
				CompositeMap child = new CompositeMap((CompositeMap) obj);
				addChild(child);
			}
		}
	}

	/**
	 * Replace a child CompositeMap with a new CompositeMap
	 * 
	 * @param child
	 *            Existing child to replace. It might be retrieved earlier by
	 *            other methods such as getChild().
	 * @param new_child
	 *            the new child. If new_child is null, then existing child will
	 *            be removed.
	 * @return if the replacement is successful, the new CompositeMap will be
	 *         returned. Otherwise the return value is null.
	 */
	public CompositeMap replaceChild(CompositeMap child, CompositeMap new_child) {
		if (child == null)
			return null;
		ListIterator it = (ListIterator) getChildIterator();
		if (it == null)
			return null;
		while (it.hasNext()) {
			CompositeMap m = (CompositeMap) it.next();
			if (m == child) {
				if (new_child == null) {
					it.remove();
					return null;
				}
				new_child.setParent(this);
				it.set(new_child);
				return new_child;
			}
		}
		return null;
	}

	/**
	 * Replace a child CompositeMap with a new CompositeMap.
	 * 
	 * @param child_name
	 *            Name of the child CompositeMap to replace
	 * @param new_child
	 *            the new child. If new_child is null, then existing child will
	 *            be removed.
	 * @return if the replacement is successful, the new CompositeMap will be
	 *         returned. Otherwise the return value is null.
	 */
	public CompositeMap replaceChild(String child_name, CompositeMap new_child) {
		return replaceChild(getChild(child_name), new_child);
		/*
		 * if( child_name == null) return null; ListIterator it =
		 * (ListIterator)getChildIterator(); if( it == null) return null; while(
		 * it.hasNext()){ CompositeMap m = (CompositeMap)it.next(); if(
		 * child_name.equals(m.getName()) ){ if( new_child ==null){ return null;
		 * it.remove(); } else{ new_child.setParent(this); it.set(new_child);
		 * return new_child; } return null; } } return null;
		 */
	}

	public CompositeMap copy(CompositeMap another) {
		// clear();
		putAll(another);
		addChilds(another.getChilds());

		this.text = another.getText();
		this.source = another.getSourceFile();
		this.location = another.getLocation();

		// this.text = ((CommentCompositeMap) another).text;
		// this.source = ((CommentCompositeMap) another).source;
		// this.location = ((CommentCompositeMap) another).location;
		return this;
	}

	/**
	 * set then name of this CompositeMap
	 * 
	 * @param _name
	 *            new name
	 */
	public void setName(String _name) {
		/*
		 * if( _name != null) name = _name.toLowerCase();
		 */
		name = _name;
	}

	/**
	 * set namespace uri
	 * 
	 * @param _uri
	 *            new namespace uri
	 */
	public void setNameSpaceURI(String _uri) {
		if (_uri != null) {
			if (_uri.length() == 0)
				_uri = null;
		}
		this.namespace_uri = _uri;
	}

	/**
	 * set prefix string
	 * 
	 * @param _p
	 *            new prefix
	 */
	public void setPrefix(String _p) {
		if (_p != null) {
			if (_p.length() == 0)
				_p = null;
		}
		this.prefix = _p;
	}

	/**
	 * set namespace uri and prefix
	 * 
	 * @param _prefix
	 *            prefix string
	 * @param _uri
	 *            namespace uri
	 */
	public void setNameSpace(String _prefix, String _uri) {
		setPrefix(_prefix);
		setNameSpaceURI(_uri);
	}

	/**
	 * get name of this CompositeMap
	 * 
	 * @return name of this CompositeMap
	 */
	public String getName() {
		return name;
	}

	/**
	 * get prefix string
	 * 
	 * @return prefix of this CompositeMap
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * get namespace uri
	 * 
	 * @return namespace uri of this CompositeMap
	 */
	public String getNamespaceURI() {
		return namespace_uri;
	}

	/**
	 * get raw name, which is equals to prefix+":"+name
	 * 
	 * @return raw name, if prefix is null this is equals to name
	 */
	public String getRawName() {
		if (prefix == null)
			return name;
		else
			return prefix + ':' + name;
	}

	/** gets text ( CDATA section in XML document ) */
	public String getText() {
		return text;
	}

	/** sets text ( CDATA section in XML document ) */
	public void setText(String t) {
		text = t;
	}

	public void setParent(CompositeMap p) {
		parent = p;
	}

	public CompositeMap getParent() {
		return parent;
	}

	public CompositeMap getRoot() {
		CompositeMap map = parent;
		while (map != null) {
			if (map.getParent() == null)
				return map;
			else
				map = map.getParent();
		}
		return this;
	}

	public Object put(Object key, Object value) {
		if (value != null)
			if (value instanceof CompositeMap) {
				((CompositeMap) value).setParent(this);
			}
		/*
		 * if( key != null && key instanceof String) key =
		 * ((String)key).toLowerCase();
		 */
		return super.put(key, value);
	}

	/*
	 * 
	 * public Object get( Object key){ if( key != null && key instanceof String)
	 * key = ((String)key).toLowerCase(); return super.get(key); }
	 */

	public Object putObject(String key, Object value, char attribute_char) {
		if (key == null)
			return null;
		if (key.charAt(0) == attribute_char)
			return put(key.substring(1), value);
		else {
			if (value instanceof CompositeMap && value != null) {
				CompositeMap cmap = (CompositeMap) value;
				cmap.setName(key);
				addChild(cmap);
				return value;
			} else
				return null;
		}
	}

	public Object putObject(String key, Object value) {
		return putObject(key, value, CompositeAccessor.DEFAULT_ATTRIB_CHAR);
	}

	Object getObject(String key, char attribute_char) {
		if (key == null)
			return null;
		if (key.charAt(0) == attribute_char)
			return get(key.substring(1));
		else
			return getChild(key);
	}

	public void addChild(int index, CompositeMap child) {
		child.setParent(this);
		// ((CommentCompositeMap) child).parent = this;
		getChildsNotNull().add(index, child);
	}

	/**
	 * add an CompositeMap instance to child list
	 * 
	 * @param child
	 *            child CompositeMap to add
	 */
	public void addChild(CompositeMap child) {
		child.setParent(this);
		// ((CommentCompositeMap) child).parent = this;
		getChildsNotNull().add(child);
	}

	/**
	 * Remove a child CompositeMap from children list
	 * 
	 * @param child
	 *            Child CompositeMap to remove
	 * @return true if remove is success
	 */
	public boolean removeChild(CompositeMap child) {
		boolean removed = false;
		if (childs != null) {
			ListIterator it = childs.listIterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj == child) {
					it.remove();
					removed = true;
					break;
				}
			}
			if (removed)
				child.setParent(null);
		}
		return removed;
	}

	/**
	 * create an child CompositeMap with specified name and namespace
	 * 
	 * @param prefix
	 *            prefix string
	 * @param uri
	 *            namespace uri
	 * @param name
	 *            name of child
	 * @return new child CompositeMap
	 * @see CompositeMap(String, String,String)
	 */
	public CompositeMap createChild(String prefix, String uri, String name) {
		CompositeMap child = new CommentCompositeMap(prefix, uri, name);
		addChild(child);
		return child;
	}

	public CompositeMap createChild(String name) {
		return createChild(null, null, name);
	}

	public CompositeMap createChildByTag(String access_tag) {
		return CompositeAccessor.defaultInstance()
				.createChild(this, access_tag);
	}

	/**
	 * get an child CompositeMap which is euqal with parameter
	 * 
	 * @param child
	 *            CompositeMap to compare
	 * @return child CompositeMap found or null if not found
	 */
	public CompositeMap getChild(CompositeMap child) {
		if (childs == null)
			return null;

		Iterator it = childs.iterator();
		while (it.hasNext()) {
			CompositeMap node = (CompositeMap) it.next();
			if (node.equals(child))
				return node;
		}

		return null;
	}

	/**
	 * get a child CompositeMap with specified name
	 * 
	 * @param name
	 *            name of CompositeMap to find
	 * @return child CompositeMap found or null
	 */
	public CompositeMap getChild(String name) {
		if (childs == null)
			return null;

		Iterator it = childs.iterator();
		while (it.hasNext()) {
			CompositeMap node = (CompositeMap) it.next();
			String nm = node.getName();
			if (nm != null)
				if (nm.equals(name))
					return node;
		}
		return null;
	}

	public CompositeMap getChildByAttrib(Object attrib_key, Object attrib_value) {
		return getChildByAttrib(null, attrib_key, attrib_value);
	}

	public CompositeMap getChildByAttrib(String element_name,
			Object attrib_key, Object attrib_value) {
		if (attrib_key == null)
			return null;
		Iterator it = getChildIterator();
		if (it == null)
			return null;
		while (it.hasNext()) {
			CompositeMap item = (CompositeMap) it.next();
			if (element_name != null)
				if (!element_name.equals(item.getName()))
					continue;
			Object vl = item.get(attrib_key);
			if (vl == null) {
				if (attrib_value == null)
					return item;
			} else if (vl.equals(attrib_value))
				return item;
		}
		return null;
	}

	/**
	 * get list of childs
	 * 
	 * @return List of childs or null if no childs
	 */
	public List getChilds() {
		return childs;
	}

	/**
	 * same as {@link getChilds()} except that if child list is null, a empty
	 * list is created thus this method will never return null
	 * 
	 * @return child list
	 */
	public List getChildsNotNull() {
		if (childs == null)
			childs = new LinkedList();
		return childs;
	}

	/**
	 * get Iterator object of child list
	 * 
	 * @return child Iterator, or null if child list is null
	 */
	public Iterator getChildIterator() {
		if (childs == null)
			return null;
		return childs.iterator();
	}

	public Object getObject(String key) {
		return CompositeAccessor.defaultInstance().get(this, key);
	}

	public boolean putObject(String key, Object value, boolean create) {
		return CompositeAccessor.defaultInstance()
				.put(this, key, value, create);
	}

	/**
	 * get a string parameter. map.put("sql", "select * from employee where
	 * employee_id = ${@employee_id}"); map.put("employee_id", "3"); String
	 * sql_stmt = map.getParameter("sql", true); // sql_stmt ==
	 * "select * from employee where employee_id = 3"
	 */

	public String getParameter(String key, boolean parse_param) {
		Object obj = get(key);
		if (obj == null)
			return null;
		if (obj instanceof String) {
			if (parse_param)
				return TextParser.parse((String) obj, this);
			else
				return (String) obj;
		} else
			return obj.toString();
	}

	/**
	 * override Object.toString()
	 */
	/*
	 * public String toString(){
	 * 
	 * StringBuffer rst =new StringBuffer(); String nm = getRawName(); if( nm !=
	 * null) rst.append('"').append(nm).append('"'); rst.append(
	 * super.toString()).append(" "); if( childs != null){
	 * rst.append("-> ").append( childs.toString()).append(" "); } return
	 * rst.toString(); }
	 */

	int iterateChild(IterationHandle handle, boolean root_first) {
		int result = IterationHandle.IT_CONTINUE;
		if (childs == null)
			return result;
		ListIterator it = childs.listIterator();
		while (it.hasNext()) {
			CompositeMap child = (CompositeMap) it.next();
			result = child.iterate(handle, root_first);
			if (result == IterationHandle.IT_BREAK)
				return result;
			else if (result == IterationHandle.IT_REMOVE) {
				it.remove();
				result = IterationHandle.IT_CONTINUE;
				// return IterationHandle.IT_CONTINUE;
			}
		}
		return result;
	}

	public int iterate(IterationHandle handle, boolean root_first) {
		int result;
		if (root_first) {
			result = handle.process(this);
			if (result == IterationHandle.IT_CONTINUE)
				result = iterateChild(handle, root_first);
			return result;
		} else {
			result = iterateChild(handle, root_first);
			if (result != IterationHandle.IT_BREAK)
				handle.process(this);
			return result;
		}
	}

	/*
	 * public void writeExternal(ObjectOutput out) throws IOException {
	 * out.writeObject(name); out.writeObject(namespace_uri);
	 * out.writeObject(prefix); out.writeObject(childs);
	 * System.out.println("writeExternal:"+childs);
	 * 
	 * }
	 * 
	 * public void readExternal(ObjectInput in) throws IOException,
	 * ClassNotFoundException { name = (String)in.readObject(); namespace_uri =
	 * (String)in.readObject(); prefix = (String)in.readObject(); childs =
	 * (List)in.readObject(); System.out.println("readExternal:"+childs); }
	 */
	/**
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		super.clear();
		if (childs != null) {
			childs.clear();
		}
		name = null;
		text = null;
		namespace_uri = null;
		prefix = null;
		childs = null;
		parent = null;
		source = null;
	}

	public QualifiedName getQName() {
		return new QualifiedName(namespace_uri, name);
	}

	/**
	 * create a new CompositeMap and copy content to it
	 */
	public Object clone() {
		CompositeMap m = (CompositeMap) super.clone();
		if (childs != null) {
			((CommentCompositeMap) m).childs = new LinkedList();
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				CompositeMap child = (CompositeMap) it.next();
				CompositeMap new_child = (CompositeMap) child.clone();
				new_child.setParent(m);
				// m.getChildsNotNull().add(new_child);
				((CommentCompositeMap) m).childs.add(new_child);
			}
		}
		return m;
	}

	public File getSourceFile() {
		if (source != null)
			return source;
		if (parent != null)
			return parent.getSourceFile();
		return null;
	}

	/** source file path where this CompositeMap is parsed from */
	public void setSourceFile(File source) {
		this.source = source;
	}

	/**
	 * get a Map containing namespace mapping, with namespace url as key,
	 * namespace prefix as value
	 */
	public Map getNamespaceMapping() {
		return namespace_mapping;
	}

	public void setNamespaceMapping(Map mapping) {
		namespace_mapping = mapping;
	}

	/*
	 * // Location in source file public Locator getLocator() { return locator;
	 * }
	 * 
	 * public void setLocator(Locator locator) { this.locator = locator; }
	 */
	public Location getLocationNotNull() {
		if (location == null)
			location = new Location();
		return location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public ILocatable asLocatable() {
		return new ILocatable() {

			public String getOriginSource() {
				File source = getSourceFile();
				return source == null ? null : source.getAbsolutePath();
			}

			public Location getOriginLocation() {
				return location;
			}
		};
	}

}
