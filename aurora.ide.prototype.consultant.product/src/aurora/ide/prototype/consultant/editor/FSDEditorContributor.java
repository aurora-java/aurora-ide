package aurora.ide.prototype.consultant.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

public class FSDEditorContributor extends EditorActionBarContributor {


	/**
	 * Create actions managed by this contributor.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
//		addRetargetAction(new DeleteRetargetAction());
//		UndoRetargetAction action = new UndoRetargetAction();
//		addRetargetAction(action);
//		addRetargetAction(new RedoRetargetAction());
	}


	/**
	 * Add actions to the given toolbar.
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {
//		toolBarManager.add(new Separator());
//		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
//		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
//		toolBarManager.add(new Separator());
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
