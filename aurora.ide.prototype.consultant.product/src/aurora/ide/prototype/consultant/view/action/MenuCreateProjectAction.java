package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.IMenuManager;

import aurora.ide.prototype.consultant.view.NavigationView;

public class MenuCreateProjectAction extends MenuAction {
	private CreateProjectAction createFunctionAction;

	public MenuCreateProjectAction(NavigationView viewer) {
		this.createFunctionAction = new CreateProjectAction(viewer);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (createFunctionAction.isEnabled()) {
			menu.add(createFunctionAction);
		}
	}

}
