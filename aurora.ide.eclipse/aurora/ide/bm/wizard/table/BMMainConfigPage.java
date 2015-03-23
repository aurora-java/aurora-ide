package aurora.ide.bm.wizard.table;

import java.sql.Connection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aurora.ide.AuroraProjectNature;
import aurora.ide.editor.widgets.WizardPageRefreshable;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.project.AuroraProject;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (bm).
 */

public class BMMainConfigPage extends WizardPageRefreshable {
	private Text containerText;
	private ISelection selection;
	private BMFromDBWizard wizard;
	private Button autoRegisterPromptButton;

	public BMMainConfigPage(ISelection selection, BMFromDBWizard bmWizard) {
		super("wizardPage");
		setTitle(LocaleMessage.getString("business.model.editor.file"));
		setDescription(LocaleMessage.getString("bm.wizard.desc"));
		this.selection = selection;
		this.wizard = bmWizard;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText(LocaleMessage.getString("container"));

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkPageValues();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText(LocaleMessage.getString("openBrowse"));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IPath path = AuroraProject
						.openFolderSelectionDialog(getShell());
				if (path != null)
					containerText.setText(path.toString());
			}
		});

		autoRegisterPromptButton = new Button(container, SWT.CHECK);
		autoRegisterPromptButton.setText("向数据库自动注册字段描述");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		autoRegisterPromptButton.setLayoutData(gd);
		autoRegisterPromptButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				if (autoRegisterPromptButton.getSelection()) {
					try {
						Connection con = wizard.getConnection();
						if (con == null) {
							DialogUtil.showErrorMessageBox("不能连接到数据库，请检查配置!");
							autoRegisterPromptButton.setSelection(false);
						}
					} catch (ApplicationException e) {
						DialogUtil.logErrorException("不能连接到数据库，请检查配置!", e);
						autoRegisterPromptButton.setSelection(false);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		initPageValues();
		checkPageValues();
		setControl(container);
	}

	public Button getAutoRegisterPromptButton() {
		return autoRegisterPromptButton;
	}

	public void initPageValues() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
	}

	public void checkPageValues() {
		String path = getContainerName();
		if (path == null || "".equals(path)) {
			updatePageStatus(LocaleMessage
					.getString("file.container.must.be.specified"));
			return;
		}
		IPath p = new Path(path);
		if (p.isEmpty() || p.hasTrailingSeparator()) {
			updatePageStatus(LocaleMessage
					.getString("file.container.must.be.specified"));
			return;
		}

		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);

		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updatePageStatus(LocaleMessage
					.getString("file.container.must.exist"));
			return;
		}
		if (!container.isAccessible()) {
			updatePageStatus(LocaleMessage
					.getString("project.must.be.writable"));
			return;
		}
		IProject project = (IProject) (container.getType() == IResource.PROJECT ? container
				: container.getProject());

		try {
			if (AuroraProjectNature.hasAuroraNature(project) == false) {
				updatePageStatus(LocaleMessage.getString("无法找到Aurora工程"));
				return;
			}
		} catch (CoreException e) {
			updatePageStatus(LocaleMessage.getString("无法找到Aurora工程"));
			return;
		}

		try {
			Connection dbConnection = DBConnectionUtil.getDBConnection(project);
			if (dbConnection == null) {
				this.updatePageStatus("无法获取数据库连接");
				return;
			}
		} catch (ApplicationException e) {
			this.updatePageStatus("无法获取数据库连接");
			return;
		}

		updatePageStatus(null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public IProject getProject() {
		if (this.isPageComplete() == false) {
			return null;
		}
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(getContainerName());
		IProject project = (IProject) (container.getType() == IResource.PROJECT ? container
				: container.getProject());

		return project;
	}

}