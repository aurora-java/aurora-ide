/**
 * 
 */
package aurora.ide.celleditor;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import aurora.ide.helpers.LocaleMessage;


/**
 * @author linjinxiao
 * 
 */
public class ComboxCellEditor extends ComboBoxCellEditor implements ICellEditor {

	protected CellInfo cellProperties;

	public ComboxCellEditor(CellInfo cellProperties) {
		this.cellProperties = cellProperties;
	}

	public void createCellEditor(Composite parent) {
		super.setItems(cellProperties.getItems());
		super.create(parent);
		super.setValue(new Integer(-1));
	}

	public Control getCellControl() {
		return super.getControl();
	}

	public CellEditor getCellEditor() {
		return this;
	}

	public String getSelection() {
		Object value = getValue();
		Integer newInt = (Integer) value;
		if (newInt.intValue() == -1)
			return null;
		String dataValue = cellProperties.getItems()[newInt.intValue()];
		return dataValue;
	}

	public void init() {
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
		if (cellProperties.isRequired()) {
			if (getCellControl() != null)
				getCellControl().setBackground(bg);
		}
		Table parent = cellProperties.getTable();
		createCellEditor(parent);
		
		if(isTableItemEditor()){
			SetSelection(cellProperties.getRecord().getString(cellProperties.getColumnName()));
			addCellListener();
		}
	}

	public void SetSelection(String value) {
		Integer showValue = (Integer) valueToShow(value);
		super.setValue(showValue);

	}

	public boolean validValue(String value) {
		boolean validResult = false;
		String selections = "";
		String errorMessage = "";
		String[] items = cellProperties.getItems();
		for (int i = 0; i < items.length; i++) {
			String selection = items[i];
			selections += selection + ",";
			if (value == null && selection.equals(""))
				return true;
			if (value != null && value.equals(selection))
				return true;
		}
		if (!validResult) {
			errorMessage =  "<" + cellProperties.getColumnName()
					+ ">"+LocaleMessage.getString("field")+LocaleMessage.getString("value.must.be.in")+"'" + selections + "' !";
			setErrorMessage(errorMessage);
			getCellControl().setFocus();
			return false;
		}
		return validResult;
	}
	public Object valueToShow(String value) {
		Integer showValue = null;
		String[] items = cellProperties.getItems();
		for (int i = 0; i < items.length; i++) {
			if (value == null && items[i].equals("")) {
				showValue = new Integer(i);
				break;
			}
			if (items[i].equals(value)) {
				showValue = new Integer(i);
				break;
			}
		}
		if (showValue == null)
			showValue = new Integer(-1);
		return showValue;
	}


private void addCellListener() {
	this.addListener(new ICellEditorListener() {

		public void editorValueChanged(boolean oldValidState,
				boolean newValidState) {
		}
		public void cancelEditor() {
		}

		public void applyEditorValue() {
			String dataValue = getSelection();
			cellProperties.getRecord().put(cellProperties.getColumnName(), dataValue);
			cellProperties.getTableViewer().refresh(true);
		}
	});
	getCellControl().addFocusListener(new FocusListener() {

		public void focusLost(FocusEvent e) {
			fillTableCellEditor(cellProperties.getTableViewer().getViewer().getTable(),
					cellProperties.getTableItem());

		}
		public void focusGained(FocusEvent e) {
		}
	});
}
	private void fillTableCellEditor(Table table, TableItem item) {
		TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.setEditor(getCellControl(), item, 1);
	}
	private boolean isTableItemEditor(){
		return cellProperties.getTableItem() != null;
	}
}
