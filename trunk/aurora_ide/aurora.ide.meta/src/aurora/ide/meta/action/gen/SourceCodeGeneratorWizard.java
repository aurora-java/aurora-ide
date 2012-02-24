package aurora.ide.meta.action.gen;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraMetaProjectNature;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.editors.source.gen.ProjectGenerator;
import aurora.ide.meta.project.AuroraMetaProject;

public class SourceCodeGeneratorWizard extends Wizard {

	public static final String copyright = "(c) Copyright HAND Enterprise Solutions Company Ltd.";

	private WizardPage mainPage;
	// cache of newly-created project
	private Text auroraProjectNameField;
	private Combo projectNameField;
	private Button overlap;

	public void addPages() {

		mainPage = new WizardPage("gensource") {

			// initial value stores
			private String initialProjectFieldValue;

			// widgets

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

				overlap = new Button(projectGroup, SWT.CHECK);
				overlap.setText("覆盖存在的文件");

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
				projectNameField = new Combo(projectGroup, SWT.READ_ONLY);
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.widthHint = SIZING_TEXT_FIELD_WIDTH;
				projectNameField.setLayoutData(data);
				projectNameField.setFont(projectGroup.getFont());

				// Set the initial value first before listener
				// to avoid handling an event during the creation.
				if (initialProjectFieldValue != null) {
					projectNameField.setText(initialProjectFieldValue);
				}
				projectNameField.addSelectionListener(new SelectionListener() {

					public void widgetSelected(SelectionEvent e) {
						IWorkspace workspace = ResourcesPlugin.getWorkspace();
						IProject project = workspace.getRoot().getProject(
								projectNameField.getText());
						AuroraMetaProject amp = new AuroraMetaProject(project);
						try {
							String name = amp.getAuroraProject().getName();
							auroraProjectNameField.setText(name);
						} catch (ResourceNotFoundException e1) {
							auroraProjectNameField.setText("");
						}
						setPageComplete(validatePage());
					}

					public void widgetDefaultSelected(SelectionEvent e) {
					}

				});

				projectNameField.select(0);
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProject[] projects = workspace.getRoot().getProjects();
				projectNameField.add("");
				for (IProject p : projects) {
					boolean hasAuroraNature = false;
					try {
						hasAuroraNature = AuroraMetaProjectNature
								.hasAuroraNature(p);
					} catch (CoreException e) {
					}
					if (hasAuroraNature) {
						projectNameField.add(p.getName());
					}
				}

			}

			private final void createAruoraProjectNameGroup(
					Composite projectGroup) {
				// project specification group

				// new project label
				Label projectLabel = new Label(projectGroup, SWT.NONE);
				projectLabel.setText("Aurora Project name: ");
				projectLabel.setFont(projectGroup.getFont());

				// aurora project name entry field
				auroraProjectNameField = new Text(projectGroup, SWT.BORDER);
				GridData data = new GridData(GridData.FILL_HORIZONTAL);
				data.widthHint = SIZING_TEXT_FIELD_WIDTH;
				auroraProjectNameField.setLayoutData(data);
				auroraProjectNameField.setEditable(false);
				auroraProjectNameField.setFont(projectGroup.getFont());
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
				IProject handle = getAuroraProjectHandle();
				if (handle == null || !handle.exists()) {
					setErrorMessage("关联的Aurora Project不存在");
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
		mainPage.setDescription("将原型工程生成，可以运行的代码");
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
		final String text = projectNameField.getText();
		final boolean selection = overlap.getSelection();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(text);
		final ProjectGenerator pg = new ProjectGenerator(project, selection,
				getShell());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				pg.go(monitor);
			}
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			this.mainPage.setErrorMessage(pg.getErrorMessage());
			return false;
		} catch (InterruptedException e) {
			return true;
		}

		return true;
	}

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}
}
