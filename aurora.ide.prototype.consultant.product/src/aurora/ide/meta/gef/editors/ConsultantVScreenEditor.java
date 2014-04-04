package aurora.ide.meta.gef.editors;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.actions.ActionFactory;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.meta.gef.editors.actions.CopyAsImageAction;
import aurora.ide.meta.gef.editors.actions.SaveAsImageAction;
import aurora.ide.meta.gef.editors.consultant.property.ConsultantPropertyManager;
import aurora.ide.meta.gef.editors.dnd.BMTransferDropTargetListener;
import aurora.ide.meta.gef.editors.parts.ExtAuroraPartFactory;
import aurora.ide.meta.gef.editors.property.MetaPropertyViewer;
import aurora.ide.prototype.consultant.product.action.DemonstrateAction;
import aurora.ide.prototype.consultant.product.action.DemonstrateSettingAction;
import aurora.ide.prototype.consultant.product.action.FSDPropertyEditAction;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.CompositeMap2Object;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class ConsultantVScreenEditor extends FlayoutBMGEFEditor {
	public static final String CONSULTANTVSCREENEDITOR_SAVE_NEW_FILE = "CONSULTANTVSCREENEDITOR_SAVE_NEW_FILE"; //$NON-NLS-1$
	public static final String CONTEXT_MENU_KEY = "aurora.ide.meta.gef.editor.contextmenu"; //$NON-NLS-1$
	ScreenBody diagram;
	private PaletteRoot root;

	private MetaPropertyViewer propertyViewer;
	private BMViewer bmViewer;
	private EditorMode editorMode;
	private IEditorInput input;

	@Override
	public void setFocus() {
		super.setFocus();
	}

	@Override
	protected void updateActions(List actionIds) {
		super.updateActions(actionIds);
	}

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
		this.getPalettePreferences().setPaletteState(
				FlyoutPaletteComposite.STATE_PINNED_OPEN);
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		IEditorInput editorInput = this.getEditorInput();
		if (editorInput instanceof PathEditorInput) {
			PathEditorInput pei = (PathEditorInput) editorInput;
			IPath path = pei.getPath();
			boolean isSaveNew = false;
			if (this.isUntitled()) {
				FileDialog sd = new FileDialog(this.getSite().getShell(),
						SWT.SAVE);
				sd.setFileName(path.toString());
				sd.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
				sd.setOverwrite(true);
				String open = sd.open();
				if (open == null || open.length() < 1) {
					return;
				}
				isSaveNew = true;
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
					}
				} else {
				}
			} catch (IOException e) {
				if (file.exists() == false) {
					MessageDialog
							.openInformation(
									this.getSite().getShell(),
									"Info", aurora.ide.meta.gef.editors.Messages.ConsultantVScreenEditor_2); //$NON-NLS-1$
				}
			}
			if (isSaveNew) {
				isSaveNew = false;
				firePartPropertyChanged(CONSULTANTVSCREENEDITOR_SAVE_NEW_FILE,
						"", path.toOSString()); //$NON-NLS-1$
			}
		}
	}

	public IPath setNewPath(PathEditorInput pei, String open) {
		IPath path;
		pei.setPath(path = new Path(open));
		String lastSegment = path.removeFileExtension().lastSegment();
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
			sd.setFileName(".uip"); //$NON-NLS-1$
			sd.setFilterExtensions(new String[] { "*.uip" }); //$NON-NLS-1$
			sd.setOverwrite(true);
			String open = sd.open();
			if (open == null || open.length() < 1) {
				return;
			}
			path = setNewPath(pei, open);
			doSave(null);
			firePartPropertyChanged(CONSULTANTVSCREENEDITOR_SAVE_NEW_FILE,
					"", path.toOSString()); //$NON-NLS-1$
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
					.removeFileExtension().lastSegment();
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

	@Override
	protected void createActions() {
		super.createActions();

		IAction action = this.getActionRegistry().getAction(
				ActionFactory.COPY.getId());
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_4);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_5);
		action = this.getActionRegistry()
				.getAction(ActionFactory.PASTE.getId());
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_6);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_7);
		action = this.getActionRegistry().getAction(ActionFactory.REDO.getId());
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_8);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_9);
		action = this.getActionRegistry().getAction(ActionFactory.UNDO.getId());
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_10);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_11);
		action = this.getActionRegistry().getAction(
				ActionFactory.DELETE.getId());
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_12);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_13);
		action = this.getActionRegistry().getAction(CopyAsImageAction.ID);
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_14);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_14);
		action = this.getActionRegistry().getAction(SaveAsImageAction.ID);
		action.setText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_17);
		action.setToolTipText(aurora.ide.meta.gef.i18n.Messages.ConsultantVScreenEditor_17);

		FSDPropertyEditAction copyIMG = new FSDPropertyEditAction(this);
		getActionRegistry().registerAction(copyIMG);
		getSelectionActions().add(copyIMG.getId());

		DemonstrateSettingAction dsa = new DemonstrateSettingAction(this);
		getActionRegistry().registerAction(dsa);
		getSelectionActions().add(dsa.getId());

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

		ContextMenuProvider provider = new ConsultantContextMenuProvider(
				getGraphicalViewer(), getActionRegistry());
		getGraphicalViewer().setContextMenu(provider);
		getSite().registerContextMenu(CONTEXT_MENU_KEY, //$NON-NLS-1$
				provider, getGraphicalViewer());
		getGraphicalViewer().addSelectionChangedListener(propertyViewer);

		if (DemonstrateAction.getIsDemon()) {
			this.maxEditorComposite();
		}
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	public void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(diagram);
		getGraphicalViewer().addDropTargetListener(
				new BMTransferDropTargetListener(getGraphicalViewer()));
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

	protected void createPropertyViewer(Composite c) {
		propertyViewer = new MetaPropertyViewer(c, this, getPropertyManager());
	}

	public ConsultantPropertyManager getPropertyManager() {
		return new ConsultantPropertyManager(getEditDomain().getCommandStack());
	}

	public ScreenBody getDiagram() {
		return diagram;
	}

}
