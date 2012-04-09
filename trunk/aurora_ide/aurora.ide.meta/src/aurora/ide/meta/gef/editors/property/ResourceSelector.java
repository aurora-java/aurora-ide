package aurora.ide.meta.gef.editors.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ResourceSelector implements ISelectionChangedListener {
	private WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();
	private Color borderColor = new Color(null, 171, 173, 179);
	private TreeViewer treeViewer;
	private IResource result;
	private CLabel parLabel;
	private Dialog dia;
	private IContainer root;
	private String[] extFilters = null;

	/**
	 * create a dialog to select a resource , the dialog will auto open after
	 * {@link #setInput(IContainer)} <br/>
	 * the dialog always block on open<br/>
	 * after select a resource and press OK , {@link #getSelection()} can return
	 * a valid value
	 * 
	 * @param shell
	 */
	public ResourceSelector(Shell shell) {
		dia = new Dialog(shell) {

			protected Control createDialogArea(Composite parent) {
				Composite container = (Composite) super
						.createDialogArea(parent);
				createControl(container);
				return container;
			}

			protected Point getInitialSize() {
				return new Point(400, 450);
			}
		};
	}

	/**
	 * creata a Composite contains this selector<br>
	 * then {@link #setInput(IContainer)} should be called
	 * 
	 * @param parent
	 * @param style
	 */
	public ResourceSelector(Composite parent, int style) {
		super();
		Composite com = new Composite(parent, style);
		createControl(com);
	}

	protected void createControl(Composite com) {
		com.setLayout(new GridLayout(1, true));
		Label l = new Label(com, SWT.NONE);
		l.setForeground(new Color(null, 87, 87, 87));
		l.setText("? = any character , * = any string");
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		FilteredTree ff = new FilteredTree(com, SWT.SINGLE,
				new PatternFilter(), true);
		ff.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer = ff.getViewer();
		treeViewer.setContentProvider(new ResourceContentProvider());
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.addSelectionChangedListener(this);
		if (root != null)
			treeViewer.setInput(root);

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
		ff.getViewer().getTree().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem ti = getTreeViewer().getTree().getItem(
						new Point(e.x, e.y));
				if (ti != null) {
					result = (IResource) ti.getData();
					if (dia != null)
						dia.close();
				}
			}
		});
	}

	/**
	 * set the file extension list,file will be list only if it has a extension
	 * in <i>exts</i>
	 * 
	 * @param exts
	 *            like {"bm","svc"}<br>
	 *            set to null means disable this feather(default),all file will
	 *            be listed
	 */
	public void setExtFilter(String[] exts) {
		extFilters = exts;
	}

	public void setInput(IContainer iContainer) {
		this.root = iContainer;
		if (dia != null) {
			dia.setBlockOnOpen(true);
			dia.create();
			dia.getShell().setText(
					"Select Resource [" + root.getProject().getName() + "]");
			if (dia.open() != IDialogConstants.OK_ID)
				result = null;
		}
		if (treeViewer != null && !treeViewer.getTree().isDisposed())
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

	private class ResourceContentProvider extends WorkbenchContentProvider
			implements Comparator<IResource> {

		public Object[] getChildren(Object element) {
			Object[] objs = super.getChildren(element);
			ArrayList<IResource> als = new ArrayList<IResource>();
			for (Object o : objs) {
				IResource r = (IResource) o;
				if (r.getName().startsWith("."))
					continue;
				if (hasChildren(r))
					als.add(r);
				else if (accept(r.getName()))
					als.add(r);
			}
			IResource[] res = new IResource[als.size()];
			als.toArray(res);
			Arrays.sort(res, this);
			return res;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof IContainer) {
				IContainer folder = (IContainer) element;
				Object[] res = getChildren(folder);
				if (res.length == 0)
					return false;
				for (Object o : res) {
					if (o instanceof IFile) {
						return true;
					} else if ((o instanceof IFolder) && hasChildren(o)) {
						return true;
					}
				}
			}
			return false;
		}

		public int compare(IResource o1, IResource o2) {
			int i1 = (o1 instanceof IFolder) ? 0 : 1;
			int i2 = (o2 instanceof IFolder) ? 0 : 1;
			int r = i1 - i2;
			if (r == 0)
				r = o1.getName().toLowerCase()
						.compareTo(o2.getName().toLowerCase());
			return r;
		}
	}

	protected boolean accept(String fileName) {
		if (extFilters == null)
			return true;
		fileName = fileName.toLowerCase();
		for (String ext : extFilters) {
			if (fileName.endsWith("." + ext.toLowerCase()))
				return true;
		}
		return false;
	}

	public Dialog getDialog() {
		return dia;
	}

	public void setResult(IResource result) {
		this.result = result;
	}
	
}
