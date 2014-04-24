package aurora.ide.prototype.consultant.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.FileExplorer;
import aurora.ide.meta.gef.editors.ConsultantVScreenEditor;
import aurora.ide.meta.gef.message.Messages;
import aurora.ide.prototype.consultant.editor.FSDEditor;
import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.prototype.consultant.product.ICommandIds;
import aurora.ide.prototype.consultant.view.action.CollapseAllAction;
import aurora.ide.prototype.consultant.view.action.FunctionExportFSDAction;
import aurora.ide.prototype.consultant.view.action.LinkEditorAction;
import aurora.ide.prototype.consultant.view.action.NewMenuAction;
import aurora.ide.prototype.consultant.view.action.OpenLocalProjectAction;
import aurora.ide.prototype.consultant.view.action.PopMenuManager;
import aurora.ide.prototype.consultant.view.action.ProjectExportFSDAction;
import aurora.ide.prototype.consultant.view.action.RefreshLocalFileSystemAction;
import aurora.ide.prototype.consultant.view.action.RemoveLocalFolderAction;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;

public class NavigationView extends ViewPart {
	public static final String ID = "aurora.ide.prototype.consultant.view.navigationView"; //$NON-NLS-1$
	private TreeViewer viewer;
	private CollapseAllAction collapseAllAction;
	private CollapseAllHandler collapseAllHandler;
	private LinkEditorAction toggleLinkingAction;
	private RefreshLocalFileSystemAction refreshLocalFileSystemAction;
	private Action removeLocalFolderAction;
	private Action openLocalFolderActionAction;
	private boolean isLinkingEnabled;

	private PopMenuManager pmm;

	public static final int IS_LINKING_ENABLED_PROPERTY = 0x10000;

	private IPropertyChangeListener partPropertyChangeListener = new IPropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			if (viewer.getControl().isDisposed() == false) {
				Object newValue = event.getNewValue();
				if (newValue instanceof String) {
					Object input = viewer.getInput();
					if (input instanceof Root) {
						List<Node> children = ((Root) input).getChildren();
						for (Node node : children) {
							if (node.getPath().isPrefixOf(
									new Path((String) newValue))) {
								viewer.refresh(node);
								Node findNode = new NodeLinkHelper(
										NavigationView.this).findNode(new Path(
										(String) newValue), node);
								if (findNode != null)
									selectReveal(findNode);
								return;
							}
						}
					}
				}
			}
		}
	};

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = createViewer(parent);
		viewer.setInput(getInitialInput());
		getSite().setSelectionProvider(viewer);
		setPartName(aurora.ide.meta.gef.message.Messages.ApplicationActionBarAdvisor_17); //$NON-NLS-1$
		makeActions();
		fillActionBars(getViewSite().getActionBars());
		initContextMenu();
		initListeners(viewer);
		configration();
		this.getSite().setSelectionProvider(viewer);

	}

	private UIJob viewerExpandJob = new UIJob("Expand") { //$NON-NLS-1$
		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (viewer.getControl().isDisposed() == false) {
				SafeRunner.run(new SafeRunnable() {
					public void run() throws Exception {
						// viewer.expandAll();
						// viewer.collapseAll();
						// if (selection != null)
						// viewer.setSelection(selection, true);
						// selection = null;
					}
				});
			}
			return Status.OK_STATUS;
		}
	};
	private ISelection selection;
	private NewMenuAction newMenuAction;
	private OpenLocalProjectAction openLocalProjectAction;

	private void configration() {
		// viewerExpandJob.schedule(100);
	}

	public void refreshViewer() {
		selection = viewer.getSelection();
		viewer.setInput(getInitialInput());
		configration();
	}

	private void fillActionBars(IActionBars actionBars) {
		fillToolBar(actionBars.getToolBarManager());
		// fillViewMenu(actionBars.getMenuManager());
	}

	private void makeActions() {
		IHandlerService service = (IHandlerService) getSite().getService(
				IHandlerService.class);
		collapseAllAction = new CollapseAllAction(viewer);
		ImageDescriptor collapseAllIcon = aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/collapseall.gif"); //$NON-NLS-1$
		collapseAllAction.setImageDescriptor(collapseAllIcon);
		collapseAllAction.setHoverImageDescriptor(collapseAllIcon);
		collapseAllHandler = new CollapseAllHandler(viewer);
		service.activateHandler(CollapseAllHandler.COMMAND_ID,
				collapseAllHandler);

		toggleLinkingAction = new LinkEditorAction(this, viewer);
		ImageDescriptor syncIcon = Activator
				.getImageDescriptor("/icons/synced.gif"); //$NON-NLS-1$
		toggleLinkingAction.setImageDescriptor(syncIcon);
		toggleLinkingAction.setHoverImageDescriptor(syncIcon);

		// openLocalFolderActionAction = new OpenLocalFolderAction(this);
		//		ImageDescriptor icon = Activator.getImageDescriptor("/icons/open.gif"); //$NON-NLS-1$
		// openLocalFolderActionAction.setImageDescriptor(icon);
		// openLocalFolderActionAction.setHoverImageDescriptor(icon);

		removeLocalFolderAction = new RemoveLocalFolderAction(this);
		ImageDescriptor icon = Activator.getDefault().getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE); //$NON-NLS-1$
		removeLocalFolderAction.setImageDescriptor(icon);
		removeLocalFolderAction.setHoverImageDescriptor(icon);

		refreshLocalFileSystemAction = new RefreshLocalFileSystemAction(this);
		icon = Activator.getImageDescriptor("/icons/nav_refresh.gif"); //$NON-NLS-1$
		refreshLocalFileSystemAction.setImageDescriptor(icon);
		refreshLocalFileSystemAction.setHoverImageDescriptor(icon);

		newMenuAction = new NewMenuAction(this);
		icon = aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/sample2.gif"); //$NON-NLS-1$
		newMenuAction.setImageDescriptor(icon);
		newMenuAction.setHoverImageDescriptor(icon);

		openLocalProjectAction = new OpenLocalProjectAction(this);
		icon = aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/open.gif"); //$NON-NLS-1$
		openLocalProjectAction.setImageDescriptor(icon);
		openLocalProjectAction.setHoverImageDescriptor(icon);

		pmm = new PopMenuManager(this);
	}

	protected void initContextMenu() {
		MenuManager menuMgr = new MenuManager("NavigationViewMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}

		});
		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);

	}

	protected void fillToolBar(IToolBarManager toolBar) {

		// if (openLocalFolderActionAction != null) {
		// toolBar.add(openLocalFolderActionAction);
		// }
		if (newMenuAction != null) {
			toolBar.add(newMenuAction);
		}
		if (openLocalProjectAction != null) {
			toolBar.add(openLocalProjectAction);
		}
		if (removeLocalFolderAction != null) {
			toolBar.add(removeLocalFolderAction);
		}
		if (refreshLocalFileSystemAction != null) {
			toolBar.add(refreshLocalFileSystemAction);
		}
		if (collapseAllAction != null) {
			toolBar.add(collapseAllAction);
		}

		if (toggleLinkingAction != null) {
			toolBar.add(toggleLinkingAction);
		}
	}

	private void fillContextMenu(IMenuManager menu) {
		// menu.add(new Separator("AAA"));

		Node node = getSelectionNode();
		if (node == null)
			return;
		pmm.fillContextMenu(menu);
		if (node.getFile().isFile()) {
			menu.add(new Action(Messages.NavigationView_1) {
				public void run() {
					Node node = getSelectionNode();
					File file = node.getFile();
					IEditorInput input = createEditorInput(file);
					IWorkbenchPage page = getViewSite().getWorkbenchWindow()
							.getActivePage();
					try {
						page.openEditor(input, FSDEditor.EDITOR_ID);
					} catch (PartInitException e) {
					}

				}
			});
		}

		Node selectionNode = this.getSelectionNode();
		if (ResourceUtil.isProject(selectionNode.getFile())) {
			menu.add(new ProjectExportFSDAction(this, Messages.NavigationView_2));
		}
		if (ResourceUtil.isModule(selectionNode.getFile())) {
			menu.add(new ProjectExportFSDAction(this, Messages.NavigationView_3));
		}
		if (ResourceUtil.isFunction(selectionNode.getFile())) {
			menu.add(new FunctionExportFSDAction(this,
					Messages.NavigationView_5));
		}

		if (getSelectionNode() != null)
			menu.add(new Action(
					aurora.ide.prototype.consultant.view.Messages.NavigationView_4) { //$NON-NLS-1$

				@Override
				public void run() {
					Node node = getSelectionNode();
					if (node.getFile().exists() == false) {
						MessageDialog.openInformation(getViewer().getControl()
								.getShell(), "Info", Messages.NavigationView_7); //$NON-NLS-1$
						return;
					}
					if (node.getFile().isFile()) {
						node = node.getParent();
					}
					FileExplorer.open(node.getPath().toOSString());
				}

			});

		menu.add(new Action(Messages.NavigationView_8) {
			public void run() {
				IHandlerService hs = (IHandlerService) PlatformUI
						.getWorkbench().getAdapter(IHandlerService.class);
				try {
					hs.executeCommand("org.eclipse.ui.file.properties", null); //$NON-NLS-1$
				} catch (ExecutionException e) {
					DialogUtil.showExceptionMessageBox(e);
				} catch (NotDefinedException e) {
					DialogUtil.showExceptionMessageBox(e);
				} catch (NotEnabledException e) {
					DialogUtil.showExceptionMessageBox(e);
				} catch (NotHandledException e) {
					DialogUtil.showExceptionMessageBox(e);
				}
			}
		});

		// menu.add(new Separator(GROUP_COPY));
		// menu.add(new Separator(GROUP_PRINT));
		// menu.add(new Separator(GROUP_EDIT));
		// menu.add(new Separator(GROUP_VIEW));
		// menu.add(new Separator(GROUP_FIND));
		// menu.add(new Separator(GROUP_ADD));
		// menu.add(new Separator(GROUP_REST));
		// menu.add(new Separator(MB_ADDITIONS));
		// menu.add(new Separator(GROUP_SAVE));

	}

	protected void fillViewMenu(IMenuManager menu) {
		// menu.add(new Separator());
		// menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		//		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS+"-end"));//$NON-NLS-1$
		// if (toggleLinkingAction != null) {
		// menu
		// .insertAfter(IWorkbenchActionConstants.MB_ADDITIONS
		//					+ "-end", toggleLinkingAction); //$NON-NLS-1$
		// }
	}

	protected TreeViewer createViewer(Composite parent) {

		TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(new CNFContentProvider());
		viewer.setLabelProvider(new CNFLabelProvider());
		viewer.setSorter(new CNFViewerSorter());
		return viewer;

	}

	protected void initListeners(TreeViewer viewer) {

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				SafeRunner.run(new SafeRunnable() {
					public void run() throws Exception {
						handleDoubleClick(event);
					}
				});
			}
		});
		this.getViewSite().getPage().addPartListener(new IPartListener() {

			public void partActivated(IWorkbenchPart part) {
			}

			public void partBroughtToTop(IWorkbenchPart part) {
			}

			public void partClosed(IWorkbenchPart part) {
				if (part instanceof ConsultantVScreenEditor) {
					((ConsultantVScreenEditor) part)
							.removePartPropertyListener(partPropertyChangeListener);
				}
			}

			public void partDeactivated(IWorkbenchPart part) {
			}

			public void partOpened(IWorkbenchPart part) {
				if (part instanceof ConsultantVScreenEditor) {
					((ConsultantVScreenEditor) part)
							.addPartPropertyListener(partPropertyChangeListener);
				}
			}
		});
	}

	protected void handleDoubleClick(DoubleClickEvent anEvent) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection.size() == 1) {
			Node node = getSelectionNode();
			File file = node.getFile();
			if (file.exists() == false) {
				MessageDialog.openInformation(getViewer().getControl()
						.getShell(), "Info", Messages.NavigationView_11); //$NON-NLS-1$
				return;
			}
			if (file.isDirectory()) {
				this.viewer.expandToLevel(node, 1);
			}
		}
		Object[] array = selection.toArray();
		for (Object node : array) {
			if (node instanceof Node) {
				File file = ((Node) node).getFile();
				if (file.isFile()) {
					IEditorInput input = createEditorInput(file);
					String editorId = getEditorId(file);
					IWorkbenchPage page = getViewSite().getWorkbenchWindow()
							.getActivePage();
					try {
						page.openEditor(input, editorId);
					} catch (PartInitException e) {
					}
				}
			}
		}
	}

	private String getEditorId(File file) {
		IWorkbench workbench = this.getViewSite().getWorkbenchWindow()
				.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(file
				.getName());
		if (descriptor != null)
			return descriptor.getId();
		return ICommandIds.EDITOR_ID;
	}

	private IEditorInput createEditorInput(File file) {
		IPath location = new Path(file.getAbsolutePath());
		PathEditorInput input = new PathEditorInput(location);
		return input;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public static IAdaptable getInitialInput() {
		Root root = new Root();
		NavViewSetting nvs = new NavViewSetting();
		String[] folders = nvs.getFolders();
		for (String string : folders) {
			root.addChild(new Node(new Path(string)));
		}
		return root;
	}

	public Node getSelectionNode() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		Node node = (Node) selection.getFirstElement();
		return node;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void selectReveal(ISelection selection) {
		if (viewer != null) {
			//
			// if (selection instanceof IStructuredSelection) {
			// IStructuredSelection sSelection = (IStructuredSelection)
			// selection;
			//
			// PipelinedViewerUpdate update = new PipelinedViewerUpdate();
			// update.getRefreshTargets().addAll(sSelection.toList());
			// update.setUpdateLabels(false);
			// /* if the update is modified */
			// /* intercept and apply the update */
			// viewer.setSelection(new
			// StructuredSelection(update.getRefreshTargets().toArray()), true);
			// }

			viewer.setSelection(selection, true);
		}
	}

	public final void setLinkingEnabled(boolean toEnableLinking) {
		isLinkingEnabled = toEnableLinking;
		firePropertyChange(IS_LINKING_ENABLED_PROPERTY);
	}

	/**
	 * @return Whether linking the current selection with open editors is
	 *         enabled.
	 */
	public final boolean isLinkingEnabled() {
		return isLinkingEnabled;
	}

	public void selectReveal(Node findNode) {

		selectReveal(new StructuredSelection(findNode));
	}

	public void addNewNode(final Object parent, Node child) {
		IPath path = child.getPath().removeLastSegments(1);
		Node[] nodes = findSamePathNodes(path);
		for (Node node : nodes) {
			if (node.equals(parent) == false) {
				Node cc = new Node(child.getPath());
				node.addChild(cc);
				getViewer().add(node, cc);
			}
		}
		((Node) parent).addChild(child);
		getViewer().add(parent, child);
		selectReveal(child);
	}

	public Node[] findSamePathNodes(Node n) {
		return findSamePathNodes(n.getPath());
	}

	public Node[] findSamePathNodes(IPath path) {
		List<Node> nodes = new ArrayList<Node>();
		Root input = (Root) this.getViewer().getInput();
		List<Node> children = input.getChildren();
		for (Node node : children) {
			if (node.getPath().isPrefixOf(path)) {
				NodeLinkHelper nlh = new NodeLinkHelper(this);
				Node findNode = nlh.findNode(path, node);
				if (findNode != null)
					nodes.add(findNode);
			}
		}
		return nodes.toArray(new Node[nodes.size()]);
	}

}