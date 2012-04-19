package aurora.ide.meta.project;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.CreateFolderOperation;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import aurora.ide.AuroraProjectNature;
import aurora.ide.meta.MetaPlugin;

public class CreateMetaProjectWizard extends BasicNewProjectResourceWizard {

	private WizardNewProjectCreationPage mainPage;
	// cache of newly-created project
	private IProject newProject;
	private Combo auroraProjectNameField;

	public void addPages() {

		mainPage = new WizardNewProjectCreationPage("basicNewProjectPage") {

			// initial value stores
			private String initialProjectFieldValue;

			// widgets
			Text projectNameField;

			private Listener nameModifyListener = new Listener() {
				public void handleEvent(Event e) {
					boolean valid = validatePage();
					setPageComplete(valid);

				}
			};

			// constants
			private static final int SIZING_TEXT_FIELD_WIDTH = 250;

			/**
			 * (non-Javadoc) Method declared on IDialogPage.
			 */
			public void createControl(Composite parent) {
				Composite composite = new Composite(parent, SWT.NULL);

				initializeDialogUnits(parent);
				composite.setLayout(new GridLayout());
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));

				Composite projectGroup = new Composite(composite, SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.numColumns = 2;
				projectGroup.setLayout(layout);
				projectGroup.setLayoutData(new GridData(
						GridData.FILL_HORIZONTAL));

				createProjectNameGroup(projectGroup);
				createAruoraProjectNameGroup(projectGroup);

				setPageComplete(validatePage());
				// Show description on opening
				setErrorMessage(null);
				setMessage(null);
				setControl(composite);
				Dialog.applyDialogFont(composite);
			}

			/**
			 * Creates the project name specification controls.
			 * 
			 * @param parent
			 *            the parent composite
			 */
			private final void createProjectNameGroup(Composite projectGroup) {
				// project specification group

				// new project label
				Label projectLabel = new Label(projectGroup, SWT.NONE);
				projectLabel.setText("Project name: ");
				projectLabel.setFont(projectGroup.getFont());

				// new project name entry field
				projectNameField = new Text(projectGroup, SWT.BORDER);
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.widthHint = SIZING_TEXT_FIELD_WIDTH;
				projectNameField.setLayoutData(data);
				projectNameField.setFont(projectGroup.getFont());

				// Set the initial value first before listener
				// to avoid handling an event during the creation.
				if (initialProjectFieldValue != null) {
					projectNameField.setText(initialProjectFieldValue);
				}
				projectNameField.addListener(SWT.Modify, nameModifyListener);
			}

			private final void createAruoraProjectNameGroup(
					Composite projectGroup) {
				// project specification group

				// new project label
				Label projectLabel = new Label(projectGroup, SWT.NONE);
				projectLabel.setText("Aurora Project name: ");
				projectLabel.setFont(projectGroup.getFont());

				// aurora project name entry field
				auroraProjectNameField = new Combo(projectGroup, SWT.READ_ONLY);
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.widthHint = SIZING_TEXT_FIELD_WIDTH;
				auroraProjectNameField.setLayoutData(data);
				auroraProjectNameField.setFont(projectGroup.getFont());
				auroraProjectNameField.select(0);

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProject[] projects = workspace.getRoot().getProjects();
				auroraProjectNameField.add("");
				for (IProject p : projects) {
					boolean hasAuroraNature = false;
					try {
						hasAuroraNature = AuroraProjectNature
								.hasAuroraNature(p);
					} catch (CoreException e) {
					}
					if (hasAuroraNature) {
						auroraProjectNameField.add(p.getName());
					}
				}
			}

			/**
			 * Creates a project resource handle for the current project name
			 * field value. The project handle is created relative to the
			 * workspace root.
			 * <p>
			 * This method does not create the project resource; this is the
			 * responsibility of <code>IProject::create</code> invoked by the
			 * new project resource wizard.
			 * </p>
			 * 
			 * @return the new project resource handle
			 */
			public IProject getProjectHandle() {
				return ResourcesPlugin.getWorkspace().getRoot()
						.getProject(getProjectName());
			}

			/**
			 * Returns the current project name as entered by the user, or its
			 * anticipated initial value.
			 * 
			 * @return the project name, its anticipated initial value, or
			 *         <code>null</code> if no project name is known
			 */
			public String getProjectName() {
				if (projectNameField == null) {
					return initialProjectFieldValue;
				}

				return getProjectNameFieldValue();
			}

			/**
			 * Returns the value of the project name field with leading and
			 * trailing spaces removed.
			 * 
			 * @return the project name in the field
			 */
			private String getProjectNameFieldValue() {
				if (projectNameField == null) {
					return ""; //$NON-NLS-1$
				}

				return projectNameField.getText().trim();
			}

			/**
			 * Sets the initial project name that this page will use when
			 * created. The name is ignored if the createControl(Composite)
			 * method has already been called. Leading and trailing spaces in
			 * the name are ignored. Providing the name of an existing project
			 * will not necessarily cause the wizard to warn the user. Callers
			 * of this method should first check if the project name passed
			 * already exists in the workspace.
			 * 
			 * @param name
			 *            initial project name for this page
			 * 
			 * @see IWorkspace#validateName(String, int)
			 * 
			 */
			public void setInitialProjectName(String name) {
				if (name == null) {
					initialProjectFieldValue = null;
				} else {
					initialProjectFieldValue = name.trim();
				}
			}

			/**
			 * Returns whether this page's controls currently all contain valid
			 * values.
			 * 
			 * @return <code>true</code> if all controls are valid, and
			 *         <code>false</code> if at least one is invalid
			 */
			protected boolean validatePage() {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();

				String projectFieldContents = getProjectNameFieldValue();
				if (projectFieldContents.equals("")) { //$NON-NLS-1$
					setErrorMessage(null);
					setMessage("Project name must be specified.");
					return false;
				}

				IStatus nameStatus = workspace.validateName(
						projectFieldContents, IResource.PROJECT);
				if (!nameStatus.isOK()) {
					setErrorMessage(nameStatus.getMessage());
					return false;
				}

				IProject handle = getProjectHandle();
				if (handle.exists()) {
					setErrorMessage("A project with that name already exists in the workspace.");
					return false;
				}
				setErrorMessage(null);
				setMessage(null);
				return true;
			}

			public boolean useDefaults() {
				return true;
			}

			/*
			 * see @DialogPage.setVisible(boolean)
			 */
			public void setVisible(boolean visible) {
				// super.setVisible(visible);
				this.getControl().setVisible(visible);
				if (visible) {
					projectNameField.setFocus();
				}
			}
		};
		mainPage.setTitle("Project");
		mainPage.setDescription("Create an aurora meta project.");
		this.addPage(mainPage);

	}

	public IProject getAuroraProjectHandle() {
		if (auroraProjectNameField == null)
			return null;
		String trim = auroraProjectNameField.getText().trim();
		if ("".equals(trim)) {
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getProject(trim);
	}

	public boolean performFinish() {

		createNewProject();
		if (newProject == null) {
			return false;
		}
		createFolders();

		IProject auroraProjectHandle = this.getAuroraProjectHandle();
		String name = auroraProjectHandle == null ? "" : auroraProjectHandle
				.getName();
		MetaProjectPropertyPage.savePersistentProperty(newProject, name);

		updatePerspective();
		selectAndReveal(newProject);

		return true;
	}


	private void createFolders() {
		if (newProject == null) {
			return;
		}

		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				IFolder model = newProject.getFolder("model_prototype");
				IFolder screen = newProject.getFolder("ui_prototype");
				IFolder[] fs = new IFolder[] { model, screen };
				for (IFolder iFolder : fs) {
					CreateFolderOperation op = new CreateFolderOperation(
							iFolder, null, "new folder");
					try {
						op.execute(monitor,
								WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
					} catch (ExecutionException e) {
						throw new InvocationTargetException(e);
					}
				}
			}
		};

		// run the new folder creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
		} catch (InvocationTargetException e) {
		}

	}

	private IProject createNewProject() {
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

		description.setNatureIds(new String[] { AuroraMetaProjectNature.ID });

		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				CreateProjectOperation op = new CreateProjectOperation(
						description, "new aurora meta project");
				try {
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
			return null;
		}

		newProject = newProjectHandle;
		AuroraMetaProjectNature naturehandler = new AuroraMetaProjectNature();
		naturehandler.setProject(newProject);
		try {
			naturehandler.configure();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return newProject;
	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}
}
