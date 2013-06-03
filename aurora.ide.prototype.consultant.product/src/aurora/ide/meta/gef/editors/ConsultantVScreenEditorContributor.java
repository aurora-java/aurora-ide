package aurora.ide.meta.gef.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.LabelRetargetAction;

import aurora.ide.meta.gef.editors.actions.CopyAsImageAction;
import aurora.ide.meta.gef.editors.actions.SaveAsImageAction;

public class ConsultantVScreenEditorContributor extends ActionBarContributor {


	/**
	 * Create actions managed by this contributor.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(createLabelRetargetAction(CopyAsImageAction.ID,
				"Copy As a Image", "/icons/full/obj16/image_obj.gif"));
		addRetargetAction(createLabelRetargetAction(SaveAsImageAction.ID,
				"Save As a Image", "/icons/save_as_image.gif"));
	}

	private LabelRetargetAction createLabelRetargetAction(String id,
			String text, String icon) {
		LabelRetargetAction action = new LabelRetargetAction(id, text);
		action.setToolTipText(text);
		action.setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor(icon));
		return action;
	}

	/**
	 * Add actions to the given toolbar.
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(CopyAsImageAction.ID));
		toolBarManager.add(getAction(SaveAsImageAction.ID));
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		// currently none
	}

}
