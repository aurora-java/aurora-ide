package aurora.ide.project.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import aurora.ide.AuroraPlugin;
import aurora.ide.AuroraProjectNature;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.StatusUtil;
import aurora.ide.project.AuroraProject;

public class NewAuroraProjectWizard extends BasicNewProjectResourceWizard
		implements Runnable, IRunnableWithProgress {

	private WizardNewProjectCreationPage mainPage;

	// cache of newly-created project
	private IProject newProject;

	/**
	 * Creates a wizard for creating a new project resource in the workspace.
	 */
	public NewAuroraProjectWizard() {

	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public void addPages() {

		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage") { //$NON-NLS-1$
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.ui.dialogs.WizardNewProjectCreationPage#createControl
			 * (org.eclipse.swt.widgets.Composite)
			 */
			public void createControl(Composite parent) {
				super.createControl(parent);
				Dialog.applyDialogFont(getControl());
			}
		};
		mainPage.setTitle("Aurora Project");
		mainPage.setDescription("Create a new Aurora Project.");
		this.addPage(mainPage);

	}

	protected IProject createNewProject() {
		if (newProject != null) {
			return newProject;
		}

		// get a project handle
		final IProject newProjectHandle = mainPage.getProjectHandle();

		// get a project descriptor
		URI location = null;
		if (!mainPage.useDefaults()) {
			location = mainPage.getLocationURI();
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);

		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				CreateProjectOperation op = new CreateProjectOperation(
						description, "Create a New Aurora Project.");
				try {
					// see bug
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
					// directly execute the operation so that the undo state is
					// not preserved. Making this undoable resulted in too many
					// accidental file deletions.
					op.execute(monitor,
							WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof ExecutionException
					&& t.getCause() instanceof CoreException) {
				CoreException cause = (CoreException) t.getCause();
				StatusAdapter status;
				if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
					status = new StatusAdapter(StatusUtil.newStatus(
							IStatus.WARNING,
							"Couldn't create the Aurora Project.", cause));
				} else {
					status = new StatusAdapter(StatusUtil.newStatus(cause
							.getStatus().getSeverity(),
							"Couldn't create the Aurora Project.", cause));
				}
				status.setProperty(StatusAdapter.TITLE_PROPERTY,
						"Couldn't create the Aurora Project.");
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
			} else {
				StatusAdapter status = new StatusAdapter(StatusUtil.newStatus(
						IStatus.WARNING, "Couldn't create the Aurora Project.",
						t));
				status.setProperty(StatusAdapter.TITLE_PROPERTY,
						"Couldn't create the Aurora Project.");
				StatusManager.getManager().handle(status,
						StatusManager.LOG | StatusManager.BLOCK);
			}
			return null;
		}

		newProject = newProjectHandle;

		return newProject;
	}

	/**
	 * Returns the newly created project.
	 * 
	 * @return the created project, or <code>null</code> if project not created
	 */
	public IProject getNewProject() {
		return newProject;
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchWizard.
	 */
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
		setNeedsProgressMonitor(true);
		setWindowTitle("Aurora");
	}

	/*
	 * (non-Javadoc) Method declared on IWizard.
	 */
	public boolean performFinish() {
		createNewProject();

		if (newProject == null) {
			return false;
		}

		updatePerspective();
		selectAndReveal(newProject);

		try {
			IProject proj = getNewProject();
			proj.setDefaultCharset(AuroraConstant.ENCODING, null);
			IFolder web_inf = ResourceUtil.searchWebInf(proj);
			if (web_inf == null) {
				createBasicFolders(proj);
			}
			{
				setPersistentProperty(proj);
				addNature(proj);
				// if (web_inf != null) {
				// startBuild();
				// }
			}
		} catch (CoreException e1) {
			DialogUtil.logErrorException(e1);
			return false;
		}

		return true;
	}

	protected void addNature(IProject proj) throws CoreException {
		AuroraProjectNature.addAuroraNature(proj);
	}

	// public boolean performFinish() {
	// if (!super.performFinish())
	// return false;
	// IProject proj = getNewProject();
	// if (proj == null)
	// return true;
	// try {
	// proj.setDefaultCharset(AuroraConstant.ENCODING, null);
	// } catch (CoreException e1) {
	// e1.printStackTrace();
	// }
	// IFolder web_inf = ResourceUtil.searchWebInf(proj);
	// if (web_inf == null) {
	// createBasicFolders(proj);
	// } else {
	// setPersistentProperty(proj, web_inf.getParent().getFullPath()
	// .toString(), web_inf.getFolder("classes").getFullPath()
	// .toString());
	// }
	// try {
	// AuroraProjectNature.addAuroraNature(proj);
	// AuroraProjectNature.autoSetProjectProperty(proj);
	// if (web_inf != null) {
	// startBuild();
	// }
	// } catch (CoreException e) {
	// DialogUtil.logErrorException(e);
	// }
	// return true;
	// }

	protected void createBasicFolders(IProject proj) {
		String[] segs = { "webRoot", "WEB-INF", "classes" };
		IPath path = proj.getFullPath();
		try {
			for (String s : segs) {
				path = path.append(s);
				IFolder f = proj.getParent().getFolder(path);
				f.create(false, true, null);
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	protected void setPersistentProperty(IProject proj) {
		try {
			AuroraProject ap = new AuroraProject(proj);
			IFolder web_inf = ResourceUtil.searchWebInf(proj);
			ap.setWebHome(web_inf.getParent().getFullPath().toString());
			if (web_inf.getFolder("classes").exists()) {
				ap.setBMHome(web_inf.getFolder("classes").getFullPath()
						.toString());
			}
			ap.setMainPage("http://127.0.0.1:8080/" + proj.getName());

			// proj.setPersistentProperty(ProjectPropertyPage.WebQN, webHome);
			// proj.setPersistentProperty(ProjectPropertyPage.BMQN, bmHome);
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	private void startBuild() {
		// TODO
		Display.getCurrent().asyncExec(this);
	}

	public void run() {
		try {
			AuroraPlugin.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(this);
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			getNewProject()
					.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		} catch (CoreException e) {
		}
	}
}
