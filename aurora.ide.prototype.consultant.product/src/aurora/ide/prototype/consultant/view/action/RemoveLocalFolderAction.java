package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import aurora.ide.prototype.consultant.view.NavViewSetting;
import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;

public class RemoveLocalFolderAction extends Action implements
		ISelectionChangedListener {

	private NavigationView viewer;

	public RemoveLocalFolderAction(NavigationView viewer) {
		this.viewer = viewer;
		viewer.getViewSite().getSelectionProvider()
				.addSelectionChangedListener(this);
		this.setEnabled(false);
	}

	public void run() {
		String queryFile = queryFile();
		if (queryFile == null)
			return;
		NavViewSetting vs = new NavViewSetting();
		vs.removeFolder(queryFile);
		new RefreshLocalFileSystemAction(viewer).run();
	}

	private String queryFile() {
		Node selectionNode = viewer.getSelectionNode();
		if (selectionNode.getParent().isRoot()) {
			return selectionNode.getPath().toOSString();
		}
		return null;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		Node selectionNode = viewer.getSelectionNode();
		if (selection.size() == 1 && selectionNode.getParent().isRoot()) {
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
	}

}
