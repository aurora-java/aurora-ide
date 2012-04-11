package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class StyleSettingDialog extends Dialog {

	public StyleSettingDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText("参数设置");
		
		return container;
	}
}
