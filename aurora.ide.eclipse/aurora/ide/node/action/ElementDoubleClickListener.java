package aurora.ide.node.action;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.editor.CompositeMapTreeShell;
import aurora.ide.editor.core.IViewer;


import uncertain.composite.CompositeMap;

public class ElementDoubleClickListener implements IDoubleClickListener {
	IViewer parent;

	public ElementDoubleClickListener(IViewer viewer) {
		this.parent = viewer;
	}

	public void doubleClick(DoubleClickEvent event) {
		TreeSelection selection = (TreeSelection) event.getSelection();
		CompositeMap data = (CompositeMap) selection.getFirstElement();
		if (data.getChilds() != null && data.getChilds().size() != 0) {
			CompositeMapTreeShell editor = new CompositeMapTreeShell(parent, data);
			Shell shell = new Shell(SWT.MIN | SWT.MAX | SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);
			shell.setLayout(new FillLayout());
			CompositeMap parent = data.getParent();
			String path = "";
			while (parent != null) {
				if (parent.getRawName() != null)
					path = parent.getRawName() + "/" + path;
				parent = parent.getParent();
			}
			path = path + data.getRawName();
			shell.setText(path);
			editor.createFormContent(shell);
			shell.open();

		}
	}

}
