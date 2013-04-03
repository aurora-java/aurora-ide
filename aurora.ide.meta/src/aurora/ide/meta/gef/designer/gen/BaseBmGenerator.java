package aurora.ide.meta.gef.designer.gen;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.ModelMerger;
import aurora.ide.meta.gef.designer.model.ModelUtil;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;
import aurora.plugin.source.gen.screen.model.Input;

public class BaseBmGenerator extends AbstractBmGenerator {
	/**
	 * this will be used to generate relation`s refAlias<br/>
	 * currently , we assume that ,seqRefAlias will never greater than 'z'(there
	 * is no so much more relations)
	 */
	private char seqRefAlias = 'f';

	private BMModel model;
	private String name;
	private IFile file;
	private ModelMerger merger;

	public BaseBmGenerator() {
	}

	public BaseBmGenerator(IFile file) {
		this();
		name = file.getName();
		int idx = name.indexOf('.');
		if (idx != -1)
			name = name.substring(0, idx);
		this.file = file;
		merger = new ModelMerger(file);
		try {
			merger.initModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model = merger.getOriginalModel();
	}

	public void process() throws Exception {
		IFile bmFile = merger.getBMFile();
		CompositeMap bmMap = null;
		// if (bmFile.exists()) {
		// // System.out.println("bm '" + bmFile.getFullPath()
		// // + "' already exists.");
		// bmMap = merger.getMergedCompositeMap();
		// }
		// if (bmMap == null)
		bmMap = gen();
		createOrWriteFile(bmFile, bmMap);
		new ExtendBmGenerator(model, bmFile).gen();
	}

	protected void createOrWriteFile(IFile file, CompositeMap map)
			throws Exception {
		String xml = xml_header + map.toXML();
		InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		if (file.exists()) {
			file.setContents(is, IFile.FORCE, new NullProgressMonitor());
			is.close();
		} else {
			CreateFileOperation cfo = new CreateFileOperation(file, null, is,
					"write " + file.getFullPath());
			cfo.execute(new NullProgressMonitor(), null);
		}
	}

	protected CompositeMap newModelMap() {
		CompositeMap map = super.newModelMap();
		map.put("alias", "e");
		map.put("baseTable", name);
		return map;
	}

	private CompositeMap genFieldsMap() throws DuplicateException {
		CompositeMap fMap = newCompositeMap("fields");
		String pk_name = model.getPkRecord().getName();
		boolean haspk = false;
		for (Record r : model.getRecordList()) {
			if (CompositeUtil.findChild(fMap, "field", "name", r.getName()) != null) {
				throw new DuplicateException("field", "name", r.getName());
			}
			CompositeMap m = getNewFieldMap(r);
			if (pk_name.equals(m.getString("name")))
				haspk = true;
			fMap.addChild(getNewFieldMap(r));
		}
		if (!haspk)
			fMap.addChild(0, genPkFieldMap());
		return fMap;
	}

	private CompositeMap genPkFieldMap() {
		CompositeMap pk = newCompositeMap("field");
		Record r = model.getPkRecord();
		pk.put("name", r.getName());
		pk.put("databaseType", "BIGINT");
		pk.put("dataType", "java.lang.Long");
		pk.put("prompt", "primary_key");
		pk.put("forInsert", r.isForInsert());
		pk.put("forUpdate", r.isForUpdate());
		return pk;
	}

	private CompositeMap getNewFieldMap(Record r) {
		CompositeMap map = newCompositeMap("field");
		map.put("name", r.getName());
		DataType dt = DataType.fromString(r.getType());
		if (dt == null)
			dt = DataType.TEXT;
		map.put("databaseType", dt.getDbType());
		map.put("dataType", dt.getJavaType());
		String editor = r.getString(IDesignerConst.COLUMN_EDITOR);
		map.put("defaultEditor", editor);
		map.put("prompt", r.getPrompt());
		map.put("forInsert", r.isForInsert());
		map.put("forUpdate", r.isForUpdate());
		if (Input.Combo.equals(editor) || Input.LOV.equals(editor)) {
			String options = r.getString(IDesignerConst.COLUMN_OPTIONS);
			if (dt.getDisplayType().equalsIgnoreCase(IDesignerConst.LOOKUPCODE)) {
				map.put("lookupCode", options);
				map.put("lookupField", r.getName() + "_lookup");
			} else
				map.put("options", options);
		}
		return map;
	}

	private CompositeMap genPkMap() {
		CompositeMap pkMap = newCompositeMap("primary-key");
		CompositeMap pk = newCompositeMap("pk-field");
		pk.put("name", model.getPkRecord().getName());
		pkMap.addChild(pk);
		return pkMap;
	}

	private CompositeMap genFeatureMap() {
		CompositeMap fMap = newCompositeMap("features");
		CompositeMap spk = newCompositeMap("sequence-pk", o_ns_pre);
		fMap.addChild(spk);
		return fMap;
	}

	private CompositeMap genRelationMap() throws DuplicateException {
		CompositeMap rMap = newCompositeMap("relations");
		for (Relation r : model.getRelationList()) {
			if ((CompositeUtil.findChild(rMap, "relation", "name", r.getName())) != null)
				throw new DuplicateException("relation", "name", r.getName());
			rMap.addChild(getNewRelationMap(r));
		}
		return rMap;
	}

	private CompositeMap getNewRelationMap(Relation r) {
		CompositeMap map = newCompositeMap("relation");
		map.put("name", r.getName());
		map.put("refModel", r.getRefTable());
		map.put("joinType", r.getJoinType());
		map.put("refAlias", getRefAlias(r));
		CompositeMap m = newCompositeMap("reference");
		m.put("localField",
				ModelUtil.getLocalFieldName(model, r.getLocalField()));
		m.put("foreignField",
				ModelUtil.getForeignFieldName(file.getProject(),
						r.getSrcField(), r.getRefTable()));
		map.addChild(m);
		return map;
	}

	private String getRefAlias(Relation r) {
		return Character.toString(seqRefAlias++);
	}

	@Override
	protected void setUpModelMap(CompositeMap map) throws DuplicateException {
		map.put("title", model.getTitle());
		Record r = model.getDefaultDisplayRecord();
		if (r != null)
			map.put("defaultDisplayField", r.getName());
		map.addChild(genFieldsMap());
		map.addChild(genPkMap());
		map.addChild(genFeatureMap());
		map.addChild(genRelationMap());
		genQueryFieldsMap(map);
	}

	protected void genQueryFieldsMap(CompositeMap modelMap) {
		String refAlias = modelMap.getString("alias");
		if (refAlias == null || refAlias.length() == 0)
			refAlias = "";
		else
			refAlias += ".";
		CompositeMap qfMap = genQueryFieldMap(refAlias);
		if (qfMap.getChildsNotNull().size() > 0)
			modelMap.addChild(qfMap);
	}

	private CompositeMap genQueryFieldMap(String refAlias) {
		CompositeMap qfMap = newCompositeMap("query-fields");
		Record[] qfs = model.getRecords(true);
		for (Record r : qfs) {
			if (r.getBoolean(IDesignerConst.COLUMN_QUERYFIELD)) {
				String qt = r.getStringNotNull(IDesignerConst.COLUMN_QUERY_OP);
				String field = r.getName();
				if (qt.equals(IDesignerConst.OP_EQ)
						|| qt.equals(IDesignerConst.OP_LIKE)) {
					qfMap.addChild(operatorQueryField(r.getName(), qt));
				} else if (qt.equals(IDesignerConst.OP_GT)
						|| qt.equals(IDesignerConst.OP_LT)
						|| qt.equals(IDesignerConst.OP_GE)
						|| qt.equals(IDesignerConst.OP_LE)) {
					qfMap.addChild(simpleQueryField(field, field, qt, refAlias,
							r.getType()));
				} else if (qt.equals(IDesignerConst.OP_INTERVAL)) {
					qfMap.addChild(simpleQueryField(field, field + "_from",
							">=", refAlias, r.getType()));
					qfMap.addChild(simpleQueryField(field, field + "_to", "<=",
							refAlias, r.getType()));
				} else if (IDesignerConst.OP_ANY_MATCH.equals(qt)
						|| IDesignerConst.OP_PRE_MATCH.equals(qt)
						|| IDesignerConst.OP_END_MATCH.equals(qt)) {
					qfMap.addChild(matchQueryField(field, qt));
				}
			}
		}
		return qfMap;
	}

	/**
	 * use queryOperator...
	 * 
	 * @param field
	 * @param op
	 * @return
	 */
	private CompositeMap operatorQueryField(String field, String op) {
		CompositeMap q = newCompositeMap("query-field");
		q.put("field", field);
		q.put("queryOperator", op);
		return q;
	}

	/**
	 * use queryExpression<br/>
	 * <b>refAlias_</b>+<b>field</b> <b>op</b> ${/parameter/@<b>paraName</b>}
	 * 
	 * @param field
	 *            field name in this bm
	 * @param paraName
	 *            parameter name from outside
	 * @param op
	 * @param refAlias_
	 * @param type
	 *            the java type of parameter ,aurora will auto convert parameter
	 *            type by this argument
	 * @return
	 */
	private CompositeMap simpleQueryField(String field, String paraName,
			String op, String refAlias_, String type) {
		CompositeMap q = newCompositeMap("query-field");
		q.put("name", paraName);
		DataType dt = DataType.fromString(type);
		if (dt != null)
			q.put("dataType", dt.getJavaType());
		q.put("queryExpression", refAlias_ + field + " " + op
				+ " ${/parameter/@" + paraName + "}");
		return q;
	}

	/**
	 * create a query-field ,that can perform pre_match,end_match ,any_match
	 * 
	 * @param field
	 * @param matchType
	 * @return
	 */
	private CompositeMap matchQueryField(String field, String matchType) {
		CompositeMap map = newCompositeMap("query-field");
		map.put("name", field);
		String para = "${/parameter/@" + field + "}";
		String matchStr = "";
		if (IDesignerConst.OP_PRE_MATCH.equals(matchType))
			matchStr = para + "||'%'";
		else if (IDesignerConst.OP_END_MATCH.equals(matchType)) {
			matchStr = "'%'||" + para;
		} else
			matchStr = "'$'||" + para + "||'%'";
		map.put("queryExpression", field + " like " + matchStr);
		return map;
	}
}
