package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class LinkPropertyDescriptor extends PropertyDescriptor {

	public LinkPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new LinkCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
