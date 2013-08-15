package aurora.ide.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TextField {

	private Label l;
	private Text t;
	private boolean hasButton;
	private Button b;
	private String buttonText;

	public TextField() {
		this(false,"");
	}

	public TextField(boolean hasButton,String buttonText) {
		this.hasButton = hasButton;
		this.buttonText = buttonText;
	}

	public Text createTextField(Composite parent, String label) {
		return createTextField(parent, label, new GridData(GridData.FILL_HORIZONTAL));
	}

	public Text createTextField(Composite parent, String label,
			Object layoutData) {
		l = new Label(parent, SWT.NONE);
		l.setText(label);
		t = new Text(parent, SWT.NONE|SWT.BORDER);
		t.setLayoutData(layoutData);
		if (hasButton) {
			b = new Button(parent, SWT.NONE);
			b.setText(buttonText);
		}
		return t;
	}

	public Button getButton() {
		return b;
	}
	public void setText(String text){
		t.setText(text);
	}

	public Text getText() {
		return t;
	}
	public void addModifyListener(ModifyListener listener) {
		t.addModifyListener(listener);
	}
	public void removeModifyListener(ModifyListener listener) {
		t.removeModifyListener(listener);
	}
	public void addButtonClickListener(SelectionListener listener) {
		b.addSelectionListener(listener);
	}

	public void addVerifyListener(VerifyListener listener) {
		t.addVerifyListener(listener);
	}
	
	public void removeButtonClickListener(SelectionListener listener) {
		b.removeSelectionListener(listener);
	}

	public void removeVerifyListener(VerifyListener listener) {
		t.removeVerifyListener(listener);
	}
}
