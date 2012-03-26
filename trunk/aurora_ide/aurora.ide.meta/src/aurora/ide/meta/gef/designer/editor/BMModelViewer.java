package aurora.ide.meta.gef.designer.editor;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.property.BooleanCellEditor;
import aurora.ide.meta.gef.editors.property.StringCellEditor;

public class BMModelViewer extends TableViewer implements IDesignerConst {

	public static final String[] editor_types = Input.INPUT_TYPES;

	private HashMap<String, CellEditor> editorMap = new HashMap<String, CellEditor>();

	public BMModelViewer(Composite parent) {
		super(parent);
		config();
	}

	public BMModelViewer(Composite parent, int style) {
		super(parent, style);
		config();
	}

	private void config() {
		Table table = getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		// define a zero-with first column,then the second column can be aligned
		// center (the first column can not be aligned center on windows)
		TableColumn zColumn = new TableColumn(table, SWT.NONE);
		zColumn.setResizable(false);
		zColumn.setWidth(0);
		TableColumn column = new TableColumn(table, SWT.CENTER);
		column.setText("编号");
		column.pack();
		column.setResizable(false);
		column = new TableColumn(table, SWT.NONE);
		column.setText("描述");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("类型");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("name");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("Editor");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("用作查询");
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("查询方式");
		column.setWidth(100);
		// ///////
		setContentProvider(new BMModelContentProvider(BMModel.RECORD));
		setLabelProvider(new BMModelLabelProvider(BMModel.RECORD));
		setCellModifier(new BMModelCellModifier(this, BMModel.RECORD));
		setColumnProperties(TABLE_COLUMN_PROPERTIES);
		// setCellEditors(getNewCellEditors(null));
	}

	public void refresh() {
		Table table = getTable();
		table.setRedraw(false);
		for (String key : editorMap.keySet()) {
			CellEditor editor = editorMap.get(key);
			editor.deactivate();
			editor.dispose();
		}
		editorMap.clear();
		super.refresh();
		for (int r = 0; r < table.getItemCount(); r++) {
			TableItem item = table.getItem(r);
			Record record = (Record) item.getData();
			CellEditor[] ces = getNewCellEditors(record);
			for (int c = 0; c < ces.length; c++) {
				if (ces[c] == null)
					continue;
				setItemEditor(table, item, ces[c], r, c);
				ces[c].setValue(record.get(TABLE_COLUMN_PROPERTIES[c]));
				ces[c].addListener(new RecordCellEditorListener(record,
						TABLE_COLUMN_PROPERTIES[c], ces[c]));
				editorMap.put(r + "-" + c, ces[c]);
			}
		}
		table.setRedraw(true);
	}

	/**
	 * if only one record needs update (editor) ,this method is much more better
	 * than {@link #refresh()}
	 * 
	 * @param record
	 */
	public void refresh(Record record) {
		Table table = getTable();
		for (int r = 0; r < table.getItemCount(); r++) {
			TableItem item = table.getItem(r);
			Record rec = (Record) item.getData();
			if (rec == record) {
				CellEditor[] ces = getNewCellEditors(record);
				for (int c = 0; c < ces.length; c++) {
					if (ces[c] == null)
						continue;
					String key = r + "-" + c;
					CellEditor ce = editorMap.get(key);
					if (ce != null) {
						ce.deactivate();
						ce.dispose();
						editorMap.put(key, null);
						ce = null;
					}
					setItemEditor(table, item, ces[c], r, c);
					ces[c].setValue(record.get(TABLE_COLUMN_PROPERTIES[c]));
					ces[c].addListener(new RecordCellEditorListener(record,
							TABLE_COLUMN_PROPERTIES[c], ces[c]));
					editorMap.put(key, ces[c]);
				}
				break;
			}
		}
	}

	private void setItemEditor(Table table, TableItem item, CellEditor ce,
			int r, int c) {
		TableEditor te = new TableEditor(table);
		te.horizontalAlignment = SWT.LEFT;
		te.grabHorizontal = true;
		Control ctrl = ce.getControl();
		ctrl.addKeyListener(new FocusMoveKeyListener(r, c));
		te.setEditor(ctrl, item, c);
	}

	private CellEditor[] getNewCellEditors(Record record) {
		Table table = getTable();
		String type = record.getType();
		ArrayList<String> ops = new ArrayList<String>();
		ops.add(OP_EQ);
		if (INTEGER.equals(type) || FLOAT.equals(type)) {
			ops.add(OP_GT);
			ops.add(OP_LT);
			ops.add(OP_GE);
			ops.add(OP_LE);
			ops.add(OP_INTERVAL);
		} else if (TEXT.equals(type) || LONG_TEXT.equals(type)) {
			ops.add(OP_LIKE);
			ops.add(OP_PRE_MATCH);
			ops.add(OP_END_MATCH);
			ops.add(OP_ANY_MATCH);
		} else if (DATE.equals(type) || DATE_TIME.equals(type)) {
			ops.add(OP_INTERVAL);
		}
		String[] ss = new String[ops.size()];
		ops.toArray(ss);
		return new CellEditor[] { null, null, new StringCellEditor(table),
				new ComboBoxCellEditor(table, data_types),
				new StringCellEditor(table),
				new ComboBoxCellEditor(table, editor_types),
				new BooleanCellEditor(table), new ComboBoxCellEditor(table, ss) };
	}

	private class FocusMoveKeyListener extends KeyAdapter {
		int r, c;

		public FocusMoveKeyListener(int r, int c) {
			this.r = r;
			this.c = c;
		}

		public void keyPressed(KeyEvent e) {
			if (e.stateMask != 0 && e.stateMask != SWT.SHIFT)
				return;
			int nextR = e.stateMask == 0 ? r + 1 : r - 1;
			if (e.keyCode == 13) {
				String nextKey = String.format("%d-%d", nextR, c);
				CellEditor ce = editorMap.get(nextKey);
				if (ce == null)
					return;
				Control ctrl = ce.getControl();
				ctrl.forceFocus();
				if (ctrl instanceof Text) {
					((Text) ctrl).selectAll();
				}
			}
		}
	}
}
