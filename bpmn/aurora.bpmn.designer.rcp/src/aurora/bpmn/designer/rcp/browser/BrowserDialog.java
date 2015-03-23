package aurora.bpmn.designer.rcp.browser;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import aurora.bpmn.designer.ws.ServiceModel;

public class BrowserDialog extends Dialog {

	public BrowserDialog(Shell parentShell) {
		super(parentShell);
	}

	private ServiceModel model;
	private PreviewBrowser instance;
	private String url;

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}

	public void open(String url) {
		this.url = url;
		this.open();

	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return super.createButtonBar(parent);
	}

	protected void createButtonsForButtonBar(Composite parent) {
//		"".intern()
		// create OK and Cancel buttons by default
		// createButton(parent, IDialogConstants.OK_ID,
		// IDialogConstants.OK_LABEL,
		// false);
		// createButton(parent, IDialogConstants.CANCEL_ID,
		// IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite cc = (Composite) super.createDialogArea(parent);

		instance = new PreviewBrowser(cc, true);
		instance.getBrowser().setUrl(url);
		return cc;
	}

	@Override
	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		return new Point(700, 450);
	}

}
