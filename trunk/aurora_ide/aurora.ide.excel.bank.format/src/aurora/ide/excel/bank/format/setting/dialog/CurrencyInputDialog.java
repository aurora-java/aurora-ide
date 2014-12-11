package aurora.ide.excel.bank.format.setting.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CurrencyInputDialog extends Dialog {

	private Text errorMessageText;
	private String errorMessage;

	private String currency_type;
	private String unit;

	public CurrencyInputDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite) super.createDialogArea(parent);
		GridLayout ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;

		Label n = new Label(c, SWT.NONE);
		n.setText("币种类型");
		final Text t = new Text(c, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setCurrency_type(t.getText());
			}
		});
		n = new Label(c, SWT.NONE);
		n.setText("币种单位");
		final Text t2 = new Text(c, SWT.BORDER);
		t2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t2.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setUnit(t2.getText());
			}
		});
		errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
		// ));
		errorMessageText.setBackground(errorMessageText.getDisplay()
				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		errorMessageText.setForeground(errorMessageText.getDisplay()
				.getSystemColor(SWT.COLOR_RED));
		setErrorMessage(null);
		if (this.currency_type != null) {
			t.setText(currency_type);
		}
		if (this.unit != null) {
			t2.setText(unit);
		}
		return c;
	}

	@Override
	protected Point getInitialSize() {
		// Point initialSize = super.getInitialSize();
		// return new Point(580, 400);
		return super.getInitialSize();
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		if (errorMessageText != null && !errorMessageText.isDisposed()) {
			errorMessageText
					.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
			// Disable the error message text control if there is no error, or
			// no error text (empty or whitespace only). Hide it also to avoid
			// color change.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
			boolean hasError = errorMessage != null
					&& (StringConverter.removeWhiteSpaces(errorMessage))
							.length() > 0;
			errorMessageText.setEnabled(hasError);
			errorMessageText.setVisible(hasError);
			errorMessageText.getParent().update();
			// Access the ok button by id, in case clients have overridden
			// button creation.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
			Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage == null);
			}
		}
	}

	public String getCurrency_type() {
		return currency_type;
	}

	public void setCurrency_type(String currency_type) {
		this.currency_type = currency_type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
