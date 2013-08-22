package aurora.ide.meta.gef.editors.consultant;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;

import aurora.ide.meta.gef.editors.actions.FSDPropertyEditAction;
import aurora.ide.meta.gef.editors.actions.ViewContextMenuProvider;

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
	}
}
