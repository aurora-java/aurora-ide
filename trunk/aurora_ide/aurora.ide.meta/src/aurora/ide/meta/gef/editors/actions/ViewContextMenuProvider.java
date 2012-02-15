
package aurora.ide.meta.gef.editors.actions;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Provides a context menu for the flow editor.
 * 
 * @author 
 */
public class ViewContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	/**
	 * Creates a new FlowContextMenuProvider assoicated with the given viewer
	 * and action registry.
	 * 
	 * @param viewer
	 *            the viewer
	 * @param registry
	 *            the action registry
	 */
	public ViewContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}

	/**
	 * @see ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);

		IAction action;
		action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.REDO.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	/**
	 * Sets the action registry
	 * 
	 * @param registry
	 *            the action registry
	 */
	public void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
