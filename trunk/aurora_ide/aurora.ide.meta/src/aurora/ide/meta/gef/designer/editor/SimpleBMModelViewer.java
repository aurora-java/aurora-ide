package aurora.ide.meta.gef.designer.editor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.editors.property.BooleanCellEditor;
import aurora.ide.meta.gef.editors.property.StringCellEditor;

public class SimpleBMModelViewer extends TableViewer implements IDesignerConst {

	String[] properties = new String[] { "", COLUMN_NUM, COLUMN_PROMPT,
			COLUMN_NAME };
	CellEditor[] editors = new CellEditor[] { null, null, null, null };
	private Table table;
	private String mode = null;
	private HashMap<String, String[]> operatorsMap = new HashMap<String, String[]>();

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
		if (AE_LOV.equals(mode)) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText("Use in Lov");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("For Query");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("For Display");
			column.pack();
			properties = expand(properties, FOR_LOV, FOR_QUERY, FOR_DISPLAY);
			editors = expand(editors, new BooleanCellEditor(table),
					new BooleanCellEditor(table), new BooleanCellEditor(table));
		} else if (AE_MAINTAIN.equals(mode)) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText("For Insert");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("For Update");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("Insert Expression");
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText("Update Expression");
			column.pack();
			properties = expand(properties, FOR_INSERT, FOR_UPDATE,
					INSERT_EXPRESSION, UPDATE_EXPRESSION);
			editors = expand(editors, new BooleanCellEditor(table),
					new BooleanCellEditor(table), new StringCellEditor(table),
					new StringCellEditor(table));
		} else if (AE_QUERY.equals(mode)) {
			TableColumn column = new TableColumn(table, SWT.CENTER);
			column.setText(DesignerMessages.BMModelViewer_5);
			column.pack();
			column = new TableColumn(table, SWT.CENTER);
			column.setText(DesignerMessages.BMModelViewer_6);
			column.setWidth(110);
			properties = expand(properties, COLUMN_QUERYFIELD, COLUMN_QUERY_OP);
			editors = expand(editors, new BooleanCellEditor(table),
					new ComboBoxCellEditor(table, OPERATORS));
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
				CellEditor ce = createCellEditor(table, rec, properties[j],
						editors[j].getClass());
				if (ce == null)
					continue;
				TableEditor te = new TableEditor(table);
				te.horizontalAlignment = SWT.LEFT;
				te.grabHorizontal = true;
				te.setEditor(ce.getControl(), item, j);
				ce.setValue(rec.get(properties[j]));
			}
		}
	}

	private CellEditor createCellEditor(Composite parent, Record record,
			String property, Class<? extends CellEditor> type) {
		CellEditor ce = null;
		if (type.equals(StringCellEditor.class))
			ce = new StringCellEditor(parent);
		else if (type.equals(BooleanCellEditor.class))
			ce = new BooleanCellEditor(parent);
		else if (type.equals(ComboBoxCellEditor.class)) {
			if (property.equals(COLUMN_QUERY_OP)) {
				String[] ss = getOperators(record == null ? "" : record
						.getType());
				ce = new ComboBoxCellEditor(parent, ss);
			}
		}
		if (ce != null)
			ce.addListener(new RecordCellEditorListener(record, property, ce));
		return ce;
	}

	private String[] getOperators(String displayType) {
		String[] ss = operatorsMap.get(displayType);
		if (ss == null) {
			DataType dt = DataType.fromString(displayType);
			if (dt == null)
				return new String[0];
			ArrayList<String> ops = new ArrayList<String>();
			ops.add(OP_EQ);
			switch (dt) {
			case INTEGER:
			case FLOAT:
				ops.add(OP_GT);
				ops.add(OP_LT);
				ops.add(OP_GE);
				ops.add(OP_LE);
			case DATE:
			case DATE_TIME:
				ops.add(OP_INTERVAL);
				break;
			case TEXT:
			case LONG_TEXT:
				ops.add(OP_LIKE);
				ops.add(OP_PRE_MATCH);
				ops.add(OP_END_MATCH);
				ops.add(OP_ANY_MATCH);
				break;
			}
			ss = new String[ops.size()];
			ops.toArray(ss);
			operatorsMap.put(displayType, ss);
		}
		return ss;
	}
}
