package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.IMenuManager;

import aurora.ide.prototype.consultant.view.NavigationView;

public class MenuCreateFunctionAction extends MenuAction {
	private CreateFunctionAction createFunctionAction;

	public MenuCreateFunctionAction(NavigationView viewer) {
		this.createFunctionAction = new CreateFunctionAction(viewer);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (createFunctionAction.isEnabled()) {
			menu.add(createFunctionAction);
		}
	}

}
