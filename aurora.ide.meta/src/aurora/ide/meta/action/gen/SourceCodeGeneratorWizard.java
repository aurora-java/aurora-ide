package aurora.ide.meta.action.gen;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import org.eclipse.ui.IWorkbenchWindow;

import aurora.ide.AuroraMetaProjectNature;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.source.gen.core.ProjectGenerator;
import aurora.ide.meta.gef.i18n.Messages;
import aurora.ide.meta.project.AuroraMetaProject;

public class SourceCodeGeneratorWizard extends Wizard {

	private WizardPage mainPage;
	// cache of newly-created project
	private Text auroraProjectNameField;
	private Combo projectNameField;
	private Button overlap;
	private IWorkbenchWindow window;

	public final static String DIALOG_SETTING_SECTION = "aurora.ide.uip.action.SourceCodeGenerator"; //$NON-NLS-1$
	private final static String IS_OVERLAP = "isOverlap"; //$NON-NLS-1$

	public SourceCodeGeneratorWizard(IWorkbenchWindow window) {
		this.window = window;
		setNeedsProgressMonitor(true);
	}

	public void addPages() {

		mainPage = new WizardPage("gensource") { //$NON-NLS-1$

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
				overlap.setText(Messages.SourceCodeGeneratorWizard_3);

				setPageComplete(validatePage());
				// Show description on opening
				setErrorMessage(null);
				setMessage(null);
				setControl(composite);
				Dialog.applyDialogFont(composite);
				initPage();
			}

			public void initPage() {
				overlap.setSelection(isOverlap());
				int count = projectNameField.getItemCount();
				projectNameField.select(count > 0 ? 1 : 0);
				String[] pNames = Util.evaluateEnclosingProject(window);
				for (String name : pNames) {
					int indexOf = projectNameField.indexOf(name);
					if (indexOf != -1) {
						projectNameField.select(indexOf);
						break;
					}
				}
				this.selecttionChanged();
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
				projectLabel.setText(Messages.SourceCodeGeneratorWizard_4);
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
						selecttionChanged();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
					}

				});

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProject[] projects = workspace.getRoot().getProjects();
				projectNameField.add(""); //$NON-NLS-1$
				for (IProject p : projects) {
					boolean hasAuroraNature = false;
					try {
						hasAuroraNature = AuroraMetaProjectNature
								.hasAuroraNature(p);
					} catch (CoreException e) {
					}
					if (hasAuroraNature && p.isAccessible()) {
						projectNameField.add(p.getName());
					}
				}

			}

			private final void createAruoraProjectNameGroup(
					Composite projectGroup) {
				// project specification group

				// new project label
				Label projectLabel = new Label(projectGroup, SWT.NONE);
				projectLabel.setText(Messages.SourceCodeGeneratorWizard_6);
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
			 * Returns whether this page's controls currently all contain valid
			 * values.
			 * 
			 * @return <code>true</code> if all controls are valid, and
			 *         <code>false</code> if at least one is invalid
			 */
			protected boolean validatePage() {
				IProject handle = getAuroraProjectHandle();
				if (handle == null || !handle.exists()) {
					setErrorMessage(Messages.SourceCodeGeneratorWizard_7);
					return false;
				}
				setErrorMessage(null);
				setMessage(null);
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

			public void selecttionChanged() {
				String text = projectNameField.getText();
				if (text == null || "".equals(text)) //$NON-NLS-1$
					return;
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProject project = workspace.getRoot().getProject(text);
				AuroraMetaProject amp = new AuroraMetaProject(project);
				try {
					String name = amp.getAuroraProject().getName();
					auroraProjectNameField.setText(name);
				} catch (ResourceNotFoundException e1) {
					auroraProjectNameField.setText(""); //$NON-NLS-1$
				}
				setPageComplete(validatePage());
			}
		};
		mainPage.setTitle("Project"); //$NON-NLS-1$
		mainPage.setDescription(Messages.SourceCodeGeneratorWizard_11);
		this.addPage(mainPage);
	}

	public IProject getAuroraProjectHandle() {
		if (auroraProjectNameField == null)
			return null;
		String trim = auroraProjectNameField.getText().trim();
		if ("".equals(trim)) { //$NON-NLS-1$
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getProject(trim);
	}

	public boolean performFinish() {
		final String text = projectNameField.getText();
		final boolean selection = overlap.getSelection();
		saveOverlap(selection);
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
			DialogUtil.logErrorException(e);
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

	public IDialogSettings getDialogSettings() {
		IDialogSettings dialogSettingsSection = MetaPlugin.getDefault()
				.getDialogSettingsSection(DIALOG_SETTING_SECTION);
		return dialogSettingsSection;
	}

	private boolean isOverlap() {
		return getDialogSettings().getBoolean(IS_OVERLAP);
	}

	private void saveOverlap(boolean value) {
		getDialogSettings().put(IS_OVERLAP, value);
	}

}
