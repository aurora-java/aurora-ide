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

import aurora.ide.meta.gef.designer.DataType;
import aurora.ide.meta.gef.designer.DesignerMessages;
import aurora.ide.meta.gef.designer.IDesignerConst;
import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.property.BooleanCellEditor;
import aurora.ide.meta.gef.editors.property.StringCellEditor;

public class BMModelViewer extends TableViewer implements IDesignerConst {

	public static final String[] editor_types = Input.INPUT_TYPES;

	private HashMap<String, CellEditor> editorMap = new HashMap<String, CellEditor>(
			1000);
	private HashMap<String, String[]> operatorsMap = new HashMap<String, String[]>();

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
		column.setText(DesignerMessages.BMModelViewer_0);
		column.pack();
		column.setResizable(false);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.BMModelViewer_1);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.BMModelViewer_2);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.BMModelViewer_3);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.BMModelViewer_4);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.BMModelViewer_5);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText(DesignerMessages.BMModelViewer_6);
		column.setWidth(100);
		column = new TableColumn(table, SWT.NONE);
		column.setText("Options");
		column.setWidth(200);
		// ///////
		setContentProvider(new BMModelContentProvider(BMModel.RECORD));
		setLabelProvider(new BMModelLabelProvider(BMModel.RECORD));
		setCellModifier(new BMModelCellModifier(this, BMModel.RECORD));
		setColumnProperties(TABLE_COLUMN_PROPERTIES);
		setCellEditors(getNewCellEditors(null));
	}

	public void refresh() {
		long t1 = System.currentTimeMillis();
		Table table = getTable();
		ArrayList<String> keys = new ArrayList<String>(editorMap.keySet());
		super.refresh();
		for (int r = 0; r < table.getItemCount(); r++) {
			TableItem item = table.getItem(r);
			Record record = (Record) item.getData();
			CellEditor[] ces = getNewCellEditors(record);
			for (int c = 0; c < ces.length; c++) {
				if (ces[c] == null)
					continue;
				String colpro = TABLE_COLUMN_PROPERTIES[c];
				Object colval = record.get(colpro);
				String key = r + "-" + c;
				keys.remove(key);
				CellEditor ce = editorMap.get(key);
				if (ce != null) {
					Object v = ce.getValue();
					if (v != null && v.equals(colval)) {
						continue;
					}
				}
				disposeEditor(key);
				updateOptionEditor(ces[c], record);
				setItemEditor(table, item, ces[c], r, c);
				ces[c].setValue(colval);
				// System.out.println(record.getPrompt() + ":" + colpro
				// + "  updated");
				ces[c].addListener(new RecordCellEditorListener(record, colpro,
						ces[c]));
				editorMap.put(key, ces[c]); //$NON-NLS-1$
			}
		}
		for (String key : keys) {
			disposeEditor(key);
		}
		// table.setRedraw(true);
		System.out.println("all:" + (System.currentTimeMillis() - t1));
	}

	/**
	 * if only one record needs update (editor) ,this method is much more better
	 * than {@link #refresh()}
	 * 
	 * @param record
	 */
	public void refresh(Record record) {
		super.refresh(record);
		long t1 = System.currentTimeMillis();
		Table table = getTable();
		for (int r = 0; r < table.getItemCount(); r++) {
			TableItem item = table.getItem(r);
			Record rec = (Record) item.getData();
			if (rec == record) {
				CellEditor[] ces = getNewCellEditors(record);
				for (int c = 0; c < ces.length; c++) {
					if (ces[c] == null)
						continue;
					String colpro = TABLE_COLUMN_PROPERTIES[c];
					Object colval = rec.get(colpro);
					String key = r + "-" + c;
					disposeEditor(key);
					updateOptionEditor(ces[c], rec);
					setItemEditor(table, item, ces[c], r, c);
					ces[c].setValue(colval);
					ces[c].addListener(new RecordCellEditorListener(record,
							colpro, ces[c]));
					editorMap.put(key, ces[c]);
				}
				break;
			}
		}
		System.out.println(record.getPrompt() + ":"
				+ (System.currentTimeMillis() - t1));
	}

	private void disposeEditor(String key) {
		CellEditor ce = editorMap.get(key);
		if (ce != null) {
			ce.deactivate();
			ce.dispose();
			editorMap.put(key, null);
		}
	}

	private void updateOptionEditor(CellEditor ce, Record rec) {
		if (ce instanceof OptionsCellEditor) {
			boolean needOptions = false;
			needOptions = rec.getEditor().equals(Input.Combo);
			if (!needOptions)
				needOptions = rec.getEditor().equals(Input.LOV);
			((OptionsCellEditor) ce).setEnable(needOptions);
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
		String[] ss = getOperators(record == null ? "" : record.getType()); //$NON-NLS-1$
		return new CellEditor[] { null, null, new StringCellEditor(table),
				new ComboBoxCellEditor(table, data_types),
				new StringCellEditor(table),
				new ComboBoxCellEditor(table, editor_types),
				new BooleanCellEditor(table),
				new ComboBoxCellEditor(table, ss), new OptionsCellEditor(table) };
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
				String nextKey = String.format("%d-%d", nextR, c); //$NON-NLS-1$
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
