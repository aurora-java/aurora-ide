package aurora.ide.meta.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import aurora.ide.meta.action.gen.SourceCodeGeneratorWizard;

public class SourceCodeGenerator implements IWorkbenchWindowActionDelegate {

	private Shell shell;

	public void run(IAction action) {
		System.out.println("生成代码啦");
		SourceCodeGeneratorWizard wizard = new SourceCodeGeneratorWizard();
		WizardDialog wd = new WizardDialog(shell, wizard);
		wd.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.shell = window.getShell();
	}

}
