package aurora.ide.meta.gef.designer.editor;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;

public class BMModelCellModifier implements ICellModifier {
	private TableViewer viewer;
	private int type;

	public BMModelCellModifier(TableViewer viewer, int type) {
		this.viewer = viewer;
		this.type = type;
	}

	public boolean canModify(Object element, String property) {
		return type == BMModel.RECORD;
	}

	public Object getValue(Object element, String property) {
		Record r = (Record) element;
		return r.get(property);
	}

	public void modify(Object element, String property, Object value) {
		if (!(element instanceof TableItem))
			return;
		TableItem item = (TableItem) element;
		Object data = item.getData();
		if (data instanceof Record) {
			Record record = (Record) data;
			record.put(property, value);
			viewer.refresh();
		}
	}
}
