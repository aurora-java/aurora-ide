package aurora.ide.core.bm.test.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.ViewPart;

import aurora.ide.core.actions.EditorViewerLinkAction;
import aurora.ide.core.debug.ITestViewerPart;

public class BMTestViewer extends ViewPart implements ITestViewerPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "aaaaviewer.views.SampleView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private EditorViewerLinkAction linkHelper;

	private StyledText delete;

	private StyledText update;

	private StyledText insert;

	private StyledText query;

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
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
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
	public BMTestViewer() {

		linkHelper = new EditorViewerLinkAction(this);
		linkHelper.activate();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		Group g1 = new Group(parent, SWT.NONE);
		g1.setLayoutData(new GridData(GridData.FILL_BOTH));
		g1.setText("Query");
		g1.setLayout(new GridLayout());
		query = new StyledText(g1, SWT.READ_ONLY);
		query.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group g2 = new Group(parent, SWT.NONE);
		g2.setLayoutData(new GridData(GridData.FILL_BOTH));
		g2.setText("Insert");

		g2.setLayout(new GridLayout());
		insert = new StyledText(g2, SWT.READ_ONLY);
		insert.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group g3 = new Group(parent, SWT.NONE);
		g3.setLayoutData(new GridData(GridData.FILL_BOTH));
		g3.setText("Update");

		g3.setLayout(new GridLayout());
		update = new StyledText(g3, SWT.READ_ONLY);
		update.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group g4 = new Group(parent, SWT.NONE);
		g4.setLayoutData(new GridData(GridData.FILL_BOTH));
		g4.setText("Delete");

		g4.setLayout(new GridLayout());
		delete = new StyledText(g4, SWT.READ_ONLY);
		delete.setLayoutData(new GridData(GridData.FILL_BOTH));

		// viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL |
		// SWT.V_SCROLL);
		// viewer.setContentProvider(new ViewContentProvider());
		// viewer.setLabelProvider(new ViewLabelProvider());
		// viewer.setSorter(new NameSorter());
		// viewer.setInput(getViewSite());
		//
		// // Create the help context id for the viewer's control
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
		// "aaaaviewer.viewer");
		// makeActions();
		// hookContextMenu();
		// hookDoubleClickAction();
		// contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				BMTestViewer.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
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
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Sample View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// viewer.getControl().setFocus();
	}

	public void dispose() {
		linkHelper.deactivated();
		super.dispose();
	}

	@Override
	public void editorChanged(IEditorPart activeEditor) {

		IFile file = ResourceUtil.getFile(activeEditor.getEditorInput());
		if (file != null) {
			String fileExtension = file.getFileExtension();
			if ("bm".equals(fileExtension)) {
				BM2SQLHelper b2s = new BM2SQLHelper(file);
				try {
					query.setText(b2s.getSQL("query"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					query.setText(localizedMessage==null?"":localizedMessage);
				}
				try {
					insert.setText(b2s.getSQL("insert"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					insert.setText(localizedMessage==null?"":localizedMessage);
				}
				try {
					update.setText(b2s.getSQL("update"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					update.setText(localizedMessage==null?"":localizedMessage);
				}
				try {
					delete.setText(b2s.getSQL("delete"));
				} catch (Exception e) {
					String localizedMessage = e.getLocalizedMessage();
					delete.setText(localizedMessage==null?"":localizedMessage);
				}
				
//				try {
//					String[] sqLs = b2s.getSQLs();
//					this.query.setText(sqLs[0]);
//					insert.setText(sqLs[1]);
//					update.setText(sqLs[2]);
//					delete.setText(sqLs[3]);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
				// StyledText st = (StyledText)
				// tabFolder.getItem(i).getControl();
				// try {
				// SQLFormat sf = new SQLFormat();
				// st.setText(sf.format(modelService.getSql(tabs[i]).toString()));
			}
		}
	}

}