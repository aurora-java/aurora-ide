package aurora.ide.meta.gef.designer.gen;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class BmGenerator implements IDesignerConst {
	public static final String bm_ns_pre = "bm";
	public static final String bm_ns_uri = "http://www.aurora-framework.org/schema/bm";

	private BMModel model;
	private String name;
	private HashMap<String, String> nsMapping = new HashMap<String, String>();

	public BmGenerator(BMModel model, String name) {
		this.model = model;
		this.name = name;
		nsMapping.put(bm_ns_uri, bm_ns_pre);
	}

	public void gen() {
		CompositeMap map = getModelMap();
		CompositeMap fields = newCompositeMap("fields");
		fillFields(fields);
		map.addChild(fields);
		System.out.println(map.toXML());
	}

	private void fillFields(CompositeMap map) {
		for (Record r : model.getRecordList()) {
			map.addChild(getNewFieldMap(r));
		}
	}

	private CompositeMap getModelMap() {
		CompositeMap map = newCompositeMap("model");
		map.setNamespaceMapping(nsMapping);
		map.setNameSpace(bm_ns_pre, bm_ns_uri);
		map.put("alias", "e");
		map.put("baseTable", name);
		return map;
	}

	private CompositeMap getNewFieldMap(Record r) {
		CompositeMap map = newCompositeMap("field");
		map.put("name", r.getName());
		String[] dts = getDataType(r);
		map.put("databaseType", dts[0]);
		map.put("datatype", dts[1]);
		map.put("prompt", r.getPrompt());
		return map;
	}

	private CompositeMap newCompositeMap(String... args) {
		CompositeMap map = new CommentCompositeMap();
		if (args.length > 0)
			map.setName(args[0]);
		map.setPrefix("bm");
		return map;
	}

	/**
	 * return a str array , contains databaseType,dataType
	 * 
	 * @param r
	 * @return
	 */
	private String[] getDataType(Record r) {
		String[] str = new String[] { "VARCHAR2", "java.lang.String" };
		String type = r.getType();
		DataType dt = DataType.fromString(type);
		if (dt != null) {
			str[0] = dt.getDbType();
			str[1] = dt.getJavaType();
		}
		return str;
	}
}
