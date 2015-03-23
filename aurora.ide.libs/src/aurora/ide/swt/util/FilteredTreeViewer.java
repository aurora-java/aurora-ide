package aurora.ide.swt.util;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class FilteredTreeViewer {



	private TreeViewer viewer;

	public void createControl(Composite parent) {
		parent.setLayout(new GridLayout());
		FilteredTree ff = new FilteredTree(parent, SWT.SINGLE,
				new PatternFilter(), true);
		setViewer(ff.getViewer());

		// refreshInput();
		getViewer().getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

}
