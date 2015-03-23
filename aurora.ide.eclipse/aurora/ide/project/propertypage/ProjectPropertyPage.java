package aurora.ide.project.propertypage;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;

import aurora.ide.AuroraPlugin;
import aurora.ide.builder.ResourceUtil;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ExceptionUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.helpers.SystemException;
import aurora.ide.project.AuroraProject;

public class ProjectPropertyPage extends PropertyPage implements Runnable,
		IRunnableWithProgress {
	public ProjectPropertyPage() {
	}

	public static final String PropertyId = "aurora.ide.projectproperty";
	private static final String LOCAL_WEB_URL = "LOCAL_WEB_URL";
	private static final String WEB_HOME = "WEB_HOME";
	private static final String BM_HOME = "BM_HOME";
	private static final String DebugMode = "DEBUG_MODE";
	private static final String BUILD_RIGHT_NOW = "BUILD_RIGHT_NOW";
	public static final QualifiedName LoclaUrlHomeQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, LOCAL_WEB_URL);
	public static final QualifiedName WebQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, WEB_HOME);
	public static final QualifiedName BMQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, BM_HOME);
	public static final QualifiedName buildNow = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, BUILD_RIGHT_NOW);
	public static final QualifiedName DebugModeQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, DebugMode);
	private Text localWebUrlText;
	private Text webHomeText;
	private Text bmHomeText;
	// private Button debugButton;
	private Button cb_isBuild;

	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		content.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		content.setLayout(layout);

		// web url
		Label localWebLabel = new Label(content, SWT.NONE);
		localWebLabel.setText(LocaleMessage.getString("preview.url"));
		localWebUrlText = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		localWebUrlText.setLayoutData(gridData);
		try {
			String localWebUrl = getProject().getPersistentProperty(
					LoclaUrlHomeQN);
			if (filtEmpty(localWebUrl) != null) {
				localWebUrlText.setText(localWebUrl);
			} else {
				localWebUrl = ProjectUtil.autoGetLocalWebUrl(getProject());
				if (filtEmpty(localWebUrl) != null) {
					localWebUrlText.setText(localWebUrl);
				}
			}
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		// webDir
		Label webDirGroup = new Label(content, SWT.NONE);
		webDirGroup.setText(LocaleMessage.getString("web.home"));
		webHomeText = new Text(content, SWT.BORDER);
		webHomeText.setEditable(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		webHomeText.setLayoutData(gridData);
		String webDir = null;
		try {
			webDir = getProject().getPersistentProperty(WebQN);
			if (filtEmpty(webDir) != null) {
				webHomeText.setText(webDir);
			} else {
				webDir = ProjectUtil.autoGetWebHome(getProject());
				if (filtEmpty(webDir) != null) {
					webHomeText.setText(webDir);
				}
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		} catch (SystemException e) {
			DialogUtil.logErrorException(e);
		}
		Button webBrowseButton = new Button(content, SWT.PUSH);
		webBrowseButton.setText(LocaleMessage.getString("openBrowse"));
		webBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String path = openFolderSelectionDialog(LocaleMessage
						.getString("please.select.the.path"));
				if (null != path) {
					String errorMessage = validWebHome(getProject(), new Path(
							path));
					if (errorMessage != null) {
						DialogUtil.showErrorMessageBox(
								LocaleMessage.getString("check.failed"),
								errorMessage);
					} else {
						webHomeText.setText(path);
					}
				}
				// IContainer initSelection = getProject();
				// IFolder folder = ResourceUtil.getWebHomeFolder(getProject());
				// if (folder != null)
				// initSelection = folder;
				// ContainerSelectionDialog dialog = new
				// ContainerSelectionDialog(
				// Display.getCurrent().getActiveShell(), initSelection,
				// false, LocaleMessage
				// .getString("please.select.the.path"));
				// if (dialog.open() == ContainerSelectionDialog.OK) {
				// Object[] result = dialog.getResult();
				// if (result.length == 1) {
				// IPath selectionPath = (IPath) result[0];
				// String errorMessage = validWebHome(getProject(),
				// selectionPath);
				// if (errorMessage != null) {
				// DialogUtil.showErrorMessageBox(
				// LocaleMessage.getString("check.failed"),
				// errorMessage);
				// return;
				// }
				// webHomeText.setText(selectionPath.toString());
				// bmHomeText.setText(selectionPath.append("WEB-INF")
				// .append("classes").toString());
				// }
				// }
			}
		});
		// BMDir
		Label bmDirLabel = new Label(content, SWT.NONE);
		bmDirLabel.setText(LocaleMessage.getString("bm.home"));
		bmHomeText = new Text(content, SWT.BORDER);
		bmHomeText.setEditable(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		bmHomeText.setLayoutData(gridData);
		String bmDir = null;
		try {
			bmDir = getProject().getPersistentProperty(BMQN);
			if (filtEmpty(bmDir) != null) {
				bmHomeText.setText(bmDir);
			} else {
				bmDir = ProjectUtil.autoGetBMHome(getProject());
				if (filtEmpty(bmDir) != null) {
					bmHomeText.setText(bmDir);
				}
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		} catch (SystemException e) {
			DialogUtil.logErrorException(e);
		}

		Button bmBrowseButton = new Button(content, SWT.PUSH);
		bmBrowseButton.setText(LocaleMessage.getString("openBrowse"));
		bmBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String path = openFolderSelectionDialog(LocaleMessage
						.getString("please.select.the.path"));
				if (null != path) {
					String errorMessage = validBMHome(getProject(), new Path(
							path));
					if (errorMessage != null) {
						DialogUtil.showErrorMessageBox(
								LocaleMessage.getString("check.failed"),
								errorMessage);
					} else
						bmHomeText.setText(path);
				}
				// IContainer initSelection = getProject();
				// IFolder folder = ResourceUtil.getBMHomeFolder(getProject());
				// if (folder != null)
				// initSelection = folder;
				// ContainerSelectionDialog dialog = new
				// ContainerSelectionDialog(
				// Display.getCurrent().getActiveShell(), initSelection,
				// false, LocaleMessage
				// .getString("please.select.the.path"));
				// if (dialog.open() == ContainerSelectionDialog.OK) {
				// Object[] result = dialog.getResult();
				// if (result.length == 1) {
				// IPath selectionPath = (IPath) result[0];
				// String errorMessage = validBMHome(getProject(),
				// selectionPath);
				// if (errorMessage != null) {
				// DialogUtil.showErrorMessageBox(
				// LocaleMessage.getString("check.failed"),
				// errorMessage);
				// return;
				// }
				// bmHomeText.setText(selectionPath.toString());
				// }
				// }
			}
		});

		// Label dbloginTimeOut = new Label(content, SWT.NONE);
		// dbloginTimeOut.setText(LocaleMessage.getString("preview.url"));
		// Text dbloginTimeOutText = new Text(content, SWT.BORDER);
		// gridData = new GridData(GridData.FILL_HORIZONTAL);
		// gridData.horizontalSpan = 2;
		// dbloginTimeOutText.setLayoutData(gridData);
		// try {
		// String time = getProject().getPersistentProperty(new QualifiedName(
		// AuroraPlugin.PLUGIN_ID,"dbloginTimeOutText" ));
		// if (filtEmpty(time) != null) {
		// dbloginTimeOutText.setText(time);
		// }
		// } catch (Throwable e) {
		// DialogUtil.showExceptionMessageBox(e);
		// }

		Button testConn = new Button(content, SWT.PUSH);
		testConn.setText(LocaleMessage.getString("test.database"));
		gridData = new GridData(GridData.BEGINNING);
		gridData.horizontalSpan = 3;
		testConn.setLayoutData(gridData);
		testConn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (!checkInput()) {
					if (getErrorMessage() != null) {
						DialogUtil.showErrorMessageBox(getErrorMessage());
					}
					return;
				}
				saveInput();
				try {
					DBConnectionUtil.testDBConnection(getProject(),
							webHomeText.getText());
				} catch (ApplicationException ae) {
					DialogUtil.showErrorMessageBox(
							LocaleMessage.getString("check.failed"),
							ExceptionUtil.getExceptionTraceMessage(ae));
					return;
				}
				DialogUtil.showMessageBox(SWT.ICON_INFORMATION, "OK",
						LocaleMessage.getString("test.database.ok"));
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// debugButton = new Button(content, SWT.CHECK);
		// debugButton.setText("调试模式 ( 记录IDE运行日志,不建议开启. )");
		// String debugMode = null;
		// try {
		// debugMode = getProject().getPersistentProperty(DebugModeQN);
		// } catch (CoreException e) {
		// DialogUtil.showExceptionMessageBox(e);
		// }
		// if ("true".equals(debugMode)) {
		// debugButton.setSelection(true);
		// }
		// gridData = new GridData(GridData.FILL_HORIZONTAL);
		// gridData.horizontalSpan = 3;
		// debugButton.setLayoutData(gridData);

		cb_isBuild = new Button(content, SWT.CHECK);
		cb_isBuild.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		cb_isBuild.setText(LocaleMessage.getString("build.now"));

		cb_isBuild.setSelection(getStoredBuildOption());
		new Label(content, SWT.NONE);
		return content;
	}

	private boolean checkInput() {
		IProject project = getProject();
		if (webHomeText.getText() == null || "".equals(webHomeText.getText())) {
			setErrorMessage(LocaleMessage.getString("require.webhome"));
			return false;
		}
		String msg = validWebHome(project, new Path(webHomeText.getText()));
		if (msg != null) {
			setErrorMessage(msg);
			return false;
		}
		msg = validWebHome(project, new Path(bmHomeText.getText()));
		if (msg != null) {
			setErrorMessage(msg);
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	private void saveInput() {
		try {
			// project.setPersistentProperty(DebugModeQN,
			// String.valueOf(debugButton.getSelection()));
			IProject project = getProject();
			project.setPersistentProperty(LoclaUrlHomeQN,
					localWebUrlText.getText());
			project.setPersistentProperty(WebQN, webHomeText.getText());
			project.setPersistentProperty(BMQN, bmHomeText.getText());
			project.setPersistentProperty(buildNow,
					Boolean.toString(cb_isBuild.getSelection()));
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public boolean performOk() {
		if (checkInput()) {
			saveInput();
			if (cb_isBuild.getSelection()) {
				Display.getCurrent().asyncExec(this);
			}
			return true;
		}
		return false;
	}

	private IProject getProject() {
		return (IProject) getElement();
	}

	public static String filtEmpty(String str) {
		if ("".equals(str))
			return null;
		return str;
	}

	public static String validWebHome(IProject project, IPath path) {
		IResource selectionResource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);
		if (selectionResource == null) {
			return LocaleMessage.getString("check.folder.not.exists");
		}
		if (!project.equals(selectionResource.getProject())) {
			return LocaleMessage.getString("check.folder.not.in.project");
		}
		String locationPath = selectionResource.getLocation().toOSString();
		if (locationPath == null) {
			return LocaleMessage.getString("check.folder.not.in.os");
		}
		return null;
	}

	public static String validBMHome(IProject project, IPath path) {
		String classesDir = "classes";
		IResource selectionResource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);
		if (selectionResource == null) {
			return LocaleMessage.getString("check.folder.not.exists");
		}
		if (!project.equals(selectionResource.getProject())) {
			return LocaleMessage.getString("check.folder.not.in.project");
		}
		String locationPath = selectionResource.getLocation().toOSString();
		if (locationPath == null) {
			return LocaleMessage.getString("check.folder.not.in.os");
		}
		if (!classesDir.equals(selectionResource.getName().toLowerCase())) {
			return String.format(LocaleMessage.getString("must.be.classes"),
					classesDir);
		}
		return null;
	}

	@Override
	protected void performDefaults() {
		IProject proj = getProject();
		IFolder folder = ResourceUtil.searchWebInf(getProject());
		if (folder == null) {
			return;
		}
		IResource clsFolder = folder.findMember("classes");
		if (clsFolder instanceof IFolder) {
			try {
				localWebUrlText.setText(ProjectUtil.autoGetLocalWebUrl(proj));
				webHomeText.setText(ProjectUtil.autoGetWebHome(proj));
				bmHomeText.setText(ProjectUtil.autoGetBMHome(proj));
				cb_isBuild.setSelection(false);
			} catch (ApplicationException e) {
				e.printStackTrace();
			}
		}
		super.performDefaults();
	}

	private boolean getStoredBuildOption() {
		String str = null;
		try {
			str = getProject().getPersistentProperty(buildNow);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (str == null)
			return false;
		return Boolean.parseBoolean(str);
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected String openFolderSelectionDialog(String msg) {
		AuroraProject ap = new AuroraProject(getProject());
		return ap.openFolderSelectionDialog(msg, this.getShell());
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
}
