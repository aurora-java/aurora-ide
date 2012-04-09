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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;

public class MutilInputResourceSelector extends ResourceSelector {

	public MutilInputResourceSelector(Shell shell) {
		super(shell);
	}

	@Override
	public void setInput(IContainer iContainer) {
		setInputs(new IContainer[] { iContainer });
	}

	public void setInputs(IContainer[] iContainers) {
		Dialog dia = this.getDialog();
		if (dia != null) {
			dia.setBlockOnOpen(true);
			dia.create();
			TreeViewer treeViewer = this.getTreeViewer();
			treeViewer.setInput(iContainers);
			dia.getShell().setText("Select Resource ");
			if (dia.open() != IDialogConstants.OK_ID)
				this.setResult(null);

		}
		TreeViewer treeViewer = this.getTreeViewer();
		if (treeViewer != null && !treeViewer.getTree().isDisposed())
			treeViewer.setInput(iContainers);
	}

	@Override
	protected void createControl(Composite com) {
		super.createControl(com);
		this.getTreeViewer().setContentProvider(new ResourceContentProvider());
	}

	private class ResourceContentProvider extends WorkbenchContentProvider
			implements Comparator<IResource> {
		public Object[] getElements(Object element) {
			if (element instanceof Object[]) {
				Object[] elements = (Object[]) element;
				return elements;
			}
			return getChildren(element);
		}

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

}
