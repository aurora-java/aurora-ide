package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.IMenuManager;

import aurora.ide.prototype.consultant.view.NavigationView;

public class MenuCreateUIPAction extends MenuAction {

	private CreateUIPAction createFunctionAction;

	public MenuCreateUIPAction(NavigationView viewer) {
		this.createFunctionAction = new CreateUIPAction(viewer);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (createFunctionAction.isEnabled()) {
			menu.add(createFunctionAction);
		}
	}


}
