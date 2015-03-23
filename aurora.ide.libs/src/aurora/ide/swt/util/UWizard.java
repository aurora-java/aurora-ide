package aurora.ide.swt.util;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

abstract public class UWizard extends Wizard {

	private Shell shell;

	public UWizard(Shell shell) {
		this.shell = shell;
	}

	public abstract void addPages();

	public int open() {
		WizardDialog wd = new WizardDialog(shell, this);
		wd.setHelpAvailable(false);
		return wd.open();
	}

}
