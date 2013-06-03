package aurora.ide.meta.gef.editors;

import java.util.EventObject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import aurora.ide.meta.gef.editors.actions.CopyAsImageAction;
import aurora.ide.meta.gef.editors.actions.CopyComponentsAction;
import aurora.ide.meta.gef.editors.actions.PasteComponentsAction;
import aurora.ide.meta.gef.editors.actions.SaveAsImageAction;

public abstract class FlayoutBMGEFEditor extends
		GraphicalEditorWithFlyoutPalette {
	private DatasetView datasetView;
	private KeyHandler sharedKeyHandler;

	public void createPartControl(Composite parent) {

		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);

		SashForm c = new SashForm(sashForm, SWT.VERTICAL | SWT.BORDER);
		createBMViewer(c);
		createPropertyViewer(c);

		Composite cpt = new Composite(sashForm, SWT.NONE);
		cpt.setLayout(new GridLayout());
		Composite bottom = new Composite(cpt, SWT.NONE);
		bottom.setLayoutData(new GridData(GridData.FILL_BOTH));
		bottom.setLayout(new FillLayout());

		super.createPartControl(bottom);
		sashForm.setWeights(new int[] { 1, 4 });
	}

	protected void initDatasetView() {

	}

	@Override
	protected void createGraphicalViewer(Composite parent) {
		super.createGraphicalViewer(parent);
	}

	public DatasetView getDatasetView() {
		return datasetView;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createActions()
	 */
	protected void createActions() {
		super.createActions();
		final ActionRegistry registry = getActionRegistry();
		IAction action;

		action = new DirectEditAction((IWorkbenchPart) this) {
			protected Request getDirectEditRequest() {
				org.eclipse.swt.graphics.Point p = getGraphicalControl()
						.toControl(Display.getCurrent().getCursorLocation());
				DirectEditRequest directEditRequest = new DirectEditRequest();
				directEditRequest.setLocation(new Point(p.x, p.y));
				return directEditRequest;
			}
		};
		CopyComponentsAction copy = new CopyComponentsAction(this);
		registry.registerAction(copy);
		getSelectionActions().add(copy.getId());
		
		PasteComponentsAction paste = new PasteComponentsAction(this);
		registry.registerAction(paste);
		getSelectionActions().add(paste.getId());
		
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		
		CopyAsImageAction copyIMG = new CopyAsImageAction(this);
		registry.registerAction(copyIMG);
		getSelectionActions().add(copyIMG.getId());
		
		SaveAsImageAction saveIMG = new SaveAsImageAction(this);
		registry.registerAction(saveIMG);
		getSelectionActions().add(saveIMG.getId());
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
			String os = Platform.getOS();
			if (Platform.OS_MACOSX.equals(os)) {
				sharedKeyHandler.put(
						KeyStroke.getPressed('c', 99, SWT.COMMAND),
						getActionRegistry().getAction(
								ActionFactory.COPY.getId()));
				sharedKeyHandler.put(
						KeyStroke.getPressed('v', 118, SWT.COMMAND),
						getActionRegistry().getAction(
								ActionFactory.PASTE.getId()));
				sharedKeyHandler.put(
						KeyStroke.getPressed('z', 122, SWT.COMMAND),
						getActionRegistry().getAction(
								ActionFactory.UNDO.getId()));
				sharedKeyHandler.put(
						KeyStroke.getPressed('y', 121, SWT.COMMAND),
						getActionRegistry().getAction(
								ActionFactory.REDO.getId()));
			} else {
				// Platform.OS_WIN32,Others
				sharedKeyHandler.put(
						KeyStroke.getPressed((char) 3, 99, SWT.CTRL),
						getActionRegistry().getAction(
								ActionFactory.COPY.getId()));
				sharedKeyHandler.put(
						KeyStroke.getPressed((char) 22, 118, SWT.CTRL),
						getActionRegistry().getAction(
								ActionFactory.PASTE.getId()));
				sharedKeyHandler.put(
						KeyStroke.getPressed((char) 26, 122, SWT.CTRL),
						getActionRegistry().getAction(
								ActionFactory.UNDO.getId()));
				sharedKeyHandler.put(
						KeyStroke.getPressed((char) 25, 121, SWT.CTRL),
						getActionRegistry().getAction(
								ActionFactory.REDO.getId()));
			}
		}
		return sharedKeyHandler;
	}

	/**
	 * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	public void markDirty() {
		Command cmd = new Command() {

		};
		this.getEditDomain().getCommandStack().execute(cmd);
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	protected abstract void createPropertyViewer(Composite c);

	protected abstract void createBMViewer(Composite c);
}
