package aurora.ide.prototype.consultant.view;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CNFContentProvider implements ITreeContentProvider {
	private static final Object[] EMPTY_ARRAY = new Object[0];

	public Object[] getChildren(Object parentElement) {
		// if (parentElement instanceof Root) {
		// if (parents == null) {
		// initializeParents(parentElement);
		// }
		// return parents;
		// } else if (parentElement instanceof Parent) {
		// return ((Parent) parentElement).getChildren();
		// } else if (parentElement instanceof Child) {
		// return EMPTY_ARRAY;
		// } else {
		// return EMPTY_ARRAY;
		// }
		if (parentElement instanceof Node) {
			((Node) parentElement).makeChildren();
			List<Node> children = ((Node) parentElement).getChildren();
			return children.toArray(new Node[children.size()]);
		}

		return EMPTY_ARRAY;

	}

	public Object getParent(Object element) {
		if (element instanceof Node) {
			return ((Node) element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Node) {
			return ((Node) element).hasChildren();
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
