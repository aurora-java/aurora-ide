package aurora.ide.meta.gef.editors.wizard.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.meta.gef.editors.template.Template;

public class StyleSettingDialog extends Dialog {

	private Template template;

	public StyleSettingDialog(Shell parentShell, Template template) {
		super(parentShell);
		// TODO Auto-generated constructor stub
		this.template = template;
	}

}
