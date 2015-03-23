package aurora.ide.swt.util;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TableContentProvider implements IStructuredContentProvider {

	private final Object[] EMPTY_ARR = new Object[0];

	public TableContentProvider() {
	}

	public void dispose() {
		// nothing to do
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		if (inputElement instanceof List) {
			return ((List<?>) inputElement)
					.toArray(new Object[((List<?>) inputElement).size()]);
		}
		return EMPTY_ARR;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
