package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.Wizard;

import aurora.ide.meta.gef.editors.models.link.TabRef;

public class CreateEditTabRefWizard extends Wizard {
	private CreateEditTabRefWizardPage page;
	private TabRef link;
	private boolean isDel;

	public CreateEditTabRefWizard(TabRef link) {
		this.link = link;
	}

	public void addPages() {
		page = new CreateEditTabRefWizardPage();
		page.init(link); 
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public TabRef getResult() {
		return page.getLink();
	}

	public boolean isDel() {
		return isDel;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}

	public boolean isNeedDelete() {
		return link != null;
	}

	public void delButtonClicked() {
		isDel = true;
	}

}
