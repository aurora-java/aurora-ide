package aurora.ide.meta.gef.editors.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class DialogCellEditor extends CellEditor implements SelectionListener,
		MouseListener {
	private Button button;
	private CLabel label;
	private DialogEditableObject value;
	private Class<? extends EditWizard> clazz;
	Shell shell;

	public DialogCellEditor(Composite parent, Class<? extends EditWizard> clazz) {
		super(parent, SWT.NONE);
		this.clazz = clazz;
	}

	@Override
	protected Control createControl(Composite parent) {
		shell = parent.getShell();
		Composite com = new Composite(parent, SWT.NONE);
		com.setBackground(parent.getBackground());
		com.setLayout(new SimpleLayout());
		label = new CLabel(com, SWT.NONE);
		label.addMouseListener(this);
		button = new Button(com, SWT.FLAT);
		button.setText("...");
		button.addSelectionListener(this);
		return com;
	}

	@Override
	protected Object doGetValue() {
		return value;
	}

	@Override
	protected void doSetFocus() {

	}

	@Override
	protected void doSetValue(Object value) {
		this.value = (DialogEditableObject) value;
		label.setText(this.value.getDescripition());
		label.setImage(this.value.getDisplayImage());
	}

	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
			super.activate(activationEvent);
		}
	}

	private class SimpleLayout extends Layout {
		private int width = 17;

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			return new Point(800, 20);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			Point size = composite.getSize();
			label.setBounds(0, 0, size.x - width, size.y);
			button.setBounds(size.x - width, 0, width, size.y);
		}

	}

	private void showDialog() {
		try {
			EditWizard wizard = clazz.newInstance();
			DialogEditableObject objClone = value.clone();
			wizard.setDialogEdiableObject(objClone);
			WizardDialog wd = new WizardDialog(shell, wizard);
			if (wd.open() == WizardDialog.OK) {
				value = objClone;
				label.setText(value.getDescripition());
				label.setImage(value.getDisplayImage());
				fireApplyEditorValue();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void widgetSelected(SelectionEvent e) {
		showDialog();
	}

	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public void mouseDoubleClick(MouseEvent e) {
		showDialog();
	}

	public void mouseDown(MouseEvent e) {
	}

	public void mouseUp(MouseEvent e) {
	}

}
