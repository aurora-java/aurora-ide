package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

public class IntegerCellEditor extends CellEditor implements FocusListener,
		KeyListener {
	private Spinner spinner;
	private int min = 0, max = Integer.MAX_VALUE, step1 = 5, step2 = 30;

	public IntegerCellEditor(Composite parent) {
		min = 0;
		max = Integer.MAX_VALUE;
		step1 = 5;
		step2 = 30;
		setStyle(SWT.NONE);
		create(parent);
	}

	public IntegerCellEditor(Composite parent, int min, int max, int step1,
			int step2) {
		this.min = min;
		this.max = max;
		this.step1 = step1;
		this.step2 = step2;
		setStyle(SWT.NONE);
		create(parent);
	}

	public void activate() {
		spinner.setSelection(0);
		fireApplyEditorValue();
	}

	public void setReadOnly(boolean readOnly) {
		spinner.setEnabled(!readOnly);
	}

	@Override
	protected Control createControl(Composite parent) {
		spinner = new Spinner(parent, SWT.NONE);
		spinner.setIncrement(step1);
		spinner.setPageIncrement(step2);
		spinner.setDigits(0);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		spinner.addFocusListener(this);
		spinner.addKeyListener(this);
		return spinner;
	}

	@Override
	protected Object doGetValue() {
		return spinner.getSelection();
	}

	@Override
	protected void doSetFocus() {

	}

	@Override
	protected void doSetValue(Object value) {
		spinner.setSelection(value == null ? 0 : (Integer) value);
	}

	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
			super.activate(activationEvent);
		}
	}

	public void focusGained(FocusEvent e) {

	}

	public void focusLost(FocusEvent e) {
		fireApplyEditorValue();
	}

	public void keyPressed(KeyEvent e) {
		// 回车
		if (e.keyCode == 13) {
			fireApplyEditorValue();
		}
	}

	public void keyReleased(KeyEvent e) {

	}

}
