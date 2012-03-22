package aurora.ide.meta.gef.designer.editor;

import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.meta.gef.designer.model.BMModel;
import aurora.ide.meta.gef.designer.model.Record;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.property.BooleanCellEditor;
import aurora.ide.meta.gef.editors.property.StringCellEditor;

public class BMModelViewer extends TableViewer {
	public static final String COLUMN_NUM = "NO.";
	public static final String COLUMN_PROMPT = "PROMPT";
	public static final String COLUMN_TYPE = "TYPE";
	public static final String COLUMN_NAME = "NAME";
	public static final String COLUMN_EDITOR = "EDITOR";
	public static final String COLUMN_QUERYFIELD = "QUERYFEILD";
	public static final String COLUMN_ISFOREIGN = "FOREIGN";
	public static final String[] TABLE_COLUMN_PROPERTIES = { "", COLUMN_NUM,
			COLUMN_PROMPT, COLUMN_TYPE, COLUMN_NAME, COLUMN_EDITOR,
			COLUMN_QUERYFIELD, COLUMN_ISFOREIGN };

	public static final String[] data_types = { "text", "long text", "integer",
			"float", "date", "dateTime" };
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
		column.setText("外键设置");
		column.setWidth(100);
		// ///////
		setContentProvider(new BMModelContentProvider(BMModel.RECORD));
		setLabelProvider(new BMModelLabelProvider(BMModel.RECORD));
		setCellModifier(new BMModelCellModifier(this, BMModel.RECORD));
		setColumnProperties(TABLE_COLUMN_PROPERTIES);
		setCellEditors(getNewCellEditors());
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
		// Rectangle clientRect = table.getClientArea();
		// int topIndex = table.getTopIndex();
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Record r = (Record) item.getData();
			CellEditor[] ces = getNewCellEditors();
			// boolean visible = false;
			for (int c = 0; c < ces.length; c++) {
				if (ces[c] == null)
					continue;
				// if (!item.getBounds(c).intersects(clientRect)) {
				// continue;
				// }
				// visible = true;
				TableEditor te = new TableEditor(table);
				te.horizontalAlignment = SWT.LEFT;
				te.grabHorizontal = true;
				Control ctrl = ces[c].getControl();
				te.setEditor(ctrl, item, c);
				// ces[c].activate();
				ces[c].setValue(r.get(TABLE_COLUMN_PROPERTIES[c]));
				ces[c].addListener(new RecordCellEditorListener(r,
						TABLE_COLUMN_PROPERTIES[c], ces[c]));
				editorMap.put(i + "-" + c, ces[c]);
			}
			// if (!visible)
			// break;
		}
		table.setRedraw(true);
	}

	private CellEditor[] getNewCellEditors() {
		Table table = getTable();
		return new CellEditor[] { null, null, new StringCellEditor(table),
				new ComboBoxCellEditor(table, data_types),
				new StringCellEditor(table),
				new ComboBoxCellEditor(table, editor_types),
				new BooleanCellEditor(table), null };
	}
}
