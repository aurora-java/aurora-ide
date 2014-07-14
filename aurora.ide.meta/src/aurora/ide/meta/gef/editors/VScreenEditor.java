package aurora.ide.meta.gef.editors;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.ide.editor.InputFileListener;
import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;
import aurora.ide.meta.gef.editors.dnd.BMTransferDropTargetListener;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.property.MetaPropertyViewer;
import aurora.ide.meta.gef.editors.property.PropertyManager;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class VScreenEditor extends FlayoutBMGEFEditor {

	public static final String CONTEXT_MENU_KEY = "aurora.ide.meta.gef.editor.contextmenu";
	ScreenBody diagram;
	private PaletteRoot root;
	private MetaPropertyViewer propertyViewer;
	private BMViewer bmViewer;
	private EditorMode editorMode;
	private IFile file;

	public VScreenEditor() {
		editorMode = new EditorMode(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new InputFileListener(this));
	}

	public void setDiagram(ScreenBody diagram) {
		this.diagram = diagram;
		GraphicalViewer gv = getGraphicalViewer();
		gv.setContents(diagram);
		if (bmViewer != null)
			bmViewer.refreshInput();
		markDirty();
	}

	public Object getAdapter(Class adapter) {
		if (IFile.class.equals(adapter)) {
			return file;
		}

		return super.getAdapter(adapter);
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
		Object2CompositeMap o2c = new Object2CompositeMap();
		String xml = o2c.createXML(diagram);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os,
				"UTF-8"));
		bw.write(xml);
		bw.close();
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

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getPaletteRoot()
	 */
	protected PaletteRoot getPaletteRoot() {
		if (root == null)
			root = VScreenEditorExtPaletteFactory
					.createPalette(this.editorMode);
		return root;
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
		if (input == null) {
			diagram = new ScreenBody();
			DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
			setEditDomain(defaultEditDomain);
			return;
		}
		super.setInput(input);
		if (!(input instanceof IFileEditorInput)) {
			return;
		}

		file = ((IFileEditorInput) input).getFile();
		this.setPartName(file.getName());
		InputStream is = null;
		try {
			is = file.getContents(false);
			CompositeLoader parser = new CompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			CompositeMap2Object c2o = new CompositeMap2Object();
			diagram = c2o.createScreenBody(rootMap);
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
				diagram = new ScreenBody();
			}
			DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
			setEditDomain(defaultEditDomain);
		}
	}

	protected void createPropertyViewer(Composite c) {
		DefaultEditDomain editDomain = getEditDomain();
		propertyViewer = new MetaPropertyViewer(c, this, new PropertyManager(
				editDomain.getCommandStack()));
	}

	protected void createBMViewer(Composite c) {
		bmViewer = new BMViewer(c, this);
	}

	public ScreenBody getDiagram() {
		return diagram;
	}

}
