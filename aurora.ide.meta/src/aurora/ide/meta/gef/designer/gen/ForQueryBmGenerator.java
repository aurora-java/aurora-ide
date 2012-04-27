package aurora.ide.meta.gef.designer.gen;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.designer.model.Relation;
import aurora.ide.search.cache.CacheManager;

public class ForQueryBmGenerator extends AbstractBmGenerator {

	private String baseBmPath;
	private BMModel model;
	private IProject auroraProject;

	public ForQueryBmGenerator(BMModel model, IFile baseBMFile) {
		super();
		this.model = model;
		baseBmPath = ResourceUtil.getBmPkgPath(baseBMFile);
		this.auroraProject = baseBMFile.getProject();
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
	protected void setUpModelMap(CompositeMap map) {
		CompositeMap refMap = genRefFieldsMap(model);
		if (refMap.getChildsNotNull().size() > 0)
			map.addChild(refMap);
		String refAlias = map.getString("alias");
		if (refAlias == null || refAlias.length() == 0)
			refAlias = "";
		else
			refAlias += ".";
		CompositeMap qfMap = genQueryFieldMap(refAlias);
		if (qfMap.getChildsNotNull().size() > 0)
			map.addChild(qfMap);
	}

	private CompositeMap genRefFieldsMap(BMModel model) {
		CompositeMap fieldsMap = newCompositeMap("ref-fields");
		ArrayList<String> reffName = new ArrayList<String>();
		for (Relation r : model.getRelationList()) {
			String bmpath = r.getRefTable();
			IFile file = ResourceUtil.getBMFile(auroraProject, bmpath);
			CompositeMap refBmMap = null;
			try {
				refBmMap = CacheManager.getCompositeMap(file);
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
			if (refBmMap == null)
				continue;
			BMCompositeMap bcm = new BMCompositeMap(refBmMap);
			for (String refp : r.getRefPromptsArray()) {
				CompositeMap remoteField = bcm.getFieldByPrompt(refp);
				if (remoteField == null)
					continue;
				CompositeMap m = newCompositeMap("ref-field");
				m.put("relationName", r.getName());
				String remoteDisplay = remoteField.getString("name");
				String ref_name = remoteDisplay + "_ref";
				int i = 1;
				while (reffName.indexOf(ref_name) != -1) {
					ref_name = remoteDisplay + "_ref_" + i++;
				}
				reffName.add(ref_name);
				m.put("name", ref_name);
				m.put("sourceField", remoteDisplay);
				fieldsMap.addChild(m);
			}
		}
		return fieldsMap;
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
		q.put("name", field);
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
