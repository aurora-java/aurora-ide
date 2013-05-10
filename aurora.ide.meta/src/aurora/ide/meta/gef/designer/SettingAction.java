package aurora.ide.meta.gef.designer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.wizard.ExtensionWizardPage;
import aurora.ide.meta.gef.editors.PrototypeImagesUtils;

public class SettingAction extends Action {

	private BMModel model;
	private IWorkbenchPart part;

	public SettingAction(BMModel model, IWorkbenchPart part) {
		super("Setting");
		setImageDescriptor(PrototypeImagesUtils.getImageDescriptor("setting.png"));
		this.model = model;
		this.part = part;
	}

	@Override
	public void run() {
		WizardDialog wd = new WizardDialog(part.getSite().getShell(),
				new SettingWizard());
		wd.setPageSize(600, 400);
		wd.open();
	}

	class SettingWizard extends Wizard {
		ExtensionWizardPage page = new ExtensionWizardPage();

		public SettingWizard() {
			super();
			page.setModel(model);
			page.setDefaultSeletion(model.getAutoExtendTypes());
		}

		@Override
		public void addPages() {
			addPage(page);
		}

		@Override
		public boolean performFinish() {
			String[] strs = page.getUserSelection();
			model.setAutoExtendsArray(strs);
			return true;
		}

	}
}
