package aurora.ide.editor.widgets;

import org.eclipse.jface.viewers.ICellModifier;

import uncertain.composite.CompositeMap;

public class PlainCompositeMapCellModifier implements ICellModifier{

	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		CompositeMap record = (CompositeMap)element;
		return record.getString(property);
	}

	public void modify(Object element, String property, Object value) {
	}

}
