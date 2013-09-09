package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;

import aurora.ide.prototype.consultant.view.NavigationView;

public class RefreshLocalFileSystemAction extends Action {

	private NavigationView viewer;

	public RefreshLocalFileSystemAction(NavigationView viewer) {
		this.viewer = viewer;
	}

	public void run() {
		viewer.refreshViewer();
	}
}
