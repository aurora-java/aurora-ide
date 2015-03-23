package aurora.ide.navigator.action;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.pkg.wizard.CreateAuroraPkgWizard;

public class CreateAuroraPkgAction implements IObjectActionDelegate {

	ISelection selection;
	IWorkbenchPart part;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
	}

	public void run(IAction action) {
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection structured = (IStructuredSelection) selection;
		if (!(structured.getFirstElement() instanceof IFolder))
			return;
		IFolder folder = (IFolder) structured.getFirstElement();
		CreateAuroraPkgWizard wizard = new CreateAuroraPkgWizard(folder);
		WizardDialog wd = new WizardDialog(part.getSite().getShell(), wizard);
		wd.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
}
