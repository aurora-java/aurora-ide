package aurora.ide.meta.gef.editors;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;

import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;
import aurora.ide.prototype.consultant.product.action.DemonstrateSettingAction;
import aurora.ide.prototype.consultant.product.action.FSDPropertyEditAction;

public class ConsultantContextMenuProvider extends ViewContextMenuProvider {

	public ConsultantContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer, registry);
	}

	public void buildContextMenu(IMenuManager menu) {
		super.buildContextMenu(menu);
		IAction action = getActionRegistry().getAction(FSDPropertyEditAction.ID);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		action = getActionRegistry().getAction(DemonstrateSettingAction.ID);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	}
}
