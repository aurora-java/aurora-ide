package aurora.ide.meta.gef.designer.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.editors.source.gen.DataSetFieldUtil;

public class ModelUtil implements IDesignerConst {
	private static final String PK = "Pk-Record";
	private static final String RECORDS = Record.class.getSimpleName() + "s";
	private static final String RELATIONS = Relation.class.getSimpleName()
			+ "s";

	/**
	 * for model io
	 * 
	 * @param model
	 * @return
	 */
	public static CompositeMap toCompositeMap(BMModel model) {
		CompositeMap map = new CommentCompositeMap(
				BMModel.class.getSimpleName());
		if (model == null)
			return map;
		map.put(BMModel.TITLE, model.getTitle());
		map.put(BMModel.AUTOEXTEND, model.getAutoExtends());
		map.put(BMModel.DEFAULT_DISPLAY, model.getDefaultDisplay());
		// /pk
		CompositeMap pkMap = toCompositeMap(model.getPkRecord());
		pkMap.setName(PK);
		map.addChild(pkMap);
		// /end pk
		CompositeMap recordMap = new CommentCompositeMap(RECORDS);
		for (Record r : model.getRecords()) {
			recordMap.addChild(toCompositeMap(r));
		}
		map.addChild(recordMap);
		CompositeMap relationMap = new CommentCompositeMap(RELATIONS);
		for (Relation r : model.getRelations()) {
			relationMap.addChild(toCompositeMap(r));
		}
		map.addChild(relationMap);
		return map;
	}

	/**
	 * for model io
	 * 
	 * @param r
	 * @return
	 */
	private static CompositeMap toCompositeMap(Record r) {
		CompositeMap map = new CommentCompositeMap(Record.class.getSimpleName());
		if (r == null)
			return null;
		String[] keys = TABLE_COLUMN_PROPERTIES;
		ArrayList<String> keyList = new ArrayList<String>();
		for (int i = 2; i < keys.length; i++) {
			keyList.add(keys[i]);
		}
		keyList.add(COLUMN_QUERY_OP);
		keyList.add(FOR_INSERT);
		keyList.add(FOR_UPDATE);
		keyList.add(FOR_QUERY);
		keyList.add(FOR_DISPLAY);
		keyList.add(FOR_LOV);
		keyList.add(INSERT_EXPRESSION);
		keyList.add(UPDATE_EXPRESSION);
		for (String k : keyList)
			map.put(k, r.get(k));
		return map;
	}

	/**
	 * for model io
	 * 
	 * @param r
	 * @return
	 */
	private static CompositeMap toCompositeMap(Relation r) {
		CompositeMap map = new CommentCompositeMap(
				Relation.class.getSimpleName());
		if (r == null)
			return null;
		String[] keys = COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			map.put(keys[i], r.get(keys[i]));
		}
		map.put(Relation.REF_PROMPTS, r.getRefPrompts());
		return map;
	}

	/**
	 * for model io
	 * 
	 * @param map
	 * @return
	 */
	public static BMModel fromCompositeMap(CompositeMap map) {
		BMModel model = new BMModel();
		model.setTitle(map.getString(BMModel.TITLE));
		model.setAutoExtends(map.getString(BMModel.AUTOEXTEND));
		model.setDefaultDisplay(map.getString(BMModel.DEFAULT_DISPLAY));
		// /pk
		CompositeMap pkMap = map.getChild(PK);
		if (pkMap != null) {
			model.setPkRecord(getRecord(pkMap));
		}
		// /end pk
		CompositeMap recMap = map.getChild(RECORDS);
		if (recMap != null) {
			@SuppressWarnings("unchecked")
			List<CompositeMap> list = recMap.getChildsNotNull();
			for (CompositeMap m : list) {
				model.add(getRecord(m));
			}
		}
		CompositeMap relMap = map.getChild(RELATIONS);
		if (relMap != null) {
			@SuppressWarnings("unchecked")
			List<CompositeMap> list = relMap.getChildsNotNull();
			for (CompositeMap m : list) {
				model.add(getRelation(m));
			}
		}
		return model;
	}

	/**
	 * for model io
	 * 
	 * @param map
	 * @return
	 */
	private static Record getRecord(CompositeMap map) {
		Record r = new Record();
		String[] keys = TABLE_COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			if (COLUMN_QUERYFIELD.equals(keys[i]))
				r.put(keys[i], map.getBoolean(keys[i]));
			r.put(keys[i], map.get(keys[i]));
		}
		r.put(COLUMN_QUERY_OP, map.get(COLUMN_QUERY_OP));
		r.put(FOR_INSERT, map.getBoolean(FOR_INSERT));
		r.put(FOR_UPDATE, map.getBoolean(FOR_UPDATE));
		r.put(FOR_QUERY, map.getBoolean(FOR_QUERY));
		r.put(FOR_DISPLAY, map.getBoolean(FOR_DISPLAY));
		r.put(FOR_LOV, map.getBoolean(FOR_LOV));
		r.put(INSERT_EXPRESSION, map.getString(INSERT_EXPRESSION));
		r.put(UPDATE_EXPRESSION, map.getString(UPDATE_EXPRESSION));

		return r;
	}

	/**
	 * for model io
	 * 
	 * @param map
	 * @return
	 */
	private static Relation getRelation(CompositeMap map) {
		Relation r = new Relation();
		String[] keys = COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			r.put(keys[i], map.get(keys[i]));
		}
		r.setRefPrompts(map.getString(Relation.REF_PROMPTS));
		return r;
	}

	/**
	 * for common use<br/>
	 * select record`s name from records(of model) where record`s prompt=prompt
	 * 
	 * @param model
	 * @param prompt
	 * @return
	 */
	public static String getLocalFieldName(BMModel model, String prompt) {
		for (Record r : model.getRecordList()) {
			if (r.getPrompt().equals(prompt))
				return r.getName();
		}
		return "";
	}

	/**
	 * for common use<br/>
	 * select field`s name from fields(of bmPath in project) where field`s
	 * prompt=prompt<br/>
	 * <b>search scope not include ref-fields<b/>
	 * 
	 * @param project
	 * @param prompt
	 * @param bmPath
	 * @return
	 */
	public static String getForeignFieldName(IProject project, String prompt,
			String bmPath) {
		DataSetFieldUtil dsfu = new DataSetFieldUtil(project, "", bmPath);
		ArrayList<CompositeMap> als = dsfu.getLocalFields(dsfu.getBmMap(),
				false);
		for (CompositeMap m : als) {
			if (prompt.equals(m.getString("prompt")))
				return m.getString("name");
		}
		return "";
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public static String join(List<String> list, String sep) {
		StringBuilder sb = new StringBuilder();
		if (list.size() > 0)
			sb.append(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			sb.append(sep);
			sb.append(list.get(i));
		}
		return sb.toString();
	}

	public static String join(String[] ss, String sep) {
		StringBuilder sb = new StringBuilder();
		if (ss.length > 0)
			sb.append(ss[0]);
		for (int i = 1; i < ss.length; i++) {
			sb.append(sep);
			sb.append(ss[i]);
		}
		return sb.toString();
	}
}
