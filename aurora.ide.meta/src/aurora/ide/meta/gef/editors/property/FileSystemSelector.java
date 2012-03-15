package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class FileSystemSelector implements ISelectionChangedListener {
	private WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();
	private Color borderColor = new Color(null, 171, 173, 179);
	private TreeViewer treeViewer;
	private IResource result;
	private CLabel parLabel;

	public FileSystemSelector(Composite parent, int style) {
		super();
		Composite com = new Composite(parent, style);
		com.setLayout(new GridLayout(1, true));
		Label l = new Label(com, SWT.NONE);
		l.setForeground(new Color(null, 87, 87, 87));
		l.setText("? = any character , * = any string");
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		FilteredTree ff = new FilteredTree(com, SWT.SINGLE,
				new PatternFilter(), true);
		ff.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer = ff.getViewer();
		treeViewer.setContentProvider(new WorkbenchContentProvider());
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.addSelectionChangedListener(this);

		// /
		parLabel = new CLabel(com, SWT.NONE);
		parLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		parLabel.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.setForeground(borderColor);
				Point p = parLabel.getSize();
				e.gc.drawRectangle(0, 0, p.x - 1, p.y - 1);
			}
		});
	}

	public void setInput(IContainer iContainer) {
		treeViewer.setInput(iContainer);
	}

	public IResource getSelection() {
		return result;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection s = event.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			if (!ss.isEmpty()) {
				result = (IResource) ss.getFirstElement();
				IResource r = result.getParent();
				parLabel.setImage(labelProvider.getImage(r));
				parLabel.setText(r.getFullPath().toString());
			}
		}
	}

}
