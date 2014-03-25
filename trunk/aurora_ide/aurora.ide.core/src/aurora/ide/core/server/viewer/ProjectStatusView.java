package aurora.ide.core.server.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import aurora.datasource.DatabaseConnection;
import aurora.ide.AuroraProjectNature;
import aurora.ide.core.server.launch.AuroraServerLauncher;
import aurora.ide.core.server.launch.AuroraServerManager;
import aurora.ide.core.server.launch.ServerEvent;
import aurora.ide.core.server.launch.ServerListener;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ExceptionUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.project.AuroraProject;

public class ProjectStatusView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "aaaaviewer.views.SampleView";

	private TableViewer viewer;
	private Action runAction;
	private Action debugAction;
	private Action doubleClickAction;

	private Action terminateAction;

	private Action dbTestAction;

	private Action refreshAction;

	private Label statusLabel;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			List<IProject> ps = new ArrayList<IProject>();

			if (parent instanceof IWorkspaceRoot) {
				IProject[] projects = ((IWorkspaceRoot) parent).getProjects();
				for (int i = 0; i < projects.length; i++) {
					try {
						boolean hasAuroraNature = AuroraProjectNature
								.hasAuroraNature(projects[i]);
						if (hasAuroraNature) {
							ps.add(projects[i]);
						}
					} catch (CoreException e) {
					}
				}
			}

			return ps.toArray(new IProject[ps.size()]);
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (index == 1) {
				return AuroraServerManager.TERMINATE == AuroraServerManager
						.getInstance().getServerKind((IProject) obj) ? "未运行"
						: "运行中";
			}
			if (index == 3) {
				AuroraProject auroraProject = new AuroraProject((IProject) obj);
				DatabaseConnection defaultDatasourceConfig = auroraProject
						.getDefaultDatasourceConfig();
				return defaultDatasourceConfig == null ? "数据源无效"
						: defaultDatasourceConfig.getUrl();
			}
			if (index == 0) {
				if (obj instanceof IProject) {
					return ((IProject) obj).getName();
				}
			}

			return "";
		}

		public Image getColumnImage(Object obj, int index) {
			if (index != 0)
				return null;
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public ProjectStatusView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setWidth(160);
		column1.setText("项目名");

		// TableColumn column6 = new TableColumn(table, SWT.NONE);
		// column6.setWidth(80);
		// column6.setText("运行目录");

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setWidth(80);
		column2.setText("运行状态");

		TableColumn column3 = new TableColumn(table, SWT.NONE);
		column3.setWidth(80);
		column3.setText("调试状态");

		TableColumn column4 = new TableColumn(table, SWT.NONE);
		column4.setWidth(200);
		column4.setText("默认数据源");

		statusLabel = new Label(parent, SWT.NONE);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		updateErrMsg(null);
		viewer.setInput(getViewerInput());
		configuration();

	}

	private void configuration() {
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		AuroraServerManager.getInstance().addListener(new ServerListener() {
			@Override
			public void handleEvent(ServerEvent event) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						refreshAction.run();
						int serverKind = AuroraServerManager.getInstance()
								.getServerKind(getSelectionProject());
						boolean enabled = AuroraServerManager.TERMINATE == serverKind;
						updateActionStatus(enabled);
					}
				});
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectionChange(event.getSelection());
			}

		});
	}

	private void selectionChange(ISelection selection) {
		IProject selectionProject = getSelectionProject();
		if (selectionProject == null) {
			this.runAction.setEnabled(false);
			this.debugAction.setEnabled(false);
			this.terminateAction.setEnabled(false);
			return;
		}
		int serverKind = AuroraServerManager.getInstance().getServerKind(
				selectionProject);
		boolean enabled = AuroraServerManager.TERMINATE == serverKind;
		updateActionStatus(enabled);
	}

	public void updateActionStatus(boolean enabled) {
		this.terminateAction.setEnabled(!enabled);
		this.runAction.setEnabled(enabled);
		this.debugAction.setEnabled(enabled);
	}

	private Object getViewerInput() {
		// Activator.getDefault().get
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		return root;
	}

	private void updateErrMsg(String s) {
		if (statusLabel != null && !statusLabel.isDisposed()) {
			boolean error = s != null;
			if (error) {
				statusLabel.setText(s);
			}
			statusLabel.setEnabled(error);
			statusLabel.setVisible(error);
			statusLabel.getParent().layout();
			// .update();
		}

		// this.errorMessage = errorMessage;
		// if (errorMessageText != null && !errorMessageText.isDisposed()) {
		//    		errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
		// // Disable the error message text control if there is no error, or
		// // no error text (empty or whitespace only). Hide it also to avoid
		// // color change.
		// // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
		// boolean hasError = errorMessage != null &&
		// (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
		// errorMessageText.setEnabled(hasError);
		// errorMessageText.setVisible(hasError);
		// errorMessageText.getParent().update();
		// // Access the ok button by id, in case clients have overridden button
		// creation.
		// // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
		// Control button = getButton(IDialogConstants.OK_ID);
		// if (button != null) {
		// button.setEnabled(errorMessage == null);
		// }
		// }
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProjectStatusView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		// fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(runAction);
		manager.add(new Separator());
		// manager.add(debugAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(runAction);
		manager.add(dbTestAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		// manager.add(debugAction);
		manager.add(runAction);
		manager.add(terminateAction);
		manager.add(refreshAction);
	}

	private void makeActions() {

		//

		refreshAction = new Action() {
			public void run() {
				viewer.setInput(getViewerInput());
			}
		};
		refreshAction.setText("Action 1");
		refreshAction.setToolTipText("Action 1 tooltip");
		refreshAction.setImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("refresh.gif"));
		// refreshAction.setDisabledImageDescriptor(AuroraImagesUtils
		// .getImageDescriptor("debug/run_tool.gif"));

		runAction = new Action() {
			public void run() {
				try {
					AuroraServerLauncher.launch(getSelectionProject());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		};
		runAction.setText("Action 1");
		runAction.setToolTipText("Action 1 tooltip");
		runAction.setImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("debug/run_tool(1).gif"));
		runAction.setDisabledImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("debug/run_tool.gif"));

		debugAction = new Action() {
			public void run() {

				// hai buzhichi
			}
		};
		debugAction.setText("Action 2");
		debugAction.setToolTipText("Action 2 tooltip");
		debugAction.setImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("debug/debug_exc(1).gif"));
		debugAction.setDisabledImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("debug/debug_exc.gif"));

		terminateAction = new Action() {
			public void run() {
				AuroraServerManager.getInstance().terminate(
						getSelectionProject());
			}
		};
		terminateAction.setEnabled(false);
		terminateAction.setText("Action 1");
		terminateAction.setToolTipText("Action 1 tooltip");
		terminateAction.setImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("debug/terminate_co(1).gif"));
		terminateAction.setDisabledImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("debug/terminate_co.gif"));

		dbTestAction = new Action() {
			public void run() {
				// DBConnectionUtil.testDBConnection(project, webHome)

				try {
					DBConnectionUtil.testDBConnection(getSelectionProject(),
							null);
				} catch (ApplicationException ae) {
					DialogUtil.showErrorMessageBox(
							LocaleMessage.getString("check.failed"),
							ExceptionUtil.getExceptionTraceMessage(ae));
					return;
				}
				DialogUtil.showMessageBox(SWT.ICON_INFORMATION, "OK",
						LocaleMessage.getString("test.database.ok"));

			}
		};
		// dbTestAction.setEnabled(false);
		dbTestAction.setText("数据库链接测试");
		dbTestAction.setToolTipText("数据库链接测试");
		// dbTestAction.setImageDescriptor(AuroraImagesUtils
		// .getImageDescriptor("debug/terminate_co(1).gif"));
		// dbTestAction.setDisabledImageDescriptor(AuroraImagesUtils
		// .getImageDescriptor("debug/terminate_co.gif"));

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Sample View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public IProject getSelectionProject() {
		ISelection selection = this.viewer.getSelection();
		if (selection.isEmpty() == false) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			return (IProject) ss.getFirstElement();
		}

		return null;
	}
}