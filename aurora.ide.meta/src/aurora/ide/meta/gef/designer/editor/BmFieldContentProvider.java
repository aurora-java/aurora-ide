package aurora.ide.meta.gef.designer.editor;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import uncertain.composite.CompositeMap;

public class BmFieldContentProvider implements IStructuredContentProvider {

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		ArrayList<CompositeMap> list = (ArrayList<CompositeMap>) inputElement;
		CompositeMap[] fields = new CompositeMap[list.size()];
		list.toArray(fields);
		return fields;
	}
}