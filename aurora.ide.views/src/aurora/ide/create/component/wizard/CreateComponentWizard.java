package aurora.ide.create.component.wizard;

import org.eclipse.jface.wizard.Wizard;

public class CreateComponentWizard extends Wizard {

	private ComponentListWizardPage componentListPage = new ComponentListWizardPage(
			"ComponentListWizardPage");

	private PrototpyeViewWizardPage prototpyePage = new PrototpyeViewWizardPage(
			"PrototpyeViewWizardPage");

	@Override
	public void addPages() {
		this.addPage(componentListPage);
		this.addPage(prototpyePage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
