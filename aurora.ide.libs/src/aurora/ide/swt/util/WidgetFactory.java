package aurora.ide.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class WidgetFactory {

	public static TextField createTextField(Composite parent, String label) {
		return createTextField(parent, label, new GridData(
				GridData.FILL_HORIZONTAL));
	}

	public static TextField createTextField(Composite parent, String label,
			Object layoutData) {
		TextField textField = new TextField();
		textField.createTextField(parent, label, layoutData);
		return textField;
	}

	public static TextField createTextButtonField(Composite parent,
			String label, String buttonText) {
		TextField textField = new TextField(true, buttonText);
		textField.createTextField(parent, label, new GridData(
				GridData.FILL_HORIZONTAL));
		return textField;
	}

	static public Composite composite(Composite p) {
		Composite composite = new Composite(p, SWT.NONE);
		composite.setLayout(new GridLayout());
		return composite;
	}

	static public Group group(Composite p, String text) {
		Group g = new Group(p, SWT.NONE);
		g.setText(text);
		g.setLayout(new GridLayout());
		return g;
	}

	static public Label hSeparator(Composite parent) {
		Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return label;
	}

	static public Label vSeparator(Composite parent) {
		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		label.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		return label;
	}

	static public org.eclipse.swt.widgets.Button button(Composite p, String text) {
		org.eclipse.swt.widgets.Button button = new org.eclipse.swt.widgets.Button(
				p, SWT.NONE);
		button.setText(text);
		return button;
	}
}
