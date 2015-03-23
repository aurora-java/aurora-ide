package aurora.bpmn.designer.rcp.viewer.action.wizard;

import java.util.Collection;

import org.eclipse.swt.widgets.Shell;

import aurora.bpmn.designer.ws.BPMNDefineCategory;
import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.UWizard;

public class CreateBPMDefineWizard extends UWizard {

	private BPMNDefineModel model;
	private CreateBPMDefinePage page;
	private Collection<BPMNDefineCategory> categorys;
	private BPMNDefineModel copyFrom;
	private boolean isNewVer;

	public CreateBPMDefineWizard(Shell shell) {
		super(shell);
	}

	public CreateBPMDefineWizard(Collection<BPMNDefineCategory> categorys,
			Shell shell) {
		super(shell);
		this.categorys = categorys;
	}

	@Override
	public void addPages() {
		page = new CreateBPMDefinePage("CreateBPMDefinePage");
		page.setCategorys(categorys);
		if (isNewVer)
			page.setNewVer(copyFrom);
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		model = new BPMNDefineModel();
		PageModel pm = page.getModel();
		model.setDescription(pm
				.getStringPropertyValue(CreateBPMDefinePage.DESCRIPTION));
		model.setName(pm.getStringPropertyValue(CreateBPMDefinePage.NAME));
		model.setProcess_code(pm
				.getStringPropertyValue(CreateBPMDefinePage.PROCESS_CODE));
		model.setProcess_version(pm
				.getStringPropertyValue(CreateBPMDefinePage.PROCESS_VERSION));
		model.setCategory_id(pm
				.getStringPropertyValue(CreateBPMDefinePage.CATEGORY_ID));
		return true;
	}

	public BPMNDefineModel getModel() {
		return model;
	}

	public void setModel(BPMNDefineModel model) {
		this.model = model;
	}

	public void setNewVer(BPMNDefineModel copyFrom) {
		this.isNewVer = true;
		this.copyFrom = copyFrom;
	}

}
