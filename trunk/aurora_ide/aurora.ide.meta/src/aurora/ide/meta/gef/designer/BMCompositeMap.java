package aurora.ide.meta.gef.designer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.search.cache.CacheManager;

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

	public BMCompositeMap(CompositeMap map) {
		if (map == null)
			throw new RuntimeException("parameter can not be null.");
		this.bmMap = map;
	}

	public BMCompositeMap(IFile bmFile) {
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
	 * reutrns a node under <b>primary-key</b> (not fields)
	 * 
	 * @return
	 */
	public CompositeMap getFirstPkField() {
		if (firstPkField == null) {
			CompositeMap map = getPrimaryKeyMap();
			if (map != null) {
				@SuppressWarnings("unchecked")
				List<CompositeMap> list = map.getChildsNotNull();
				if (list.size() > 0)
					firstPkField = list.get(0);
			}
		}
		return firstPkField;
	}

	/**
	 * reutrns a node under <b>fields</b> , and it is pk
	 * 
	 * @return
	 */
	public CompositeMap getFieldOfPk() {
		if (fieldOfPk == null) {
			String pkn = getPkFieldName();
			if (pkn == null)
				return null;
			for (CompositeMap m : getFields())
				if (pkn.equals(m.getString("name"))) {
					fieldOfPk = m;
					break;
				}
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
		if (fields == null)
			fields = getChildsOf("fields");
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
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getPrimaryKeys() {
		if (primaryKeys == null)
			primaryKeys = getChildsOf("primary-key");
		return primaryKeys;
	}

	/**
	 * get all defined relations under node <b>relations</b><br/>
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getRelations() {
		if (relations == null)
			relations = getChildsOf("relations");
		return relations;
	}

	/**
	 * get all defined ref-fields under node <b>ref-fields</b><br/>
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getRefFields() {
		if (refFields == null)
			refFields = getChildsOf("ref-fields");
		return refFields;
	}

	/**
	 * get all defined query-fields under node <b>query-fields</b><br/>
	 * {@link #getChildsOf(String)}
	 * 
	 * @return
	 */
	public List<CompositeMap> getQueryFields() {
		if (queryFields == null)
			queryFields = getChildsOf("query-fields");
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
		return bmMap.getString("defaultDisplayField");
	}
}
