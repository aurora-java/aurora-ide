package aurora.ide.meta.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.DialogUtil;

public class MetaProjectPropertyPage extends PropertyPage {
	public MetaProjectPropertyPage() {
	}

	public static final String PROPERTY_ID = "aurora.ide.meta.project.property";
	private static final String MODEL_PROTOTYPE = "LOCAL_WEB_URL";
	private static final String UI_PROTOTYPE= "WEB_HOME";
	private static final String TEMPLATE = "BM_HOME";
	public static final QualifiedName MODEL_QN = new QualifiedName(PROPERTY_ID,
			MODEL_PROTOTYPE);
	public static final QualifiedName SCREEN_QN = new QualifiedName(
			PROPERTY_ID, UI_PROTOTYPE);
	public static final QualifiedName TEMPLATE_QN = new QualifiedName(
			PROPERTY_ID, TEMPLATE);
	public static final QualifiedName AURORA_PROJECT_QN = new QualifiedName(
			PROPERTY_ID, "project");
	private Text model;
	private Text screen;
	private Text template;
	private Combo auroraProjectNameField;

	protected Control createContents(Composite parent) {
		this.noDefaultAndApplyButton();
		Composite content = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		content.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		content.setLayout(layout);

		Label label = new Label(content, SWT.NONE);
		label.setText("Model Prototype Folder: ");
		model = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		model.setLayoutData(gridData);
		model.setEditable(false);
		try {
			String mf = getProject().getPersistentProperty(MODEL_QN);
			if (filtEmpty(mf) != null) {
				model.setText(mf);
			}
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		label = new Label(content, SWT.NONE);
		label.setText("UI Prototype Folder: ");
		screen = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		screen.setLayoutData(gridData);
		screen.setEditable(false);
		try {
			String mf = getProject().getPersistentProperty(SCREEN_QN);
			if (filtEmpty(mf) != null) {
				screen.setText(mf);
			}
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		label = new Label(content, SWT.NONE);
		label.setText("Template Folder: ");
		this.template = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		template.setLayoutData(gridData);
		template.setEditable(false);
		try {
			String mf = getProject().getPersistentProperty(TEMPLATE_QN);
			if (filtEmpty(mf) != null) {
				template.setText(mf);
			}
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		// new project label
		Label projectLabel = new Label(content, SWT.NONE);
		projectLabel.setText("Aurora Project Name: ");
		projectLabel.setFont(parent.getFont());

		// aurora project name entry field
		auroraProjectNameField = new Combo(content, SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		auroraProjectNameField.setLayoutData(data);
		auroraProjectNameField.setFont(parent.getFont());
		auroraProjectNameField.select(0);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		auroraProjectNameField.add("");
		for (IProject p : projects) {
			boolean hasAuroraNature = false;
			try {
				hasAuroraNature = AuroraProjectNature.hasAuroraNature(p);
			} catch (CoreException e) {
			}
			if (hasAuroraNature) {
				auroraProjectNameField.add(p.getName());
			}
		}

		try {
			String mf = getProject().getPersistentProperty(AURORA_PROJECT_QN);
			if (filtEmpty(mf) != null) {
				int indexOf = auroraProjectNameField.indexOf(mf);
				if (indexOf > -1)
					auroraProjectNameField.select(indexOf);
				else{
					auroraProjectNameField.select(0);
				}
			}
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		new Label(content, SWT.NONE);
		return content;
	}

	public boolean performOk() {
		try {
			getProject().setPersistentProperty(
					MetaProjectPropertyPage.AURORA_PROJECT_QN,
					auroraProjectNameField.getText());
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return true;
	}

	private IProject getProject() {
		return (IProject) getElement();
	}

	public static String filtEmpty(String str) {
		if ("".equals(str))
			return null;
		return str;
	}

	public static void savePersistentProperty(IProject newProject,
			String auroraProjectName) {
		try {
			newProject.setPersistentProperty(MetaProjectPropertyPage.MODEL_QN,
					"model_prototype");
			newProject.setPersistentProperty(MetaProjectPropertyPage.SCREEN_QN,
					"ui_prototype");
			newProject.setPersistentProperty(
					MetaProjectPropertyPage.TEMPLATE_QN, "template");
			newProject.setPersistentProperty(
					MetaProjectPropertyPage.AURORA_PROJECT_QN,
					auroraProjectName);
		} catch (CoreException e) {
		}
	}
}
