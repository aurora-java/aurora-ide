package aurora.ide.search.action;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;

import aurora.ide.search.core.Util;
import aurora.ide.search.reference.FileReferenceQuery;

public class FileReferenceAciton implements IObjectActionDelegate {
	private IFile sourceFile;

	public FileReferenceAciton() {
	}

	public void run(IAction action) {
		IProject project = sourceFile.getProject();

		IContainer scope = Util.findWebInf(sourceFile);
		if (scope == null) {
			scope = project;
		} else {
			scope = scope.getParent();
		}
		FileReferenceQuery query = new FileReferenceQuery(scope, sourceFile);
		NewSearchUI.runQueryInBackground(query);
	}

	public void selectionChanged(IAction action, ISelection selection) {

		boolean isEnable = checkSelection(selection);
		action.setEnabled(isEnable);

	}

	private boolean checkSelection(ISelection selection) {
		if (selection == null) {
			return false;
		}
		List resources = IDE
				.computeSelectedResources((IStructuredSelection) selection);
		if (resources.size() != 1) {
			return false;
		}
		Object resource = resources.get(0);
		if (!(resource instanceof IFile)) {
			return false;
		}
		sourceFile = (IFile) resource;
		return true;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
