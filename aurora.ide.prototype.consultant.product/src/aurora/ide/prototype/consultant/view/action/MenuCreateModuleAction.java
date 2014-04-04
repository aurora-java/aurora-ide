package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.IMenuManager;

import aurora.ide.prototype.consultant.view.NavigationView;

public class MenuCreateModuleAction extends MenuAction {
	private CreateModuleAction createFunctionAction;

	public MenuCreateModuleAction(NavigationView viewer) {
		this.createFunctionAction = new CreateModuleAction(viewer);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (createFunctionAction.isEnabled()) {
			menu.add(createFunctionAction);
		}
	}

}
