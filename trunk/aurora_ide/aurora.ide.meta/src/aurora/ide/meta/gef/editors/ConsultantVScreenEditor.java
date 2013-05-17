package aurora.ide.meta.gef.editors;

import java.io.File;
import java.io.IOException;
import java.util.EventObject;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;
import aurora.ide.meta.gef.editors.consultant.property.ConsultantPropertyManager;
import aurora.ide.meta.gef.editors.dnd.BMTransferDropTargetListener;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.property.MetaPropertyViewer;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class ConsultantVScreenEditor extends FlayoutBMGEFEditor {
	public static final String CONTEXT_MENU_KEY = "aurora.ide.meta.gef.editor.contextmenu";
	ScreenBody diagram;
	private PaletteRoot root;
	private KeyHandler sharedKeyHandler;
	private MetaPropertyViewer propertyViewer;
	private BMViewer bmViewer;
	private EditorMode editorMode;
	private IEditorInput input;

	public ConsultantVScreenEditor() {
		super();
		editorMode = new EditorMode() {
			public String getMode() {
				return None;
			}

			public boolean isForDisplay() {
				return false;
			}

			public boolean isForCreate() {
				return true;
			}

			public boolean isForUpdate() {
				return true;
			}

			public boolean isForSearch() {
				return false;
			}
		};
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		IEditorInput editorInput = this.getEditorInput();
		if (editorInput instanceof PathEditorInput) {
			PathEditorInput pei = (PathEditorInput) editorInput;
			IPath path = pei.getPath();
			if (this.isUntitled()) {
				FileDialog sd = new FileDialog(this.getSite().getShell(),
						SWT.SAVE);
				sd.setFileName(path.toString());
				sd.setFilterExtensions(new String[] { "*.uip" });
				sd.setOverwrite(true);
				String open = sd.open();
				if (open == null || open.length() < 1) {
					return;
				}
				path = setNewPath(pei, open);
			}
			File file = path.toFile();
			try {
				file.createNewFile();
				if (file.exists()) {
					if (file.canWrite()) {
						Object2CompositeMap o2c = new Object2CompositeMap();
						CompositeMap map = o2c.createCompositeMap(diagram);
						XMLOutputter.saveToFile(file, map);
						getCommandStack().markSaveLocation();
					} else {
						// // XXX prompt to SaveAs
						//						throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "file is read-only", null)); //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else {
					//					throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "error creating file", null)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} catch (IOException e) {
				//				throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "error when saving file", e)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	public IPath setNewPath(PathEditorInput pei, String open) {
		IPath path;
		pei.setPath(path = new Path(open));
		String lastSegment = path.lastSegment();
		this.setPartName(lastSegment);
		return path;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		IEditorInput editorInput = this.getEditorInput();
		if (editorInput instanceof PathEditorInput) {
			PathEditorInput pei = (PathEditorInput) editorInput;
			IPath path = pei.getPath();
			FileDialog sd = new FileDialog(this.getSite().getShell(), SWT.SAVE);
			sd.setFileName(".uip");
			sd.setFilterExtensions(new String[] { "*.uip" });
			sd.setOverwrite(true);
			String open = sd.open();
			if (open == null || open.length() < 1) {
				return;
			}
			path = setNewPath(pei, open);
			doSave(null);
		}
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	public void setInput(IEditorInput input) {
		this.input = input;
		super.setInput(input);
		if (input instanceof PathEditorInput) {
			if (isUntitled()) {
				diagram = new ScreenBody();
			} else {
				File file = ((PathEditorInput) input).getPath().toFile();
				CompositeMap loadFile = CompositeMapUtil.loadFile(file);
				if (loadFile != null) {
					CompositeMap2Object c2o = new CompositeMap2Object();
					diagram = c2o.createScreenBody(loadFile);
				} else {
					diagram = new ScreenBody();
				}
			}
			String lastSegment = ((PathEditorInput) input).getPath()
					.lastSegment();
			this.setPartName(lastSegment);
			DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
			setEditDomain(defaultEditDomain);
		}
	}

	protected void createBMViewer(Composite c) {
		// bmViewer = new BMViewer(c, this);
	}

	public boolean isUntitled() {
		if (input instanceof PathEditorInput) {
			return ((PathEditorInput) input).isUntitled();
		}
		return false;
	}

	public void setDiagram(ScreenBody diagram) {
		this.diagram = diagram;
		GraphicalViewer gv = getGraphicalViewer();
		gv.setContents(diagram);
		if (bmViewer != null)
			bmViewer.refreshInput();
		markDirty();
	}

	public void markDirty() {
		Command cmd = new Command() {

		};
		this.getEditDomain().getCommandStack().execute(cmd);
		firePropertyChange(IEditorPart.PROP_DIRTY);
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
		ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new DirectEditAction((IWorkbenchPart) this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}

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
		getGraphicalViewer().addDropTargetListener(
				new BMTransferDropTargetListener(getGraphicalViewer()));
	}

	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */

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
			root = VScreenEditorExtPaletteFactory
					.createPalette(this.editorMode);
		return root;
	}

	public void gotoMarker(IMarker marker) {
	}

	@Override
	public IEditorInput getEditorInput() {
		return super.getEditorInput();
	}

	@Override
	public IEditorSite getEditorSite() {
		return super.getEditorSite();
	}

	protected void createPropertyViewer(Composite c) {
		DefaultEditDomain editDomain = getEditDomain();
		propertyViewer = new MetaPropertyViewer(c, this, new ConsultantPropertyManager(
				editDomain.getCommandStack()));
	}

	public ScreenBody getDiagram() {
		return diagram;
	}

}
