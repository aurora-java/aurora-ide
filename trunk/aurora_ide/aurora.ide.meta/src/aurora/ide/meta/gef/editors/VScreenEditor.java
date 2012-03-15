package aurora.ide.meta.gef.editors;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.editor.InputFileListener;
import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;
import aurora.ide.meta.gef.editors.dnd.BMTransferDropTargetListener;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.models.io.ModelIOManager;
import aurora.ide.meta.gef.editors.parts.AuroraPartFactory;
import aurora.ide.meta.gef.editors.property.MetaPropertyViewer;

public class VScreenEditor extends FlayoutBMGEFEditor {

	public static final String CONTEXT_MENU_KEY = "aurora.ide.meta.gef.editor.contextmenu";
	ViewDiagram diagram;
	private PaletteRoot root;
	private KeyHandler sharedKeyHandler;
	private MetaPropertyViewer propertyViewer;
	private BMViewer bmViewer;
	private EditorMode editorMode;

	public VScreenEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new InputFileListener(this));
		editorMode = new EditorMode(this);
	}

	public void setDiagram(ViewDiagram diagram) {
		this.diagram = diagram;
		GraphicalViewer gv = getGraphicalViewer();
		gv.setContents(diagram);
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
	 * Creates an appropriate output stream and writes the activity diagram out
	 * to this stream.
	 * 
	 * @param os
	 *            the base output stream
	 * @throws IOException
	 */
	protected void createOutputStream(OutputStream os) throws IOException {
		ModelIOManager mim = ModelIOManager.getNewInstance();
		CompositeMap rootMap = mim.toCompositeMap(diagram);
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ rootMap.toXML();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,
				"UTF-8"));
		bw.write(xml);
		bw.close();
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(new AuroraPartFactory(editorMode));
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
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(diagram);
		getGraphicalViewer().addDropTargetListener(
				new BMTransferDropTargetListener(getGraphicalViewer()));
	}

	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			createOutputStream(out);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true,
					false, monitor);
			out.close();
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		SaveAsDialog dialog = new SaveAsDialog(getSite().getWorkbenchWindow()
				.getShell());
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();
		IPath path = dialog.getResult();

		if (path == null)
			return;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IFile file = workspace.getRoot().getFile(path);

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			public void execute(final IProgressMonitor monitor)
					throws CoreException {
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					createOutputStream(out);
					file.create(new ByteArrayInputStream(out.toByteArray()),
							true, monitor);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		try {
			new ProgressMonitorDialog(getSite().getWorkbenchWindow().getShell())
					.run(false, true, op);
			setInput(new FileEditorInput((IFile) file));
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			root = VScreenEditorPaletteFactory.createPalette();
		return root;
	}

	public void gotoMarker(IMarker marker) {
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
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (!(input instanceof IFileEditorInput)) {
			diagram = new ViewDiagram();
			return;
		}

		IFile file = ((IFileEditorInput) input).getFile();
		this.setPartName(file.getName());
		InputStream is = null;
		try {
			is = file.getContents(false);
			CompositeLoader parser = new CompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			ModelIOManager mim = ModelIOManager.getNewInstance();
			diagram = mim.fromCompositeMap(rootMap);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (diagram == null) {
				diagram = new ViewDiagram();
			}
		}
	}

	protected void createPropertyViewer(Composite c) {
		propertyViewer = new MetaPropertyViewer(c, this);
		propertyViewer.setCommandStack(getEditDomain().getCommandStack());
	}

	protected void createBMViewer(Composite c) {
		bmViewer = new BMViewer(c, this);
	}

	public ViewDiagram getDiagram() {
		return diagram;
	}

}
