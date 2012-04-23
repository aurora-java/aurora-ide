package aurora.ide.meta.gef.editors.property;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

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

	private class ResourceContentProvider extends
			ResourceSelector.ResourceContentProvider {
		public Object[] getElements(Object element) {
			if (element instanceof Object[]) {
				Object[] elements = (Object[]) element;
				return elements;
			}
			return getChildren(element);
		}
	}
}
