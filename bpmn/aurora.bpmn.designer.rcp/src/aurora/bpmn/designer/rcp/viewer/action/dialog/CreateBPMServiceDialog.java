package aurora.bpmn.designer.rcp.viewer.action.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
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

import aurora.ide.swt.util.WidgetFactory;

public class CreateBPMServiceDialog extends Dialog {
	private Text errorMessageText;
	private String errorMessage;

	public CreateBPMServiceDialog(Shell parentShell) {
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
		createButton(parent, 999, "Test Service", true);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cc = (Composite) super.createDialogArea(parent);
		
		
		Composite c2 = new Composite(cc,SWT.NONE);
		c2.setLayout(new GridLayout());
		c2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l = new Label(c2,SWT.NONE);
		l.setText("Service Model");
		l.setFont(JFaceResources.getBannerFont());
		WidgetFactory.hSeparator(c2);
		
		
		Composite c = new Composite(cc,SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;

		Label n = new Label(c, SWT.NONE);
		n.setText("User Name");
		Text t = new Text(c, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		n = new Label(c, SWT.NONE);
		n.setText("Password");
		t = new Text(c, SWT.PASSWORD | SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		n = new Label(c, SWT.NONE);
		n.setText("Host");
		t = new Text(c, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		n = new Label(c, SWT.NONE);
		n.setText("Service Name");
		t = new Text(c, SWT.BORDER);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// name

		// psd
		// host
		// test button

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
		return  new Point(600,550);
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
