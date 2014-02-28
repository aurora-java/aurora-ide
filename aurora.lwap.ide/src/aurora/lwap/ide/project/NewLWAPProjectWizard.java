package aurora.lwap.ide.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.project.wizard.NewAuroraProjectWizard;

public class NewLWAPProjectWizard extends NewAuroraProjectWizard {
	protected void addNature(IProject proj) throws CoreException {
		LWAPProjectNature.addNature(proj);
	}
	protected void setPersistentProperty(IProject proj) {
		//do nothing
	}
}
