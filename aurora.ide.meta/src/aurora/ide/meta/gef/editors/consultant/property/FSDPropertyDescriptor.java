package aurora.ide.meta.gef.editors.consultant.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aurora.ide.meta.gef.editors.property.StylePropertyDescriptor;

public class FSDPropertyDescriptor extends StylePropertyDescriptor {

	public FSDPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new CellEditor() {

			@Override
			protected Control createControl(Composite parent) {
				return FSDPropertyDescriptor.this.createControl(parent);
			}

			@Override
			protected Object doGetValue() {
				return null;
			}

			@Override
			protected void doSetFocus() {

			}

			@Override
			protected void doSetValue(Object value) {

			}
			public void deactivate() {
				
			}

		};
	}

	public Control createControl(Composite parent) {
		return null;
	}

}
