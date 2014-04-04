package aurora.ide.prototype.consultant.view.action;

import java.io.File;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.UIPViewerSortSeq;

public class UpItemAction extends MenuAction {

	private NavigationView viewer;

	public UpItemAction(NavigationView viewer) {
		this.setText("向上");
		setToolTipText(Messages.CreateUIPAction_1);
		this.viewer = viewer;
		// commonViewer = viewer.getViewer();
		// commonViewer.addSelectionChangedListener(this);
		// selectionChanged();

	}

	public void run() {

		Node next = viewer.getSelectionNode();
		Widget testFindItem = viewer.getViewer().testFindItem(next);
		if (testFindItem instanceof TreeItem) {
			TreeItem parentItem = ((TreeItem) testFindItem).getParentItem();
			int indexOf = parentItem.indexOf((TreeItem) testFindItem);
			TreeItem pre = parentItem.getItem(indexOf - 1);
			UIPViewerSortSeq.getUIPViewerSortSeq((Node) parentItem.getData())
					.changeSort((Node) pre.getData(),
							(Node) testFindItem.getData());
			viewer.getViewer().refresh((Node) parentItem.getData());
		}
		// System.out.println(testFindItem);
		// viewer.getViewer().get
		// if (next != null) {
		// Node parent = next.getParent();
		// if (parent != null && parent.isRoot() == false) {
		// List<Node> children = parent.getChildren();
		// int indexOf = children.indexOf(next);
		// Node pre = children.get(indexOf - 1);
		// UIPViewerSortSeq.getUIPViewerSortSeq(parent).changeSort(pre,
		// next);
		// viewer.getViewer().refresh(parent);
		// }
		// }

	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		if (this.isActionEnabled()) {
			menu.add(this);
		}
	}

	private boolean isActionEnabled() {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getViewer().getSelection();
		if (selection.size() != 1) {
			return false;
		}

		Node selectionNode = viewer.getSelectionNode();

		if (selectionNode != null) {
			Widget testFindItem = viewer.getViewer()
					.testFindItem(selectionNode);
			if (testFindItem instanceof TreeItem) {
				TreeItem parentItem = ((TreeItem) testFindItem).getParentItem();
				int indexOf = parentItem.indexOf((TreeItem) testFindItem);
				if (indexOf != 0 && indexOf != -1) {
					TreeItem pre = parentItem.getItem(indexOf - 1);
					return isSameType((Node) pre.getData(), selectionNode);
				}
			}
		}
		return false;
	}

	private boolean isSameType(Node data, Node selectionNode) {
		File a = data.getFile();
		File b = selectionNode.getFile();
		boolean aIsDir = a.isDirectory();
		boolean bIsDir = b.isDirectory();
		if (aIsDir && bIsDir)
			return true;
		if (!bIsDir && !aIsDir)
			return true;

		return false;
	}


}
