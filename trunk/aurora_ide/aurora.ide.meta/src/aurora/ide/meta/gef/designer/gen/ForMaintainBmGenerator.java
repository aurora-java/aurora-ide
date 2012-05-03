package aurora.ide.meta.gef.designer.gen;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class ForMaintainBmGenerator extends AbstractBmGenerator {

	private String baseBmPath;
	private BMModel model;
	private IProject auroraProject;

	public ForMaintainBmGenerator(BMModel model, IFile baseBMFile) {
		this.model = model;
		baseBmPath = ResourceUtil.getBmPkgPath(baseBMFile);
		this.auroraProject = baseBMFile.getProject();
	}

	protected BMModel getModel() {
		return model;
	}

	protected IProject getAuroraProject() {
		return auroraProject;
	}

	@Override
	protected String getExtend() {
		return baseBmPath;
	}

	@Override
	protected String getExtendMode() {
		return "override";
	}

	@Override
	protected void setUpModelMap(CompositeMap modelMap) {
		genQueryFieldMap(modelMap);
	}

	protected void genQueryFieldMap(CompositeMap modelMap) {
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
		ArrayList<Record> qfs = new ArrayList<Record>(model.getRecordList());
		qfs.add(0, model.getPkRecord());
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
