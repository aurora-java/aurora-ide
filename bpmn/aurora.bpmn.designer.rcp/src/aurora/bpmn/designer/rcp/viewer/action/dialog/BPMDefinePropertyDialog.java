package aurora.bpmn.designer.rcp.viewer.action.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aurora.bpmn.designer.ws.BPMNDefineModel;
import aurora.ide.swt.util.WidgetFactory;

public class BPMDefinePropertyDialog extends Dialog {

	private BPMNDefineModel model;

	public BPMDefinePropertyDialog(Shell parentShell, BPMNDefineModel model) {
		super(parentShell);
		this.model = model;
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
		Composite cc = (Composite) super.createDialogArea(parent);

		Composite c2 = new Composite(cc, SWT.NONE);
		c2.setLayout(new GridLayout());
		c2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l = new Label(c2, SWT.NONE);
		l.setText("BPM Model");
		l.setFont(JFaceResources.getBannerFont());
		WidgetFactory.hSeparator(c2);

		Composite c = new Composite(cc, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout ly = (GridLayout) c.getLayout();
		ly.numColumns = 2;

		createProperty(c, "BPM Name", model.getName());
		createProperty(c, "BPM Code", model.getProcess_code());
		createProperty(c, "BPM Version", model.getProcess_version());
		createProperty(c, "Approve Flag", model.getApprove_flag());
		createProperty(c, "Enable", model.getEnable());
		createProperty(c, "Current Version Flag",
				model.getCurrent_version_flag());
		createProperty(c, "Description", model.getDescription());

		return c;
	}

	private void createProperty(Composite c, String text, String value) {
		Label n = new Label(c, SWT.NONE);
		n.setText(text);
		Text t = new Text(c, SWT.READ_ONLY | SWT.WRAP);
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		t.setBackground(t.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
		t.setText(value);

		// rrorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// // errorMessageText.setLayoutData(new
		// GridData(GridData.GRAB_HORIZONTAL
		// // ));
		// errorMessageText.setBackground(errorMessageText.getDisplay()
		// .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		// errorMessageText.setForeground(errorMessageText.getDisplay()
		// .getSystemColor(SWT.COLOR_RED));
		// setErrorMessage(null);

	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(600, 550);
	}

}
