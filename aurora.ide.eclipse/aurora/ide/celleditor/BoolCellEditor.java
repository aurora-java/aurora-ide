/**
 * 
 */
package aurora.ide.celleditor;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import aurora.ide.helpers.LocaleMessage;


/**
 * @author linjinxiao
 * 
 */
public class BoolCellEditor extends CheckboxCellEditor implements ICellEditor {

	private Button button;
	private boolean hasSelection = false;

	protected CellInfo cellProperties;

	public BoolCellEditor(CellInfo cellProperties) {
		this.cellProperties = cellProperties;
	}
	protected Control createControl(Composite parent) {
		if(isTableItemEditor()){
			Composite content = new NewButton(parent, SWT.NONE);
			content.setLayout(new FillLayout());
			button = new Button(content, SWT.CHECK);
//			button.setLayoutData(gridData);
			button.setBackground(parent.getBackground());
			content.setBackground(parent.getBackground());
			return content;
		}
		else{
			return super.createControl(parent);
		}
	}
	
	public boolean validValue(String value) {
		boolean validValue = true;
		String errorMessage = "";
		if(cellProperties.isRequired() && value == null){
			validValue = false;
			errorMessage = "<"+cellProperties.getColumnName()+">"+LocaleMessage.getString("field")+LocaleMessage.getString("is.required");
		}
		if(value != null && (!value.equals("true")) &&(!value.equals("false"))){
			validValue = false;
			errorMessage = "<"+cellProperties.getColumnName()+">"+LocaleMessage.getString("field")+LocaleMessage.getString("value.must.be.true.or.false");
		}
		if(!validValue){
			setErrorMessage(errorMessage);
			if(getCellControl() != null)
				getCellControl().setFocus();
			return false;
		}
		return true;
	}

	public Control getCellControl() {
		return super.getControl();
	}

	public String getSelection() {
		if (button != null) {
			if(!hasSelection)
				return null;
			if (button.getSelection()) {
				return "true";
			} else
				return "false";
		} else {
			if (super.getValue() == null)
				return null;
			return super.getValue().toString();
		}
	}
	public Object valueToShow(String value){
		Boolean showValue = null;
		if ("true".equals(value))
			showValue = new Boolean(true);
		else
			showValue = new Boolean(false);
		return showValue;
	}

	public void SetSelection(String dataValue) {
		if(dataValue == null)
			dataValue = "false";
		Boolean showValue = (Boolean)valueToShow(dataValue);
		if(button != null){
			hasSelection = true;
			button.setSelection(showValue.booleanValue());
		}else{
			super.setValue(showValue);
		}
	}

	public Control createCellControl(Composite parent) {
		return super.getControl();
	}

	public void dispose() {
		super.dispose();

	}
	public void init() {
		Table parent = cellProperties.getTable();
		createCellEditor(parent);
		Color bg = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
		if (cellProperties.isRequired()) {
			if(getCellControl() != null)
				getCellControl().setBackground(bg);
		}
		if(isTableItemEditor()){
			SetSelection(cellProperties.getRecord().getString(cellProperties.getColumnName()));
			addCellListener();
		}
	}

	private void addCellListener() {
		button.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if(!hasSelection)
					hasSelection = true;
				String dataValue = getSelection();
				cellProperties.getRecord().put(cellProperties.getColumnName(), dataValue);
				cellProperties.getTableViewer().refresh(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);

			}
		});
	}

	public void createCellEditor(Composite parent) {
		super.create(parent);
	}

	public CellEditor getCellEditor() {
		return this;
	}
	private boolean isTableItemEditor(){
		return cellProperties.getTableItem() != null;
	}
	class NewButton extends Composite{
		public NewButton(Composite parent, int style) {
			super(parent, SWT.NONE);
		}
		public String toString(){
			return "ok"+button.toString();
		}		
	}
}
