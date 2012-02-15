package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

public class PropertyItem {
	private IPropertySheetEntry data;
	private CellEditor editor = null;

	public PropertyItem(IPropertySheetEntry pse) {
		data = pse;
	}

	public PropertyItem(IPropertySheetEntry pse, int index) {
		data = pse;
	}

	public void setData(IPropertySheetEntry pse) {
		data = pse;
	}

	public IPropertySheetEntry getData() {
		return data;
	}

	public String getLabel() {
		return data.getDisplayName();
	}

	public Control getControl(Composite par) {
		if (editor == null)
			editor = data.getEditor(par);
		Control control = editor.getControl();
		if (control == null) {
			editor = data.getEditor(par);
			return editor.getControl();
		}
		return control;
	}
}
