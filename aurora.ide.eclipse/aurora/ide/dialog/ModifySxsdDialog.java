package aurora.ide.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ModifySxsdDialog extends Dialog {

	private Text txtNamespace;
	private Text txtTags;

	public ModifySxsdDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 500);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());
		// Add the buttons to the button bar.
		createButtonsForButtonBar(composite);
		return composite;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("Modify Tags");
		GridLayout layou = new GridLayout();
		container.setLayout(layou);
		
		GridData gdTag = new GridData(GridData.FILL_BOTH);
		txtTags = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		txtTags.setLayoutData(gdTag);

		return container;

	}
}
