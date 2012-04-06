package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.search.core.Util;

public class ExtendBmGenerator extends BaseBmGenerator {
	private BMModel model;
	private IFile bmFile;
	private String baseBmPath;

	public ExtendBmGenerator(BMModel model, IFile bmFile) {
		super();
		this.model = model;
		this.bmFile = bmFile;
		baseBmPath = Util.toPKG(bmFile.getFullPath().removeFileExtension());
	}

	public void gen() {
		String[] ss = model.getAutoExtends().split("\\|");
		for (String s : ss) {
			if (IDesignerConst.AE_LOV.equals(s)) {

			} else if (IDesignerConst.AE_QUERY.equals(s)) {
				IFile file = getExtFile(s);
				CompositeMap map = getForQueryBm();
				try {
					createOrWriteFile(file, map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (IDesignerConst.AE_MAINTAIN.equals(s)) {

			} else {
				System.out.println("unknown auto extend mode : " + s);
			}
		}
	}

	private CompositeMap genExtBm(String baseBmPath, String extType) {
		CompositeMap map = genModelMap();
		map.put("extendMode", extType);
		map.put("extend", baseBmPath);
		return map;
	}

	private IFile getExtFile(String s) {
		IPath path = new Path(bmFile.getFullPath().removeFileExtension()
				.toString()
				+ "_for_" + s).addFileExtension("bm");
		IFile file = bmFile.getProject().getParent().getFile(path);
		return file;
	}

	private CompositeMap getForLovBm() {
		CompositeMap map = genExtBm(baseBmPath, "reference");
		CompositeMap fieldsMap = new CommentCompositeMap("fields");
		for (Record r : model.getRecordList()) {

		}
		map.addChild(fieldsMap);
		return map;
	}

	private CompositeMap getForMaintainBm() {
		CompositeMap map = genExtBm(baseBmPath, "reference");
		return map;
	}

	private CompositeMap getForQueryBm() {
		CompositeMap map = genExtBm(baseBmPath, "override");
		String refAlias = map.getString("alias");
		if (refAlias == null || refAlias.length() == 0)
			refAlias = "";
		else
			refAlias += ".";
		CompositeMap qfMap = newCompositeMap("query-fields");
		for (Record r : model.getRecordList()) {
			if (r.getBoolean(IDesignerConst.COLUMN_QUERYFIELD)) {
				String qt = r.getStringNotNull(IDesignerConst.COLUMN_QUERY_OP);
				if (qt.equals(IDesignerConst.OP_EQ)
						|| qt.equals(IDesignerConst.OP_GT)
						|| qt.equals(IDesignerConst.OP_LT)
						|| qt.equals(IDesignerConst.OP_GE)
						|| qt.equals(IDesignerConst.OP_LE)
						|| qt.equals(IDesignerConst.OP_LIKE)) {
					qfMap.addChild(simpleQueryField(r.getName(), qt));
				} else if (qt.equals(IDesignerConst.OP_INTERVAL)) {
					CompositeMap[] maps = intervalQueryField(r.getName(),
							refAlias);
					qfMap.addChild(maps[0]);
					qfMap.addChild(maps[1]);
				} else {
					qfMap.addChild(matchQueryField(r.getName(), qt));
				}
			}
		}
		map.addChild(qfMap);
		return map;
	}

	private CompositeMap simpleQueryField(String field, String op) {
		CompositeMap q = newCompositeMap("query-field");
		q.put("field", field);
		q.put("queryOperator", op);
		return q;
	}

	/**
	 * create 2 query-field ,that can perform a interval query
	 * 
	 * @param field
	 * @param refAlias_
	 *            empty str "" or real alias+"."
	 * @return
	 */
	private CompositeMap[] intervalQueryField(String field, String refAlias_) {
		CompositeMap[] maps = new CompositeMap[2];
		maps[0] = newCompositeMap("query-field");
		String fromName = field + "_from";
		maps[0].put("name", fromName);
		maps[0].put("queryExpression", refAlias_ + field + " >= ${/parameter/@"
				+ fromName + "}");
		maps[1] = newCompositeMap("query-field");
		String toName = field + "_to";
		maps[1].put("name", toName);
		maps[1].put("queryExpression", refAlias_ + field + " <= ${/parameter/@"
				+ toName + "}");
		return maps;
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
