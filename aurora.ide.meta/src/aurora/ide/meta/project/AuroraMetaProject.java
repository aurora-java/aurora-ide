package aurora.ide.meta.project;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import aurora.ide.AuroraProjectNature;
import aurora.ide.meta.exception.ResourceNotFoundException;

public class AuroraMetaProject {

	private IProject project;

	// meta project
	public AuroraMetaProject(IProject project) {
		super();
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

	public IFolder getScreenFolder() throws ResourceNotFoundException {
		return this.getFolder(MetaProjectPropertyPage.SCREEN_QN);
	}

	public IFolder getModelFolder() throws ResourceNotFoundException {
		return this.getFolder(MetaProjectPropertyPage.MODEL_QN);
	}

	public IFolder getTemplateFolder() throws ResourceNotFoundException {
		return this.getFolder(MetaProjectPropertyPage.TEMPLATE_QN);
	}

	public IProject getAuroraProject() throws ResourceNotFoundException {

		String name = getPersistentProperty(MetaProjectPropertyPage.AURORA_PROJECT_QN);
		IProject p = project.getWorkspace().getRoot().getProject(name);
		try {
			if (p.exists() && AuroraProjectNature.hasAuroraNature(p)) {
				return p;
			}
		} catch (CoreException e) {
		}
		throw new ResourceNotFoundException();
	}

	public IFolder getFolder(QualifiedName key)
			throws ResourceNotFoundException {
		String name = getPersistentProperty(key);
		IFolder folder = project.getFolder(name);
		if (folder.exists()) {
			return folder;
		}
		throw new ResourceNotFoundException();
	}

	private String getPersistentProperty(QualifiedName key)
			throws ResourceNotFoundException {
		try {
			String persistentProperty = this.getProject()
					.getPersistentProperty(key);
			if (null != persistentProperty && !"".equals(persistentProperty)) {
				return persistentProperty;
			}
		} catch (CoreException e) {
		}
		throw new ResourceNotFoundException();
	}

}
