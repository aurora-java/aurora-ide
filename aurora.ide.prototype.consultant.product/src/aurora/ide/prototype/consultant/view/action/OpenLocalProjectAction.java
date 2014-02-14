package aurora.ide.prototype.consultant.view.action;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.prototype.consultant.view.NavViewSetting;
import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;

public class OpenLocalProjectAction extends Action {

	private NavigationView viewer;

	public OpenLocalProjectAction(NavigationView viewer) {
		this.viewer = viewer;
	}

	public void run() {
		String queryFile = queryFile();
		if (queryFile == null)
			return;
		boolean project = ResourceUtil.isProject(new File(queryFile));
		if (project == false) {
			MessageDialog.openInformation(viewer.getSite().getShell(), "Info", //$NON-NLS-1$
					Messages.OpenLocalProjectAction_1);

		} else {
			NavViewSetting vs = new NavViewSetting();
			String[] folders = vs.getFolders();
			for (String string : folders) {
				if (new Path(string).equals(new Path(queryFile))) {
					MessageDialog.openInformation(viewer.getSite().getShell(),
							"Info", Messages.OpenLocalProjectAction_3); //$NON-NLS-1$
					return;

				}
			}
			vs.addFolder(queryFile);
			Node newNode = new Node(new Path(queryFile));
			// viewer.addNewNode(viewer.getViewer().getInput(), newNode);
			((Node) (viewer.getViewer().getInput())).addChild(newNode);
			viewer.getViewer().add(viewer.getViewer().getInput(), newNode);
			viewer.selectReveal(newNode);
		}
	}

	private String queryFile() {

		DirectoryDialog directoryDialog = new DirectoryDialog(new Shell());
		String value = directoryDialog.open();
		return value;
	}
}
