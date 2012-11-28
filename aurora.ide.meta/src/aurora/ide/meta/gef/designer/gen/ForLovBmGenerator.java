package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class ForLovBmGenerator extends AbstractBmGenerator {

	private String baseBmPath;
	private IFile bmFile;
	private BMModel model;

	public ForLovBmGenerator(BMModel model, IFile baseBMFile) {
		super();
		this.bmFile = baseBMFile;
		this.model = model;
		baseBmPath = ResourceUtil.getBmPkgPath(baseBMFile);
	}

	@Override
	protected String getExtend() {
		return baseBmPath;
	}

	@Override
	protected String getExtendMode() {
		return "reference";
	}

	@Override
	protected void setUpModelMap(CompositeMap map) {
		CompositeMap fieldsMap = newCompositeMap("fields");
		for (Record r : model.getRecords(true)) {
			if (!r.isForLov())
				continue;
			CompositeMap m = newCompositeMap("field");
			m.put("name", r.getName());
			if (r.isForDisplay())
				m.put("forDisplay", true);
			if (r.isForQuery())
				m.put("forQuery", true);
			fieldsMap.addChild(m);
		}
		// CompositeMap bmMap = null;
		// try {
		// bmMap = CacheManager.getCompositeMap(bmFile);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// if (bmMap == null)
		// return;
		// BMCompositeMap bcm = new BMCompositeMap(bmMap);
		// String[] fnames = { bcm.getPkFieldName(),
		// bcm.getDefaultDisplayFieldName() };
		// for (String name : fnames) {
		// if (name != null) {
		// CompositeMap m = newCompositeMap("field");
		// m.put("name", name);
		// m.put("forDisplay", "true");
		// m.put("forQuery", "true");
		// fieldsMap.addChild(m);
		// }
		// }
		map.addChild(fieldsMap);
		// map.addChild(genEmptyQueryFieldsMap());
	}

	/**
	 * generate a empty query-fields map to hide parent bm`s query-fields <br/>
	 * 
	 * @return
	 */
	private CompositeMap genEmptyQueryFieldsMap() {
		return newCompositeMap("query-fields");
	}

}
