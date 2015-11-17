package aurora.ide.navigator.action;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.editor.textpage.action.AllFunctionRegisterWizard;

public class CreateAllFunctionRegister implements IObjectActionDelegate {

	private ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {

		IStructuredSelection selection =  (IStructuredSelection)this.selection;
		
		Object firstElement = selection.getFirstElement();
		
		AllFunctionRegisterWizard wizard = new AllFunctionRegisterWizard((IContainer) firstElement);
	
		WizardDialog wd = new WizardDialog(new Shell(), wizard);
		wd.open();
//		if (WizardDialog.OK != wd.open()) {
//			wd.open();
//		}
	
	}


	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}
}
