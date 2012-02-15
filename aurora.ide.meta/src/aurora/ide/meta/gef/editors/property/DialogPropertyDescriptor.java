package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class DialogPropertyDescriptor extends PropertyDescriptor {
	private Class<? extends EditWizard> clazz;

	public DialogPropertyDescriptor(Object id, String displayName,
			Class<? extends EditWizard> clazz) {
		super(id, displayName);
		this.clazz = clazz;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new DialogCellEditor(parent, clazz);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
