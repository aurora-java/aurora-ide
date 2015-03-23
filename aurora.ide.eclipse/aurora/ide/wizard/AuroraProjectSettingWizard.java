package aurora.ide.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.AuroraProjectNature;
import aurora.ide.project.AuroraProject;
import aurora.ide.swt.util.UWizard;

public class AuroraProjectSettingWizard extends UWizard {

	private IProject project;
	private AuroraProjectPropertiesSettingPage page;

	public AuroraProjectSettingWizard(Shell shell, IProject project) {
		super(shell);
		this.project = project;
		page = new AuroraProjectPropertiesSettingPage(
				"AuroraProjectPropertiesSettingPage", project);
	}

	@Override
	public void addPages() {
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		try {
			AuroraProjectNature.addAuroraNature(project);
			AuroraProject ap = new AuroraProject(project);
			ap.setBMHome(page.getBmPath());
			ap.setWebHome(page.getWebPath());
			ap.setMainPage("http://127.0.0.1:8080/" + project.getName());
		} catch (CoreException e) {
			return false;
		}

		return true;
	}

}
