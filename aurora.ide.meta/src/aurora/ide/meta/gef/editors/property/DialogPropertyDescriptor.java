package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

public class DialogPropertyDescriptor extends StylePropertyDescriptor {
	private Class<? extends EditWizard> clazz;

	public DialogPropertyDescriptor(Object id, String displayName,
			Class<? extends EditWizard> clazz) {
		super(id, displayName);
		this.clazz = clazz;
	}

	public DialogPropertyDescriptor(Object id, String displayName,
			Class<? extends EditWizard> clazz, int style) {
		this(id, displayName, clazz);
		this.setStyle(style);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new DialogCellEditor(parent, clazz);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
