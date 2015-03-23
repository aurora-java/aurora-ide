package aurora.bpmn.designer.rcp.viewer.action.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateBPMDefineDialog extends Dialog {
	private Text errorMessageText;
	private String errorMessage;

	public CreateBPMDefineDialog(Shell parentShell) {
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
		n.setText("BPM Name");
		Text t = new Text(c, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		n = new Label(c, SWT.NONE);
		n.setText("Code");
		t = new Text(c, SWT.PASSWORD | SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		n = new Label(c, SWT.NONE);
		n.setText("Version");
		t = new Text(c, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		n = new Label(c, SWT.NONE);
		n.setText("Description");
		t = new Text(c, SWT.BORDER|SWT.MULTI);
		GridData data = 	new GridData(GridData.FILL_BOTH);
		data.verticalSpan = 5;
		t.setLayoutData(data);
		

//		private String name;
//		private String define_id;
//		private String process_code;
//		private String process_version;
//		private String current_version_flag;
//		private String defines;
//		private String description;

		errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
		// ));
		errorMessageText.setBackground(errorMessageText.getDisplay()
				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		errorMessageText.setForeground(errorMessageText.getDisplay()
				.getSystemColor(SWT.COLOR_RED));
		setErrorMessage(null);
		
//		"  " + "cccc"
		return c;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(580,400);
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

}
