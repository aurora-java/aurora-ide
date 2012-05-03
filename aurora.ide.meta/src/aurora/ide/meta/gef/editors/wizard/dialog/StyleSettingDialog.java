package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.models.link.Parameter;
import aurora.ide.meta.gef.i18n.Messages;

public class StyleSettingDialog extends Dialog {

	private List<Parameter> input = new ArrayList<Parameter>();

	public StyleSettingDialog(Shell parentShell) {
		super(parentShell);
	}

	public StyleSettingDialog(Shell parentShell, List<Parameter> input) {
		super(parentShell);
		this.input = input;
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX | SWT.HELP;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	public List<Parameter> getResult() {
		return input;
	}

	public List<Parameter> getInput() {
		return input;
	}

	public void setInput(List<Parameter> input) {
		this.input = input;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText(Messages.StyleSettingDialog_ParSetting);
		PComposite pComposite = new PComposite(container, input, SWT.None);
		pComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return container;
	}

}
