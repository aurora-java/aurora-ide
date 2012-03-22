package aurora.ide.meta.gef.designer.model;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.editor.BMModelViewer;
import aurora.ide.meta.gef.designer.editor.RelationViewer;

public class ModelUtil {
	private static final String RECORDS = Record.class.getSimpleName() + "s";
	private static final String RELATIONS = Relation.class.getSimpleName()
			+ "s";

	public static CompositeMap toCompositeMap(BMModel model) {
		CompositeMap map = new CompositeMap(BMModel.class.getSimpleName());
		if (model == null)
			return map;
		map.put(BMModel.TITLE, model.getTitle());
		CompositeMap recordMap = new CompositeMap(RECORDS);
		for (Record r : model.getRecords()) {
			recordMap.addChild(toCompositeMap(r));
		}
		map.addChild(recordMap);
		CompositeMap relationMap = new CompositeMap(RELATIONS);
		for (Relation r : model.getRelations()) {
			relationMap.addChild(toCompositeMap(r));
		}
		map.addChild(relationMap);
		return map;
	}

	private static CompositeMap toCompositeMap(Record r) {
		CompositeMap map = new CompositeMap(Record.class.getSimpleName());
		if (r == null)
			return null;
		String[] keys = BMModelViewer.TABLE_COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			map.put(keys[i], r.get(keys[i]));
		}
		return map;
	}

	private static CompositeMap toCompositeMap(Relation r) {
		CompositeMap map = new CompositeMap(Relation.class.getSimpleName());
		if (r == null)
			return null;
		String[] keys = RelationViewer.COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			map.put(keys[i], r.get(keys[i]));
		}
		return map;
	}

	public static BMModel fromCompositeMap(CompositeMap map) {
		BMModel model = new BMModel();
		model.setTitle(map.getString(BMModel.TITLE));
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

	private static Record getRecord(CompositeMap map) {
		Record r = new Record();
		String[] keys = BMModelViewer.TABLE_COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			if (BMModelViewer.COLUMN_QUERYFIELD.equals(keys[i]))
				r.put(keys[i], map.getBoolean(keys[i]));
			r.put(keys[i], map.get(keys[i]));
		}
		return r;
	}

	private static Relation getRelation(CompositeMap map) {
		Relation r = new Relation();
		String[] keys = RelationViewer.COLUMN_PROPERTIES;
		for (int i = 2; i < keys.length; i++) {
			r.put(keys[i], map.get(keys[i]));
		}
		return r;
	}
}
