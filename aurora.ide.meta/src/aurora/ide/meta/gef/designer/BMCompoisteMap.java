package aurora.ide.meta.gef.designer;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.search.cache.CacheManager;

public class BMCompoisteMap {

	private CompositeMap bmMap;

	public BMCompoisteMap(CompositeMap map) {
		if (map == null)
			throw new RuntimeException("parmeter can not be null.");
		this.bmMap = map;
	}

	public BMCompoisteMap(IFile bmFile) {
		try {
			this.bmMap = CacheManager.getWholeBMCompositeMap(bmFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CompositeMap getBmMap() {
		return bmMap;
	}

	/**
	 * reutrns a node under primary-key (not fields)
	 * 
	 * @return
	 */
	public CompositeMap getFirstPkField() {
		CompositeMap map = getPrimaryKeyMap();
		if (map != null) {
			@SuppressWarnings("unchecked")
			List<CompositeMap> list = map.getChildsNotNull();
			if (list.size() > 0)
				return list.get(0);
		}
		return null;
	}

	/**
	 * search primary-key,get the first pk-field ,then get its name<br/>
	 * if not exists ,return null
	 * 
	 * @return
	 */
	public String getPkFieldName() {
		CompositeMap pk = getFirstPkField();
		if (pk != null)
			return pk.getString("name");
		return null;
	}

	CompositeMap getFieldByProperty(String property, String value) {
		for (CompositeMap m : getFields()) {
			if (eq(m.getString(property), value))
				return m;
		}
		return null;
	}

	public CompositeMap getFieldByName(String name) {
		return getFieldByProperty("name", name);
	}

	public CompositeMap getFieldByPrompt(String prompt) {
		return getFieldByProperty("prompt", prompt);
	}

	private boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	private String nns(String s) {
		if (s == null)
			return "";
		return s;
	}

	public CompositeMap getFieldsMap() {
		return bmMap.getChild("fields");
	}

	public CompositeMap getPrimaryKeyMap() {
		return bmMap.getChild("primary-key");
	}

	public CompositeMap getRelationsMap() {
		return bmMap.getChild("relations");
	}

	public CompositeMap getQueryFieldsMap() {
		return bmMap.getChild("query-fields");
	}

	/**
	 * node canbe fields,primary-keys,relations...<br/>
	 * never return null
	 * 
	 * @param nodeName
	 * @return
	 */
	List<CompositeMap> getChildsOf(String nodeName) {
		CompositeMap map = bmMap.getChild(nodeName);
		if (map != null) {
			@SuppressWarnings("unchecked")
			List<CompositeMap> list = map.getChildsNotNull();
			return list;
		}
		return Collections.emptyList();
	}

	/**
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getFields() {
		return getChildsOf("fields");
	}

	/**
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getPrimaryKeys() {
		return getChildsOf("primary-key");
	}

	/**
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getRelations() {
		return getChildsOf("relations");
	}

	/**
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getQueryFields() {
		return getChildsOf("query-fields");
	}

	public String getField_name(CompositeMap fMap) {
		return nns(fMap.getString("name"));
	}

	public String getField_prompt(CompositeMap fMap) {
		return nns(fMap.getString("prompt"));
	}

	public CompositeMap getDefaultDisplayField() {
		String ddf = getDefaultDisplayFieldName();
		return getFieldByName(ddf);
	}

	public String getDefaultDisplayFieldName() {
		return bmMap.getString("defaultDisplayField");
	}
}
