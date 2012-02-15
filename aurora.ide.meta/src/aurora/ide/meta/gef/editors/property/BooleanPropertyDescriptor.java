package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BooleanPropertyDescriptor extends PropertyDescriptor {
	private boolean readOnly = false;

	public BooleanPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public BooleanPropertyDescriptor(Object id, String displayName,
			boolean readOnly) {
		super(id, displayName);
		this.readOnly = readOnly;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		BooleanCellEditor editor = new BooleanCellEditor(parent);
		editor.setReadOnly(readOnly);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
