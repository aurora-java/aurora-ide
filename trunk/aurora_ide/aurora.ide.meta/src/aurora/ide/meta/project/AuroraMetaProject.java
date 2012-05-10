package aurora.ide.meta.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.ResourceDescription;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import uncertain.composite.CompositeMap;
import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.search.cache.CacheManager;

public class AuroraMetaProject {

	private static final String CONFIG_FILE_NAME = ".prototype";

	private IProject project;

	private static final String UIP_HOME = "ui_prototype";
	private static final String MODEL_HOME = "model_prototype";
	private static final String AURORA_PROJECT = "aurora_project";

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
		return this.getFolder(UIP_HOME);
	}

	public IFolder getModelFolder() throws ResourceNotFoundException {
		return this.getFolder(MODEL_HOME);
	}

	public IProject getAuroraProject() throws ResourceNotFoundException {

		String name = getPersistentProperty(AURORA_PROJECT);
		if ("".equals(name)) {
			try {
				name = getProject().getPersistentProperty(
						MetaProjectPropertyPage.AURORA_PROJECT_QN);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (name == null || "".equals(name))
			throw new ResourceNotFoundException();
		IProject p = project.getWorkspace().getRoot().getProject(name);
		try {
			if (p.exists() && AuroraProjectNature.hasAuroraNature(p)) {
				return p;
			}
		} catch (CoreException e) {
		}
		throw new ResourceNotFoundException();
	}

	public void setUIPFolder(String folderName) {
		this.setPersistentProperty(UIP_HOME, folderName);
	}

	public void setModelFolder(String folderName) {
		this.setPersistentProperty(MODEL_HOME, folderName);
	}

	public void setAuroraProject(String name) {
		this.setPersistentProperty(AURORA_PROJECT, name);
	}

	public IFolder getFolder(String key) throws ResourceNotFoundException {
		String name = getPersistentProperty(key);
		if ("".equals(name) || name == null)
			name = key;
		IFolder folder = project.getFolder(name);
		if (folder.exists()) {
			return folder;
		}
		throw new ResourceNotFoundException();
	}

	private String getPersistentProperty(String key)
			throws ResourceNotFoundException {
		IFile config = this.getProject().getFile(CONFIG_FILE_NAME);
		if (config.exists()) {
			try {
				CompositeMap map = CacheManager.getCompositeMap(config);
				return map.getString(key, "");
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	private String setPersistentProperty(String key, String value) {
		IFile config = this.getProject().getFile(CONFIG_FILE_NAME);
		CompositeMap map = new CompositeMap("config");
		if (config.exists()) {
			try {
				CompositeMap _map = CacheManager.getCompositeMap(config);
				map.copy(_map);
			} catch (CoreException e) {
				e.printStackTrace();
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		map.put(key, value);
		try {
			genNewFile(config, map.toXML());
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return key;
	}

	private void genNewFile(IFile newFile, String content)
			throws ExecutionException {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (is == null) {
			return;
		}
		if (newFile.exists()) {
			try {
				newFile.setContents(is, true, false, null);
				return;
			} catch (CoreException e) {
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

			}
		}
		CreateFileOperation cfo = new CreateFileOperation(newFile, null, is,
				"create file.") { //$NON-NLS-1$
			@Override
			protected void setResourceDescriptions(
					ResourceDescription[] descriptions) {
				super.setResourceDescriptions(descriptions);
			}

			public IStatus computeExecutionStatus(IProgressMonitor monitor) {
				IStatus status = super.computeExecutionStatus(monitor);
				if (status.isOK()) {
					// Overwrite is not allowed when we are creating a new
					// file
					status = computeCreateStatus(false);
				}
				return status;
			}
		};
		cfo.execute(null, WorkspaceUndoUtil.getUIInfoAdapter(Display
				.getDefault().getActiveShell()));
	}
}
