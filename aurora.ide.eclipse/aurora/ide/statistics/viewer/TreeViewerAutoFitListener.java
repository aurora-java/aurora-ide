package aurora.ide.statistics.viewer;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;

public class TreeViewerAutoFitListener implements ITreeViewerListener {

	public void treeExpanded(TreeExpansionEvent event) {
		packColumns((TreeViewer) event.getSource());
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		packColumns((TreeViewer) event.getSource());
	}

	public static void packColumns(final TreeViewer treeViewer) {
		treeViewer.getControl().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				TreeColumn[] treeColumns = treeViewer.getTree().getColumns();
				for (TreeColumn treeColumn : treeColumns) {
					if (treeColumn.getWidth() == 0){
						continue;
					}
					treeColumn.pack();
				}
			}
		});
	}
}
