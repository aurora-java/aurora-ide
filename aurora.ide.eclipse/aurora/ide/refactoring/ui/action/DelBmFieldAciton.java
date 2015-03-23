package aurora.ide.refactoring.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.refactor.screen.ScreenCustomerRefactoring;
import aurora.ide.refactoring.ui.AuroraRefactoringWizard;

public class DelBmFieldAciton extends Action implements IObjectActionDelegate {

	private IWorkbenchPart targetPart;
	private ISelection selection;

	public DelBmFieldAciton() {
		this.setText("重构：field删除");
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
		if (targetPart == null) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}

	public void run(IAction action) {
		// Shell shell = targetPart.getSite().getShell();
		// ScreenCustomerRefactoring refactor = new ScreenCustomerRefactoring(
		// (IStructuredSelection) selection);
		// AuroraRefactoringWizard wizard = new
		// AuroraRefactoringWizard(refactor);
		// RefactoringWizardOpenOperation op = new
		// RefactoringWizardOpenOperation(
		// wizard);
		// try {
		// op.run(shell, "Screen Custom");
		// } catch (InterruptedException e) {
		// }
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		if (selection instanceof IStructuredSelection) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}

}
