package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import aurora.ide.helpers.FileDeleter;
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
		selectionChanged();
	}

	public void run() {
		boolean oc = MessageDialog.openConfirm(viewer.getViewer().getControl()
				.getShell(), "Confirm", Messages.RemoveLocalFolderAction_1); //$NON-NLS-1$
		if (oc == false)
			return;
		Node node = viewer.getSelectionNode();
		if (node == null)
			return;
		
		Node[] nodes = viewer.findSamePathNodes(node);
		for (Node n : nodes) {
			if (n.getParent() != null && n.getParent().isRoot()) {
				String queryFile = n.getPath().toOSString();
				NavViewSetting vs = new NavViewSetting();
				vs.removeFolder(queryFile);
			}
			n.getParent().removeChild(n);
			viewer.getViewer().remove(n);
		}
		FileDeleter.deleteDirectory(node.getFile());
		// new RefreshLocalFileSystemAction(viewer).run();
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
		// IStructuredSelection selection = (IStructuredSelection) event
		// .getSelection();
		// Node selectionNode = viewer.getSelectionNode();
		// if (selection.size() == 1 && selectionNode.getParent().isRoot()) {
		// this.setEnabled(true);
		// } else {
		// this.setEnabled(false);
		// }
		selectionChanged();
	}

	private void selectionChanged() {
		Node selectionNode = viewer.getSelectionNode();
		if (selectionNode == null) {
			this.setEnabled(false);
		} else {
			this.setEnabled(true);
		}
	}

}
