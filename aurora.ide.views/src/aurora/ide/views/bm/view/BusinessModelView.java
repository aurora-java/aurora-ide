package aurora.ide.views.bm.view;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.project.AuroraProject;
import aurora.ide.screen.editor.ServiceEditor;
import aurora.ide.views.Activator;
import aurora.ide.views.IListener;
import aurora.ide.views.bm.BMTransferDropTargetListener;

public class BusinessModelView extends ViewPart {

	private IListener moduleChangedListener = new IListener() {

		@Override
		public void handleEvent(Object object) {
			modelsViewer.setInput((IFolder) object);
		}
	};
	private IPartListener partListener = new IPartListener() {

		@Override
		public void partActivated(IWorkbenchPart part) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partOpened(IWorkbenchPart part) {
			if (part instanceof ServiceEditor) {
				TextPage textPage = (TextPage) ((ServiceEditor) part)
						.getTextPage();
				StyledText textWidget = (StyledText) textPage
						.getAdapter(StyledText.class);
				BMTransferDropTargetListener listener = new BMTransferDropTargetListener(
						textPage);
				DropTarget realDropTarget = (DropTarget) textWidget
						.getData(DND.DROP_TARGET_KEY);
				if (realDropTarget == null)
					return;
				realDropTarget.addDropListener(listener);
				Transfer[] transfers = realDropTarget.getTransfer();
				Transfer[] allTransfers = new Transfer[transfers.length + 1];
				int curTransfer = 0;
				for (int i = 0; i < transfers.length; i++) {
					allTransfers[curTransfer++] = transfers[i];
				}
				allTransfers[curTransfer++] = listener.getTransfer();
				realDropTarget.setTransfer(allTransfers);
			}
		}
	};

	private ModulesComposite modulesComposite;
	private BMViewer modelsViewer;
	private Action projectSelectionAtion;

	public BusinessModelView() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IPartService partService = window.getPartService();
		partService.addPartListener(partListener);
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage != null) {
			IEditorReference[] editorReferences = activePage
					.getEditorReferences();
			for (IEditorReference e : editorReferences) {
				IEditorPart editor = e.getEditor(false);
				partListener.partOpened(editor);
			}
		}
	}

	@Override
	public void dispose() {
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		window.getPartService().removePartListener(partListener);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		// SashForm sf = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);

		Composite p = new Composite(parent, SWT.NONE);
		p.setLayout(new GridLayout(2, false));
		ScrolledComposite sc1 = new ScrolledComposite(p, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		sc1.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		modulesComposite = new ModulesComposite(sc1, SWT.NONE);
		sc1.setMinSize(70, 400);
		sc1.setContent(modulesComposite);

		// modulesComposite.setLayout(new GridLayout());
		modulesComposite.addListener(moduleChangedListener);

		// test.aurora.project
		// IProject project = ResourcesPlugin.getWorkspace().getRoot()
		// .getProject("test.aurora.project");
		modelsViewer = new BMViewer(p);
		modelsViewer.getViewer().addDoubleClickListener(
				new IDoubleClickListener() {

					@Override
					public void doubleClick(DoubleClickEvent event) {
						Object selectObject = modelsViewer.getSelectObject();
						if (selectObject instanceof IFile) {
							try {
								IDE.openEditor(getSite().getPage(),
										(IFile) selectObject, true);
							} catch (PartInitException e) {
								DialogUtil.logErrorException(e);
							}
						}
					}
				});
		createActions();
		contributeToActionBars();
	}

	public void setInput(IProject p) {
		AuroraProject ap = new AuroraProject(p);
		IFolder web_classes = ap.getWeb_classes();
		if (web_classes != null)
			setInput(web_classes);
		// modelsViewer
	}

	private void setInput(IFolder web_classes) {
		modulesComposite.setInput(web_classes);
		modelsViewer.setInput(web_classes);
	}

	@Override
	public void setFocus() {

	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IMenuManager menuManager = bars.getMenuManager();
		fillLocalPullDown(menuManager);
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		// manager.add(action1);
		// manager.add(action2);
	}


	private void createActions() {

		projectSelectionAtion = new Action("Project Selection",
				Action.AS_DROP_DOWN_MENU) {
			private List<IProject> projects;
			{
				this.setId("projectSelectionAtion");
				projects = ProjectUtil.getALLAuroraProjects();
			}

			@Override
			public IMenuCreator getMenuCreator() {
				return new MenuCreator();
			}

			@Override
			public void run() {
				if (projects != null && projects.size() > 0) {
					setInput(projects.get(0));
				}
			}

			class MenuCreator implements IMenuCreator, SelectionListener {


				public void dispose() {

				}

				public Menu getMenu(Control parent) {
					final Menu menu = new Menu(parent);
					menu.addMenuListener(new MenuListener() {

						@Override
						public void menuShown(MenuEvent e) {
							fillMenu(menu);
						}

						@Override
						public void menuHidden(MenuEvent e) {

						}
					});
					return menu;
				}

				public void fillMenu(Menu menu) {
					projects = ProjectUtil.getALLAuroraProjects();
					for (IProject p : projects) {
						MenuItem mi = new MenuItem(menu, SWT.NONE);
						mi.setText("Load : " + p.getName());
						mi.setData(p);
						mi.setImage(PrototypeImagesUtils.getImage("prj_obj.gif"));
						mi.addSelectionListener(this);
					}
				}


				public Menu getMenu(Menu parent) {
					final Menu menu = new Menu(parent);
					menu.addMenuListener(new MenuListener() {
						@Override
						public void menuShown(MenuEvent e) {
							fillMenu(menu);
						}
						@Override
						public void menuHidden(MenuEvent e) {
						}
					});

					return menu;
				}

				public void widgetSelected(SelectionEvent e) {
					MenuItem mi = (MenuItem) e.getSource();
					setInput((IProject) mi.getData());
				}

				public void widgetDefaultSelected(SelectionEvent e) {

				}
			}

		};
		projectSelectionAtion.setImageDescriptor(PrototypeImagesUtils
				.getImageDescriptor("prj_obj.gif"));
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(this.projectSelectionAtion);
	}

}
