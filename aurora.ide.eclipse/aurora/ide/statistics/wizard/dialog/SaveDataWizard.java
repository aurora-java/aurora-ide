package aurora.ide.statistics.wizard.dialog;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.api.statistics.Statistician;
import aurora.ide.api.statistics.model.StatisticsProject;

public class SaveDataWizard extends Wizard {

	private Statistician statistician;
	private StatisticsProject project;

	private FirstSaveDataWizardPage firstPage = new FirstSaveDataWizardPage("0");

	public SaveDataWizard(Statistician statistician) {
		this.project = statistician.getProject();
		this.statistician = statistician;
	}

	public StatisticsProject getProject() {
		return project;
	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		setWindowTitle("保存向导");
		firstPage.init(statistician);
	}

	@Override
	public void addPages() {
		addPage(firstPage);
	}

	@Override
	public boolean performFinish() {
		project.setProjectName(firstPage.getProjectName());
		project.setStorer(firstPage.getStorer());
		project.setRepositoryType(firstPage.getRepositoryType());
		project.setRepositoryRevision(firstPage.getRepositoryRevesion());
		project.setRepositoryPath(firstPage.getRepositoryPath());
		return true;
	}
}
