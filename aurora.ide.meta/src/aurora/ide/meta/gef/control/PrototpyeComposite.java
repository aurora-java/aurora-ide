package aurora.ide.meta.gef.control;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchEncoding;
import org.eclipse.ui.actions.ActionFactory;

import aurora.ide.editor.editorInput.StringEditorInput;
import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.VScreenEditorExtPaletteFactory;
import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.property.MetaPropertyViewer;
import aurora.ide.meta.gef.editors.property.PropertyManager;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class PrototpyeComposite extends GraphicalEditor implements
		ISelectionChangedListener {
	public void createPartControl(Composite parent) {

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

		SashForm c = new SashForm(sashForm, SWT.VERTICAL | SWT.BORDER);
		createBMViewer(c);
		createPropertyViewer(c);

		Composite cpt = new Composite(sashForm, SWT.NONE);
		cpt.setLayout(new GridLayout());
		Composite bottom = new Composite(cpt, SWT.NONE|SWT.BORDER);
		bottom.setLayoutData(new GridData(GridData.FILL_BOTH));
		bottom.setLayout(new FillLayout());

		super.createPartControl(bottom);
		sashForm.setWeights(new int[] { 1, 4 });
		this.setControl(sashForm);
	}

	private Control control;

	@Override
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
	}

	public static final String CONTEXT_MENU_KEY = "aurora.ide.gef.prototpye.composite.contextmenu";
	private ScreenBody diagram;
	private PaletteRoot root;
	private KeyHandler sharedKeyHandler;
	private MetaPropertyViewer propertyViewer;
	private EditorMode editorMode;

	// private IFile file;

	public void setDiagram(ScreenBody diagram) {
		this.diagram = diagram;
		GraphicalViewer gv = getGraphicalViewer();
		gv.setContents(diagram);
	}

	// public void markDirty() {
	// Command cmd = new Command() {
	//
	// };
	// this.getEditDomain().getCommandStack().execute(cmd);
	// firePropertyChange(IEditorPart.PROP_DIRTY);
	// }

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		// if (IFile.class.equals(adapter)) {
		// return file;
		// }

		return super.getAdapter(adapter);
	}

	/**
	 * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	protected void createActions() {
		super.createActions();
		// ActionRegistry registry = getActionRegistry();

		// IAction action;
		//
		// action = new DirectEditAction((IWorkbenchPart) this);
		// registry.registerAction(action);
		// getSelectionActions().add(action.getId());
	}

	/**
	 * Creates an appropriate output stream and writes the activity diagram out
	 * to this stream.
	 * 
	 * @param os
	 *            the base output stream
	 * @throws IOException
	 */
	// protected void createOutputStream(OutputStream os) throws IOException {
	// ModelIOManager mim = ModelIOManager.getNewInstance();
	// CompositeMap rootMap = mim.toCompositeMap(diagram);
	// String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
	// + rootMap.toXML();
	// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,
	// "UTF-8"));
	// bw.write(xml);
	// bw.close();
	// }

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	public void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(
				new ExtAuroraPartFactory(editorMode));
		getGraphicalViewer().setKeyHandler(
				new GraphicalViewerKeyHandler(getGraphicalViewer())
						.setParent(getCommonKeyHandler()));

		ContextMenuProvider provider = new ViewContextMenuProvider(
				getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(CONTEXT_MENU_KEY, //$NON-NLS-1$
				provider, getGraphicalViewer());
		getGraphicalViewer().addSelectionChangedListener(propertyViewer);

	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	public void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(diagram);
		// getGraphicalViewer().addDropTargetListener(
		// new BMTransferDropTargetListener(getGraphicalViewer()));
	}

	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {

	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler
					.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
							getActionRegistry().getAction(
									ActionFactory.DELETE.getId()));
			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(
							GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		if (root == null)
			root = VScreenEditorExtPaletteFactory.createPalette(this.editorMode);
		return root;
	}

	// public void gotoMarker(IMarker marker) {
	// }

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	public void setInput(IEditorInput input) {
		super.setInput(input);
	}

//	protected void createPropertyViewer(Composite c) {
//		propertyViewer = new MetaPropertyViewer(c, this);
//		DefaultEditDomain editDomain = getEditDomain();
//		if (editDomain == null)
//			return;
//		propertyViewer.setCommandStack(editDomain.getCommandStack());
//	}
	protected void createPropertyViewer(Composite c) {
		DefaultEditDomain editDomain = getEditDomain();
		propertyViewer = new MetaPropertyViewer(c, this,new PropertyManager(editDomain.getCommandStack()));
//		if (editDomain == null)
//			return;
//		propertyViewer.setCommandStack(editDomain.getCommandStack());
	}

	protected void createBMViewer(Composite c) {
	}

	public ScreenBody getDiagram() {
		return diagram;
	}

	protected void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
		getSite().setSelectionProvider(getGraphicalViewer());
		this.getGraphicalViewer().addSelectionChangedListener(this);
		ActionRegistry registry = getActionRegistry();

		SelectionAction sa = (SelectionAction) registry
				.getAction(ActionFactory.DELETE.getId());
		sa.setSelectionProvider(this.getGraphicalViewer());
	}

	public PrototpyeComposite() {
		super();
		this.setEditDomain(new DefaultEditDomain(this));
		editorMode = new EditorMode(null) {
			public String getMode() {
				return None;
			}

			public boolean isForDisplay() {
				return false;
			}

			public boolean isForCreate() {
				return false;
			}

			public boolean isForUpdate() {
				return false;
			}

			public boolean isForSearch() {
				return false;
			}
		};
		this.setInput(new StringEditorInput("",WorkbenchEncoding.getWorkbenchDefaultEncoding()));
		getCommandStack().addCommandStackListener(this);

		initializeActionRegistry();
	}

	public Control getControl() {
		return control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	public void setInput(ScreenBody viewDiagram) {
		this.diagram = viewDiagram;
		GraphicalViewer gv = getGraphicalViewer();
		if (gv != null)
			gv.setContents(diagram);
	}

	@Override
	public IEditorSite getEditorSite() {
		return super.getEditorSite();
	}

	@Override
	public IWorkbenchPartSite getSite() {
		return fakeSite;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		updateActions(this.getSelectionActions());
	}

	private IWorkbenchPartSite fakeSite = new IWorkbenchPartSite() {

		public IWorkbenchPage getPage() {

			IWorkbenchPage activePage = getWorkbenchWindow().getActivePage();
			return activePage;
		}

		public ISelectionProvider getSelectionProvider() {
			return getGraphicalViewer();
		}

		public Shell getShell() {
			return getControl().getShell();
		}

		public IWorkbenchWindow getWorkbenchWindow() {
			IWorkbench workbench = MetaPlugin.getDefault().getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			return window;
		}

		public void setSelectionProvider(ISelectionProvider provider) {
		}

		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
			return null;
		}

		public Object getService(@SuppressWarnings("rawtypes") Class api) {
			return null;
		}

		public boolean hasService(@SuppressWarnings("rawtypes") Class api) {
			return false;
		}

		public String getId() {
			return "PrototpyeComposite.fake.site";
		}

		public String getPluginId() {
			return MetaPlugin.PLUGIN_ID;
		}

		public String getRegisteredName() {
			return "PrototpyeComposite.fake.site.name";
		}

		public IWorkbenchPart getPart() {
			return PrototpyeComposite.this;
		}

		public void registerContextMenu(String menuId, MenuManager menuManager,
				ISelectionProvider selectionProvider) {

		}

		public void registerContextMenu(MenuManager menuManager,
				ISelectionProvider selectionProvider) {

		}

		@SuppressWarnings("deprecation")
		public IKeyBindingService getKeyBindingService() {
			return null;
		}
	};

	public void selectionChanged(SelectionChangedEvent event) {
		updateActions(this.getSelectionActions());

	}

}
