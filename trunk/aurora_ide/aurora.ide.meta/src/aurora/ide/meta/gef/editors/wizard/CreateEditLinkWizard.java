package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.jface.wizard.Wizard;

//import aurora.ide.meta.gef.editors.models.link.Link;

public class CreateEditLinkWizard extends Wizard {
	private CreateEditLinkWizardPage page;
//	private Link link;
	private boolean isDel;

//	public CreateEditLinkWizard(Link link) {
//		this.link = link;
//	}

	public void addPages() {
		page = new CreateEditLinkWizardPage();
//		page.init(link);
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

//	public Link getResult() {
//		return page.getLink();
//	}

	public boolean isDel() {
		return isDel;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}

//	public boolean isNeedDelete() {
//		return link != null;
//	}

	public void delButtonClicked() {
		isDel = true;
	}

}
