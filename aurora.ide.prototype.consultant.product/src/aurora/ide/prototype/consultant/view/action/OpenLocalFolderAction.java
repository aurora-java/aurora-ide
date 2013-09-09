package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.prototype.consultant.view.NavViewSetting;
import aurora.ide.prototype.consultant.view.NavigationView;

public class OpenLocalFolderAction extends Action {

	
	private NavigationView viewer;

	public OpenLocalFolderAction(NavigationView viewer) {
		this.viewer = viewer;
	}
	
	
	public void run() {
		String queryFile = queryFile();
		if(queryFile == null)
			return;
		NavViewSetting vs = new NavViewSetting();
		vs.addFolder(queryFile);
		new RefreshLocalFileSystemAction(viewer).run();
	}
	private String queryFile() {

		DirectoryDialog directoryDialog = new DirectoryDialog(
				new Shell());
		String value = directoryDialog.open();
		return value;
	}
}
