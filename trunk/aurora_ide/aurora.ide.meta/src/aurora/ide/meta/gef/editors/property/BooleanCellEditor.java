package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BooleanCellEditor extends CellEditor implements SelectionListener {

	private static final int defaultStyle = SWT.NONE;
	private Button checkButton;

	public BooleanCellEditor(Composite parent) {
		super(parent, defaultStyle);
	}

	public void activate() {
		checkButton.setSelection(!checkButton.getSelection());
		fireApplyEditorValue();
	}

	protected Control createControl(Composite parent) {
		checkButton = new Button(parent, SWT.CHECK);
		checkButton.addSelectionListener(this);
		return checkButton;
	}

	protected Object doGetValue() {
		return checkButton.getSelection();
	}

	protected void doSetFocus() {
		// Ignore
	}

	public void setReadOnly(boolean readOnly) {
		checkButton.setEnabled(!readOnly);
	}

	protected void doSetValue(Object value) {
		checkButton.setSelection(value == null ? false : ((Boolean) value));
		checkButton.setText(Boolean.toString(checkButton.getSelection()));
	}

	public void widgetSelected(SelectionEvent e) {
		checkButton.setText(Boolean.toString(checkButton.getSelection()));
		fireApplyEditorValue();
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
