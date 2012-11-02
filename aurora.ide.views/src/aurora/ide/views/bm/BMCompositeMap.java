package aurora.ide.views.bm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.search.cache.CacheManager;

/**
 * useful methods set to get information of bm<br/>
 * intend for <b>READ ONLY</b>
 * 
 * @author jessen
 * 
 */
public class BMCompositeMap {

	private CompositeMap bmMap;
	private List<CompositeMap> queryFields;
	private List<CompositeMap> relations;
	private List<CompositeMap> primaryKeys;
	private List<CompositeMap> fields;
	private List<CompositeMap> refFields;
	private CompositeMap fieldsMap;
	private CompositeMap primaryKeyMap;
	private CompositeMap relationMap;
	private CompositeMap refFieldsMap;
	private CompositeMap queryFieldsMap;
	private CompositeMap firstPkField;
	private CompositeMap fieldOfPk;

	/**
	 * 
	 * @param map
	 *            the root of bm map,can not be null
	 */
	public BMCompositeMap(CompositeMap map) {
		if (map == null)
			throw new NullPointerException("parameter can not be null.");
		this.bmMap = map;
	}

	/**
	 * 
	 * @param bmFile
	 *            the file of bm
	 */
	public BMCompositeMap(IFile bmFile) {
		try {
			bmMap = CacheManager.getCompositeMap(bmFile);
			if (bmMap.get("extend") != null)
				bmMap = CacheManager.getWholeBMCompositeMap(bmFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CompositeMap getBmMap() {
		return bmMap;
	}

	/**
	 * reutrns a node under <b>primary-key</b> (not fields)
	 * 
	 * @see #getFieldOfPk
	 * @see #getPrimaryKeys
	 * @return
	 */
	public CompositeMap getFirstPkField() {
		if (firstPkField == null) {
			List<CompositeMap> list = getPrimaryKeys();
			if (list.size() > 0)
				firstPkField = list.get(0);
		}
		return firstPkField;
	}

	/**
	 * reutrns a node under <b>fields</b> , and it is pk
	 * 
	 * @see #getFirstPkField
	 * @return
	 */
	public CompositeMap getFieldOfPk() {
		if (fieldOfPk == null) {
			String pkn = getPkFieldName();
			if (pkn == null)
				return null;
			fieldOfPk = getFieldByName(pkn);
		}
		return fieldOfPk;
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

	/**
	 * {@link BMCompositeMap#getChildByProperty(CompositeMap,String,String)}
	 * 
	 * @param property
	 * @param value
	 * @return
	 */
	public CompositeMap getFieldByProperty(String property, String value) {
		return getChildByProperty(getFieldsMap(), property, value);
	}

	/**
	 * if the <b> parMap</b> has children ,then search its children list<br/>
	 * try to get the first child ,that its value of <b> property</b> equals <b>
	 * value</b><br/>
	 * note that, case of <b> property</b> is ignored<br/>
	 * 
	 * @see BMCompositeMap#getMapAttribute(CompositeMap, String)
	 * 
	 * @param parMap
	 * @param property
	 * @param value
	 * @return
	 */
	public static CompositeMap getChildByProperty(CompositeMap parMap,
			String property, String value) {
		if (parMap == null || property == null)
			return null;
		@SuppressWarnings("unchecked")
		List<CompositeMap> list = parMap.getChildsNotNull();
		for (CompositeMap m : list) {
			if (eq(getMapAttribute(m, property), value))
				return m;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	List<CompositeMap> getChild(CompositeMap map) {
		if (map == null)
			return Collections.emptyList();
		return map.getChildsNotNull();
	}

	/**
	 * get value of map ,ignore key typecase
	 * 
	 * @param map
	 * @param property
	 * @return
	 */
	public static String getMapAttribute(CompositeMap map, String property) {
		if (map == null)
			throw new NullPointerException("parameter map can not be null.");
		if (property == null)
			return map.getString(null);
		// try to get value directly,maybe avoid loss of performance
		String value = map.getString(property);
		if (value != null)
			return value;
		for (Object key : map.keySet()) {
			if (key != null && property.equalsIgnoreCase(key.toString()))
				return map.getString(key);
		}
		return null;
	}

	public CompositeMap getFieldByName(String name) {
		return getFieldByProperty("name", name);
	}

	public CompositeMap getFieldByPrompt(String prompt) {
		return getFieldByProperty("prompt", prompt);
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	/**
	 * if s==null return "" <br/>
	 * else return s
	 * 
	 * @param s
	 * @return
	 */
	public static String nns(String s) {
		if (s == null)
			return "";
		return s;
	}

	public CompositeMap getFieldsMap() {
		if (fieldsMap == null)
			fieldsMap = bmMap.getChild("fields");
		return fieldsMap;
	}

	public CompositeMap getPrimaryKeyMap() {
		if (primaryKeyMap == null)
			primaryKeyMap = bmMap.getChild("primary-key");
		return primaryKeyMap;
	}

	public CompositeMap getRelationsMap() {
		if (relationMap == null)
			relationMap = bmMap.getChild("relations");
		return relationMap;
	}

	public CompositeMap getRefFieldsMap() {
		if (refFieldsMap == null)
			refFieldsMap = bmMap.getChild("ref-fields");
		return refFieldsMap;
	}

	public CompositeMap getQueryFieldsMap() {
		if (queryFieldsMap == null)
			queryFieldsMap = bmMap.getChild("query-fields");
		return queryFieldsMap;
	}

	/**
	 * node canbe fields,primary-keys,relations...<br/>
	 * never return null
	 * 
	 * @param nodeName
	 * @return
	 */
	List<CompositeMap> getChildOf(String nodeName) {
		CompositeMap map = bmMap.getChild(nodeName);
		return getChild(map);
	}

	/**
	 * {@link #getChildOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getFields() {
		if (fields == null)
			fields = getChildOf("fields");
		return fields;
	}

	/**
	 * return children of fields,but ,remove pk field
	 * 
	 * @return never return null
	 */
	public List<CompositeMap> getFieldsWithoutPk() {
		String pkn = getPkFieldName();
		List<CompositeMap> fields = new ArrayList<CompositeMap>();
		for (CompositeMap m : getFields())
			if (!m.getString("name").equals(pkn))
				fields.add(m);
		return fields;
	}

	/**
	 * get fields in this bm,scope include <b>fields,ref-fields</b>
	 * {@link #getFieldsWithoutPk()}
	 * 
	 * @param pk
	 *            include pk
	 * @param ref
	 *            include ref-fields
	 * @return
	 */
	public List<CompositeMap> getFields(boolean pk, boolean ref) {
		List<CompositeMap> fields = new ArrayList<CompositeMap>();
		if (pk)
			fields.addAll(getFields());
		else
			fields.addAll(getFieldsWithoutPk());
		if (ref)
			fields.addAll(getRefFields());
		return fields;
	}

	/**
	 * get all defined pk-fields under node <b>primary-key</b><br/>
	 * {@link #getChildOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getPrimaryKeys() {
		if (primaryKeys == null)
			primaryKeys = getChildOf("primary-key");
		return primaryKeys;
	}

	/**
	 * get all defined relations under node <b>relations</b><br/>
	 * {@link #getChildOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getRelations() {
		if (relations == null)
			relations = getChildOf("relations");
		return relations;
	}

	/**
	 * get all defined ref-fields under node <b>ref-fields</b><br/>
	 * {@link #getChildOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getRefFields() {
		if (refFields == null)
			refFields = getChildOf("ref-fields");
		return refFields;
	}

	/**
	 * get all defined query-fields under node <b>query-fields</b><br/>
	 * {@link #getChildOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getQueryFields() {
		if (queryFields == null)
			queryFields = getChildOf("query-fields");
		return queryFields;
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
		return getMapAttribute(bmMap, "defaultDisplayField");
	}
}
