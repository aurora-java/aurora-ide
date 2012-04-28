package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.search.cache.CacheManager;

public class ForLovBmGenerator extends AbstractBmGenerator {

	private String baseBmPath;
	private IFile bmFile;

	public ForLovBmGenerator(BMModel model, IFile baseBMFile) {
		super();
		this.bmFile = baseBMFile;
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
		CompositeMap bmMap = null;
		try {
			bmMap = CacheManager.getCompositeMap(bmFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bmMap == null)
			return;
		BMCompositeMap bcm = new BMCompositeMap(bmMap);
		String[] fnames = { bcm.getPkFieldName(),
				bcm.getDefaultDisplayFieldName() };
		for (String name : fnames) {
			if (name != null) {
				CompositeMap m = newCompositeMap("field");
				m.put("name", name);
				m.put("forDisplay", "true");
				m.put("forQuery", "true");
				fieldsMap.addChild(m);
			}
		}
		map.addChild(fieldsMap);
	}

}
