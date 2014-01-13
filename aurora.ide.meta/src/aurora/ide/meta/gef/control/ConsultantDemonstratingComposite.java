package aurora.ide.meta.gef.control;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.wizard.dialog.DemonstratingDialog;
import aurora.ide.prototype.consultant.demonstrate.DemonstrateEditorMode;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class ConsultantDemonstratingComposite extends GraphicalEditor implements
		ISelectionChangedListener {
	private Composite cpt;

	public void createPartControl(Composite parent) {
		cpt = new Composite(parent, SWT.NONE);
		cpt.setLayout(new GridLayout());
		Composite bottom = new Composite(cpt, SWT.NONE | SWT.BORDER);
		bottom.setLayoutData(new GridData(GridData.FILL_BOTH));
		bottom.setLayout(new FillLayout());

		super.createPartControl(bottom);
		this.setControl(cpt);
	}

	private Control control;

	@Override
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
	}

	public static final String CONTEXT_MENU_KEY = "aurora.ide.gef.prototpye.composite.contextmenu";
	private ScreenBody diagram;
	private EditorMode editorMode;
	private DemonstratingDialog parent;

	public void setDiagram(ScreenBody diagram) {
		this.diagram = diagram;
		GraphicalViewer gv = getGraphicalViewer();
		gv.setContents(diagram);
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {

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
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	public void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(
				parent.getPartFactory(editorMode));
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	public void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(diagram);
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

	public ScreenBody getDiagram() {
		return diagram;
	}

	protected void hookGraphicalViewer() {
		this.getGraphicalViewer().addSelectionChangedListener(this);
	}

	public ConsultantDemonstratingComposite(DemonstratingDialog sysLovDialog) {
		super();
		this.parent = sysLovDialog;
		this.setEditDomain(new DefaultEditDomain(this));
		editorMode = new DemonstrateEditorMode(parent);
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

	// @Override
	// public IWorkbenchPartSite getSite() {
	// return fakeSite;
	// }

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}

	// private IWorkbenchPartSite fakeSite = new IWorkbenchPartSite() {
	//
	// public IWorkbenchPage getPage() {
	//
	// IWorkbenchPage activePage = getWorkbenchWindow().getActivePage();
	// return activePage;
	// }
	//
	// public ISelectionProvider getSelectionProvider() {
	// return getGraphicalViewer();
	// }
	//
	// public Shell getShell() {
	// return getControl().getShell();
	// }
	//
	// public IWorkbenchWindow getWorkbenchWindow() {
	// IWorkbench workbench = MetaPlugin.getDefault().getWorkbench();
	// IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	// return window;
	// }
	//
	// public void setSelectionProvider(ISelectionProvider provider) {
	// }
	//
	// public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
	// return null;
	// }
	//
	// public Object getService(@SuppressWarnings("rawtypes") Class api) {
	// return null;
	// }
	//
	// public boolean hasService(@SuppressWarnings("rawtypes") Class api) {
	// return false;
	// }
	//
	// public String getId() {
	// return "PrototpyeComposite.fake.site";
	// }
	//
	// public String getPluginId() {
	// return MetaPlugin.PLUGIN_ID;
	// }
	//
	// public String getRegisteredName() {
	// return "PrototpyeComposite.fake.site.name";
	// }
	//
	// public IWorkbenchPart getPart() {
	// return ConsultantComposite.this;
	// }
	//
	// public void registerContextMenu(String menuId, MenuManager menuManager,
	// ISelectionProvider selectionProvider) {
	//
	// }
	//
	// public void registerContextMenu(MenuManager menuManager,
	// ISelectionProvider selectionProvider) {
	//
	// }
	//
	// @SuppressWarnings("deprecation")
	// public IKeyBindingService getKeyBindingService() {
	// return null;
	// }
	// };

	public void selectionChanged(SelectionChangedEvent event) {

	}

}
