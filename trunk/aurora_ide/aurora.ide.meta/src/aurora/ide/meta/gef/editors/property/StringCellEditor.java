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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class StringCellEditor extends CellEditor implements FocusListener,
		KeyListener {
	private Text text;
	private boolean readOnly = false;

	public StringCellEditor() {
		setStyle(SWT.NONE);
	}

	public StringCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	public StringCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	public void activate() {
		text.setText("");
		fireApplyEditorValue();
	}

	public void setReadOnly(boolean readOnly) {
		text.setEnabled(!readOnly);
		this.readOnly = readOnly;
	}

	@Override
	protected Control createControl(Composite parent) {
		text = new Text(parent, SWT.SINGLE);
		text.addFocusListener(this);
		text.addKeyListener(this);
		text.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_IBEAM));
		text.setEnabled(!readOnly);
		return text;
	}

	@Override
	protected Object doGetValue() {
		return text.getText();
	}

	@Override
	protected void doSetFocus() {

	}

	@Override
	protected void doSetValue(Object value) {
		text.setText(value == null ? "" : (String) value);
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
