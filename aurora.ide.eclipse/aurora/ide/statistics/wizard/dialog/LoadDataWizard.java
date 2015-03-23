package aurora.ide.statistics.wizard.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import aurora.ide.api.statistics.model.StatisticsProject;

public class LoadDataWizard extends Wizard {
	FirstLoadDataWizardPage firstpage = new FirstLoadDataWizardPage("1");
	SecondLoadDataWizardPage secondPage = new SecondLoadDataWizardPage("2");

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setWindowTitle("载入向导");
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				firstpage.init();
			}
		});
	}

	public StatisticsProject getStatisticsProject() {
		return secondPage.getStatisticsProject();
	}
	
	public IProject getProject() {
		return secondPage.getProject();
	}
	
	@Override
	public void addPages() {
		addPage(firstpage);
		addPage(secondPage);
	}

	@Override
	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page instanceof SecondLoadDataWizardPage) {
			if (secondPage.isPageComplete()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
