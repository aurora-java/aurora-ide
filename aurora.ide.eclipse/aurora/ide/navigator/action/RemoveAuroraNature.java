package aurora.ide.navigator.action;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.DialogUtil;


public class RemoveAuroraNature implements IObjectActionDelegate {

	ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	public void run(IAction action) {
		if (!(selection instanceof IStructuredSelection)){
			DialogUtil.showErrorMessageBox(selection+"is not a IStructuredSelection!");
			return;
		}
		IStructuredSelection structured = (IStructuredSelection) selection;
		Object firstElment = structured.getFirstElement();
		if (!(firstElment instanceof IProject)){
			DialogUtil.showErrorMessageBox(firstElment+"is not a IProject!");
			return;
		}
		IProject project = (IProject) firstElment;
		if(!project.isOpen()){
			return;
		}
		try {
			if(AuroraProjectNature.hasAuroraNature(project)){
				AuroraProjectNature.removeAuroraNature(project);
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}
}
