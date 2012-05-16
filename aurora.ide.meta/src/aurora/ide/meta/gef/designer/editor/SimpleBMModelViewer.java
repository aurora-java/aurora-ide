package aurora.ide.meta.gef.designer.editor;

import java.lang.reflect.Array;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.editors.property.BooleanCellEditor;

public class SimpleBMModelViewer extends TableViewer {

	String[] properties = new String[] { "", IDesignerConst.COLUMN_NUM,
			IDesignerConst.COLUMN_PROMPT, IDesignerConst.COLUMN_NAME };
	CellEditor[] editors = new CellEditor[] { null, null, null, null };
	private Table table;
	private String mode = null;

	public SimpleBMModelViewer(Composite parent, int style) {
		super(parent, style);
		table = getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		TableColumn zColumn = new TableColumn(table, 0);
		zColumn.setResizable(false);
		zColumn.setWidth(0);
		zColumn.setMoveable(false);

		TableColumn column = new TableColumn(table, SWT.CENTER);
		column.setText("NO.");
		column.pack();
		column.setMoveable(false);
		column.setResizable(false);
		column = new TableColumn(table, SWT.CENTER);
		column.setText("Prompt");
		column.pack();
		column = new TableColumn(table, SWT.CENTER);
		column.setText("Name");
		column.setWidth(80);

		setColumnProperties(properties);
		setContentProvider(new BMModelContentProvider(BMModel.RECORD));
		setLabelProvider(new BMModelLabelProvider(BMModel.RECORD, properties));
		setCellModifier(new BMModelCellModifier(this, BMModel.RECORD));
		setCellEditors(editors);
	}

	public void setDisplayMode(String mode) {
		if (this.mode != null)
			return;
		this.mode = mode;
		if (IDesignerConst.AE_LOV.equals(mode)) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText("Use in Lov");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("For Quqery");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("For Display");
			column.pack();
			properties = expand(properties, IDesignerConst.FOR_LOV,
					IDesignerConst.FOR_QUERY, IDesignerConst.FOR_DISPLAY);
			editors = expand(editors, new BooleanCellEditor(table),
					new BooleanCellEditor(table), new BooleanCellEditor(table));
		} else if (IDesignerConst.AE_MAINTAIN.equals(mode)) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText("For Insert");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("For Update");
			column.pack();
			properties = expand(properties, IDesignerConst.FOR_INSERT,
					IDesignerConst.FOR_UPDATE);
			editors = expand(editors, new BooleanCellEditor(table),
					new BooleanCellEditor(table));
		} else if (IDesignerConst.AE_QUERY.equals(mode)) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText(DesignerMessages.BMModelViewer_5);
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText(DesignerMessages.BMModelViewer_6);
			column.setWidth(110);
			properties = expand(properties, IDesignerConst.COLUMN_QUERYFIELD,
					IDesignerConst.COLUMN_QUERY_OP);
			editors = expand(editors, new BooleanCellEditor(table),
					new ComboBoxCellEditor(table, IDesignerConst.OPERATORS));
		}

		setColumnProperties(properties);
		setLabelProvider(new BMModelLabelProvider(BMModel.RECORD, properties));
		setCellEditors(editors);
		refresh();
	}

	private <T> T[] expand(T[] arr, T... values) {
		@SuppressWarnings("unchecked")
		T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(),
				arr.length + values.length);
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		System.arraycopy(values, 0, newArr, arr.length, values.length);
		return newArr;
	}

	public void refresh() {
		super.refresh();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Record rec = (Record) item.getData();
			for (int j = 0; j < editors.length; j++) {
				if (editors[j] == null)
					continue;

				CellEditor ce = null;
				if (editors[j] instanceof BooleanCellEditor)
					ce = new BooleanCellEditor(table);
				else if (editors[j] instanceof ComboBoxCellEditor) {
					ComboBoxCellEditor cce = (ComboBoxCellEditor) editors[j];
					ce = new ComboBoxCellEditor(table, cce.getItems());
				} else
					continue;
				ce.addListener(new RecordCellEditorListener(rec, properties[j],
						ce));
				TableEditor te = new TableEditor(table);
				te.horizontalAlignment = SWT.LEFT;
				te.grabHorizontal = true;
				te.setEditor(ce.getControl(), item, j);
				ce.setValue(rec.get(properties[j]));
			}
		}
	}
}
