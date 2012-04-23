package aurora.ide.meta.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.designer.model.ModelMerger;

public class OpenBMofBmqAction implements IObjectActionDelegate {
	private IFile bmqFile = null;
	private IWorkbenchPage page;

	public OpenBMofBmqAction() {
	}

	public void run(IAction action) {
		if (bmqFile == null) {
			DialogUtil.showErrorMessageBox("invalid selection.");
			return;
		}
		ModelMerger merger = new ModelMerger(bmqFile);
		IFile bmFile = merger.getBMFile();
		if (bmFile == null || !bmFile.exists()) {
			DialogUtil.showWarningMessageBox("relative file does not exists.");
			return;
		}
		try {
			IDE.openEditor(page, bmFile, "aurora.ide.BusinessModelEditor", true);
		} catch (PartInitException e) {
			DialogUtil
					.showErrorMessageBox("Error Occurred while open eidtor.\n"
							+ e.getMessage());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		bmqFile = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object o = ss.getFirstElement();
			if (o instanceof IFile) {
				bmqFile = (IFile) o;
			}
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		page = targetPart.getSite().getPage();
	}

}
