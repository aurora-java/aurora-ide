package aurora.ide.meta.gef.designer.gen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import uncertain.composite.CompositeMap;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

/**
 * override base bm,and set {@code forinsert,forupdate} property of field
 * 
 * @author jessen
 * 
 */
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
		CompositeMap fieldsMap = newCompositeMap("fields");
		for (Record r : model.getRecords(true)) {
			CompositeMap m = newCompositeMap("field");
			m.put("name", r.getName());
			m.put("forInsert", r.isForInsert());
			m.put("forUpdate", r.isForUpdate());
			fieldsMap.addChild(m);
		}

		modelMap.addChild(fieldsMap);
	}
}
