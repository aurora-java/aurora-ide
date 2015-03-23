package aurora.ide.navigator.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.wizard.AuroraProjectSettingWizard;

public class AddAuroraNature implements IObjectActionDelegate {

	private ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		if (check()) {
			action.setEnabled(true);
			IStructuredSelection structured = (IStructuredSelection) selection;
			Object firstElment = structured.getFirstElement();
			if (firstElment instanceof IProject) {
				IProject project = (IProject) firstElment;
				AuroraProjectSettingWizard wizard = new AuroraProjectSettingWizard(
						Display.getDefault().getActiveShell(), project);
				wizard.open();
			}
		} else {
			action.setEnabled(false);
		}
	}

	private boolean check() {
		if (!(selection instanceof IStructuredSelection)) {
			return false;
		}
		IStructuredSelection structured = (IStructuredSelection) selection;
		Object firstElment = structured.getFirstElement();
		if (!(firstElment instanceof IProject)) {
			return false;
		}
		IProject project = (IProject) firstElment;
		if (!project.isOpen()) {
			return false;
		}
		try {
			if (AuroraProjectNature.hasAuroraNature(project)) {
				return false;
			}
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}
}
