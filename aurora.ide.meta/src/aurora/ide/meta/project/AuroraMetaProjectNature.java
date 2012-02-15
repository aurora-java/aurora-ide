package aurora.ide.meta.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.meta.MetaPlugin;

public class AuroraMetaProjectNature implements IProjectNature {

	private IProject project;
	public static final String ID = MetaPlugin.PLUGIN_ID + ".nature";

	public void configure() throws CoreException {}

	public void deconfigure() throws CoreException {}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
	public static boolean hasAuroraNature(IProject project)
			throws CoreException {
		return project.hasNature(ID);
	}
}
