package aurora.ide.meta.gef.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.LabelRetargetAction;

import aurora.ide.meta.gef.editors.actions.CopyAsImageAction;
import aurora.ide.meta.gef.editors.actions.SaveAsImageAction;
import aurora.ide.meta.gef.message.Messages;

public class ConsultantVScreenEditorContributor extends ActionBarContributor {


	/**
	 * Create actions managed by this contributor.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		addRetargetAction(new DeleteRetargetAction());
		UndoRetargetAction action = new UndoRetargetAction();
		addRetargetAction(action);
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(createLabelRetargetAction(CopyAsImageAction.ID,
				Messages.ConsultantVScreenEditorContributor_0, "/icons/full/obj16/image_obj.gif")); //$NON-NLS-2$
		addRetargetAction(createLabelRetargetAction(SaveAsImageAction.ID,
				Messages.ConsultantVScreenEditorContributor_2, "/icons/save_as_image.gif")); //$NON-NLS-2$
		addRetargetAction(createLabelRetargetAction(FlayoutBMGEFEditor.FLAYOUT_BMGEF_EDITOR_MAX_EDITOR_COMPOSITE_ACTION,
				"", "/icons/max_editor.png")); //$NON-NLS-2$
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
		IAction action = getAction(CopyAsImageAction.ID);
		toolBarManager.add(action);
		toolBarManager.add(getAction(SaveAsImageAction.ID));
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(new Separator());
		toolBarManager.add(getAction(FlayoutBMGEFEditor.FLAYOUT_BMGEF_EDITOR_MAX_EDITOR_COMPOSITE_ACTION));
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
