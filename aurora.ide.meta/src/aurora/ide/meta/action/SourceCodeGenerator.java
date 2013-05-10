package aurora.ide.meta.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

import aurora.ide.meta.action.gen.SourceCodeGeneratorWizard;

public class SourceCodeGenerator implements IWorkbenchWindowPulldownDelegate2 {

	private Shell shell;
	private IWorkbenchWindow window;
	public SourceCodeGenerator(){
	}

	public void run(IAction action) {
		SourceCodeGeneratorWizard wizard = new SourceCodeGeneratorWizard(window);
		WizardDialog wd = new WizardDialog(shell, wizard);
		wd.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.shell = window.getShell();
	}

	public Menu getMenu(Control parent) {
		return null;
	}

	public Menu getMenu(Menu parent) {
		return null;
	}

	
}
