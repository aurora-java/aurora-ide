package aurora.ide.meta.gef.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;
import aurora.ide.meta.gef.editors.dnd.BMTransferDropTargetListener;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.parts.AuroraPartFactory;
import aurora.ide.meta.gef.editors.parts.DatasetPartFactory;
import aurora.ide.meta.gef.editors.property.MetaPropertyViewer;
import aurora.ide.meta.gef.editors.source.gen.ScreenGenerator;

public class VScreenEditor extends FlayoutBMGEFEditor {

	ViewDiagram diagram;
	private PaletteRoot root;
	private KeyHandler sharedKeyHandler;
	private MetaPropertyViewer propertyViewer;

	public VScreenEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
	}

	/**
	 * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
//		this.getSite().registerContextMenu(menuId, menuManager, selectionProvider)
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
		ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(diagram);
		out.close();
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableRootEditPart());
		getGraphicalViewer().setEditPartFactory(new AuroraPartFactory());
		getGraphicalViewer().setKeyHandler(
				new GraphicalViewerKeyHandler(getGraphicalViewer())
						.setParent(getCommonKeyHandler()));

		ContextMenuProvider provider = new ViewContextMenuProvider(
				getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(
				"org.eclipse.gef.examples.flow.editor.contextmenu", //$NON-NLS-1$
				provider, getGraphicalViewer());
		getGraphicalViewer().addSelectionChangedListener(propertyViewer);

	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(diagram);
		// getGraphicalViewer().addDropTargetListener(
		// new TemplateTransferDropTargetListener(getGraphicalViewer()));

		getGraphicalViewer().addDropTargetListener(
				new BMTransferDropTargetListener(getGraphicalViewer()));
		// this.getGraphicalViewer()
	}

	protected void initDatasetView() {
		DatasetView datasetView = getDatasetView();
		// datasetView.getControl().setBackground(ColorConstants.white);
		getEditDomain().addViewer(datasetView);
		getSelectionSynchronizer().addViewer(datasetView);
		datasetView.setRootEditPart(new ScalableRootEditPart());
		datasetView.setEditPartFactory(new DatasetPartFactory());
		this.getDatasetView().setContents(diagram);

	}

	// /**
	// * @see
	// org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#initializePaletteViewer()
	// */
	// protected void initializePaletteViewer() {
	// super.initializePaletteViewer();
	// getPaletteViewer().addDragSourceListener(
	// new TemplateTransferDragSourceListener(getPaletteViewer()));
	// }

	@Override
	public GraphicalViewer getGraphicalViewer() {
		// TODO Auto-generated method stub
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

		createScreen();
	}

	private void createScreen() {
		ScreenGenerator sg = new ScreenGenerator();
		sg.genFile(this.diagram);

		

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

		IFile file = ((IFileEditorInput) input).getFile();
		try {
			InputStream is = file.getContents(false);
			ObjectInputStream ois = new ObjectInputStream(is);
			diagram = (ViewDiagram) ois.readObject();
			ois.close();
		} catch (Exception e) {
			// This is just an example. All exceptions caught here.
			// e.printStackTrace();
			diagram = new ViewDiagram();
		}
	}

	protected void createPropertyViewer(Composite c) {
		propertyViewer = new MetaPropertyViewer(c, this);
		propertyViewer.setCommandStack(getEditDomain().getCommandStack());
	}

	protected void createBMViewer(Composite c) {
		BMViewer bmViewer = new BMViewer(c, this);

	}

}
