package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.designer.BMCompositeMap;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.search.cache.CacheManager;
import aurora.ide.search.core.Util;

public class ForLovBmGenerator extends AbstractBmGenerator {

	private BMModel model;
	private String baseBmPath;
	private IProject auroraProject;
	private IFile bmFile;

	public ForLovBmGenerator(BMModel model, IFile baseBMFile) {
		super();
		this.model = model;
		this.bmFile = baseBMFile;
		baseBmPath = Util.toPKG(baseBMFile.getFullPath().removeFileExtension());
		this.auroraProject = baseBMFile.getProject();
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
