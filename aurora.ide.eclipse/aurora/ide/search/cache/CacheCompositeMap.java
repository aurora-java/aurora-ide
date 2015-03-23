package aurora.ide.search.cache;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.QualifiedName;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;
import aurora.ide.api.composite.map.CommentCompositeMap;

class CacheCompositeMap extends CommentCompositeMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4707528070420323353L;

	private CommentCompositeMap map;

	CommentCompositeMap getRealMap() {
		return map;
	}

	public CacheCompositeMap(CommentCompositeMap map) {
		super();
		this.map = map;
	}

	public String getString(Object key) {
		return map.getString(key);
	}

	public String getString(Object key, String default_value) {
		return map.getString(key, default_value);
	}

	public void putString(Object key, String value) {
		// map.putString(key, value);
		throw new CacheUnsupportOperationException();
	}

	public Boolean getBoolean(Object key) {
		return map.getBoolean(key);
	}

	public boolean getBoolean(Object key, boolean default_value) {
		return map.getBoolean(key, default_value);
	}

	public void putBoolean(Object key, boolean value) {
		// map.putBoolean(key, value);
		throw new CacheUnsupportOperationException();
	}

	public Integer getInt(Object key) {
		return map.getInt(key);
	}

	public String getComment() {
		return map.getComment();
	}

	public int getInt(Object key, int default_value) {
		return map.getInt(key, default_value);
	}

	public void setComment(String t) {
		// map.setComment(t);
		throw new CacheUnsupportOperationException();
	}

	public String getEndElementComment() {
		return map.getEndElementComment();
	}

	public void putInt(Object key, int value) {
		// map.putInt(key, value);
		throw new CacheUnsupportOperationException();
	}

	public void setEndElementComment(String t) {
		// map.setEndElementComment(t);
		throw new CacheUnsupportOperationException();
	}

	public Long getLong(Object key) {
		return map.getLong(key);
	}

	public void setStartPoint(int line, int column) {
		// map.setStartPoint(line, column);
		throw new CacheUnsupportOperationException();
	}

	public void setEndPoint(int line, int column) {
		// map.setEndPoint(line, column);
		throw new CacheUnsupportOperationException();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public long getLong(Object key, long default_value) {
		return map.getLong(key, default_value);
	}

	public String toXML() {
		return map.toXML();
	}

	public void putLong(Object key, long value) {
		// map.putLong(key, value);
		throw new CacheUnsupportOperationException();
	}

	public Short getShort(Object key) {
		return map.getShort(key);
	}

	public void addChilds(Collection another) {
		// map.addChilds(another);
		throw new CacheUnsupportOperationException();
	}

	public short getShort(Object key, short default_value) {
		return map.getShort(key, default_value);
	}

	public void putShort(Object key, short value) {
		// map.putShort(key, value);
		throw new CacheUnsupportOperationException();
	}

	public CompositeMap replaceChild(CompositeMap child, CompositeMap new_child) {
		// return map.replaceChild(child, new_child);
		throw new CacheUnsupportOperationException();
	}

	public Double getDouble(Object key) {
		return map.getDouble(key);
	}

	public double getDouble(Object key, double default_value) {
		return map.getDouble(key, default_value);
	}

	public void putDouble(Object key, double value) {
		// map.putDouble(key, value);
		throw new CacheUnsupportOperationException();
	}

	public Float getFloat(Object key) {
		return map.getFloat(key);
	}

	public CompositeMap replaceChild(String child_name, CompositeMap new_child) {
		// return map.replaceChild(child_name, new_child);
		throw new CacheUnsupportOperationException();
	}

	public float getFloat(Object key, float default_value) {
		return map.getFloat(key, default_value);
	}

	public void putFloat(Object key, float value) {
		// map.putFloat(key, value);
		throw new CacheUnsupportOperationException();
	}

	public Byte getByte(Object key) {
		return map.getByte(key);
	}

	public CompositeMap copy(CompositeMap another) {
		// return map.copy(another);
		throw new CacheUnsupportOperationException();
	}

	public byte getByte(Object key, byte default_value) {
		return map.getByte(key, default_value);
	}

	public void setName(String _name) {
		// map.setName(_name);
		throw new CacheUnsupportOperationException();
	}

	public int hashCode() {
		return map.hashCode();
	}

	public void setNameSpaceURI(String _uri) {
		// map.setNameSpaceURI(_uri);
		throw new CacheUnsupportOperationException();
	}

	public void setPrefix(String _p) {
		// map.setPrefix(_p);
		throw new CacheUnsupportOperationException();
	}

	public void setNameSpace(String _prefix, String _uri) {
		// map.setNameSpace(_prefix, _uri);
		throw new CacheUnsupportOperationException();
	}

	public String getName() {
		return map.getName();
	}

	public String getPrefix() {
		return map.getPrefix();
	}

	public String getNamespaceURI() {
		return map.getNamespaceURI();
	}

	public String getRawName() {
		return map.getRawName();
	}

	public String getText() {
		return map.getText();
	}

	public void setText(String t) {
		// map.setText(t);
		throw new CacheUnsupportOperationException();
	}

	public void setParent(CompositeMap p) {
		// map.setParent(p);
		throw new CacheUnsupportOperationException();
	}

	public CompositeMap getParent() {
		return map.getParent();
	}

	public CompositeMap getRoot() {
		return map.getRoot();
	}

	public Object put(Object key, Object value) {
		// return map.put(key, value);
		throw new CacheUnsupportOperationException();
	}

	public Object putObject(String key, Object value, char attribute_char) {
		// return map.putObject(key, value, attribute_char);
		throw new CacheUnsupportOperationException();
	}

	public Object putObject(String key, Object value) {
		// return map.putObject(key, value);
		throw new CacheUnsupportOperationException();
	}

	public void addChild(int index, CompositeMap child) {
		// map.addChild(index, child);
		throw new CacheUnsupportOperationException();
	}

	public void addChild(CompositeMap child) {
		// map.addChild(child);
		throw new CacheUnsupportOperationException();
	}

	public boolean removeChild(CompositeMap child) {
		// return map.removeChild(child);
		throw new CacheUnsupportOperationException();
	}

	public CompositeMap createChild(String prefix, String uri, String name) {
		// return map.createChild(prefix, uri, name);
		throw new CacheUnsupportOperationException();
	}

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public CompositeMap createChild(String name) {
		// return map.createChild(name);
		throw new CacheUnsupportOperationException();
	}

	public CompositeMap createChildByTag(String access_tag) {
		// return map.createChildByTag(access_tag);
		throw new CacheUnsupportOperationException();
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public CompositeMap getChild(CompositeMap child) {
		return map.getChild(child);
	}

	public CompositeMap getChild(String name) {
		return map.getChild(name);
	}

	public CompositeMap getChildByAttrib(Object attrib_key, Object attrib_value) {
		return map.getChildByAttrib(attrib_key, attrib_value);
	}

	public CompositeMap getChildByAttrib(String element_name,
			Object attrib_key, Object attrib_value) {
		return map.getChildByAttrib(element_name, attrib_key, attrib_value);
	}

	public List getChilds() {
		return map.getChilds();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public List getChildsNotNull() {
		return map.getChildsNotNull();
	}

	public Iterator getChildIterator() {
		return map.getChildIterator();
	}

	public Object getObject(String key) {
		return map.getObject(key);
	}

	public boolean putObject(String key, Object value, boolean create) {
		// return map.putObject(key, value, create);
		throw new CacheUnsupportOperationException();
	}

	public String getParameter(String key, boolean parse_param) {
		return map.getParameter(key, parse_param);
	}

	public String toString() {
		return map.toString();
	}

	public int iterate(IterationHandle handle, boolean root_first) {
		return map.iterate(handle, root_first);
	}

	public void clear() {
		// map.clear();
		throw new CacheUnsupportOperationException();
	}

	public QualifiedName getQName() {
		return map.getQName();
	}

	public Object clone() {
		return map.clone();
	}

	public File getSourceFile() {
		return map.getSourceFile();
	}

	public void setSourceFile(File source) {
		// map.setSourceFile(source);
		throw new CacheUnsupportOperationException();
	}

	public Map getNamespaceMapping() {
		return map.getNamespaceMapping();
	}

	public void setNamespaceMapping(Map mapping) {
		// map.setNamespaceMapping(mapping);
		throw new CacheUnsupportOperationException();
	}

	public Location getLocationNotNull() {
		return map.getLocationNotNull();
	}

	public Location getLocation() {
		return map.getLocation();
	}

	public void setLocation(Location location) {
		// map.setLocation(location);
		throw new CacheUnsupportOperationException();
	}

	public ILocatable asLocatable() {
		return map.asLocatable();
	}

	public void putAll(Map m) {
		// map.putAll(m);
		throw new CacheUnsupportOperationException();
	}

	public Object remove(Object key) {
		// return map.remove(key);
		throw new CacheUnsupportOperationException();
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set keySet() {
		return map.keySet();
	}

	public Collection values() {
		return map.values();
	}

	public Set entrySet() {
		return map.entrySet();
	}

}
