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
import aurora.ide.meta.exception.ResourceNotFoundException;
import aurora.ide.meta.gef.designer.model.ModelMerger;
import aurora.ide.meta.project.AuroraMetaProject;
import aurora.ide.screen.editor.ServiceEditor;

public class OpenGeneratorFileAction implements IObjectActionDelegate {
	private IFile file = null;

	public IFile getFile() {
		return file;
	}

	private IWorkbenchPage page;

	public IWorkbenchPage getPage() {
		return page;
	}

	public void setPage(IWorkbenchPage page) {
		this.page = page;
	}

	private boolean isBm;
	private boolean isScreen;

	public boolean isBm() {
		return isBm;
	}

	public boolean isScreen() {
		return isScreen;
	}

	public OpenGeneratorFileAction() {
	}

	public void run(IAction action) {
		if (file == null) {
			DialogUtil.showErrorMessageBox("invalid selection.");
			return;
		}
		if (isBm)
			openBMFile();
		if (isScreen)
			openScreenFile();
	}

	private void openScreenFile() {
		AuroraMetaProject amp = new AuroraMetaProject(file.getProject());
		try {
			IFile newScreenFile = amp.getNewScreenFile(file);
			IDE.openEditor(getPage(), newScreenFile, ServiceEditor.ID, true);
		} catch (ResourceNotFoundException e) {
			DialogUtil.showWarningMessageBox("relative file does not exists.");
		} catch (PartInitException e) {
			DialogUtil
					.showErrorMessageBox("Error Occurred while open eidtor.\n"
							+ e.getMessage());
		}
	}

	public void openBMFile() {
		ModelMerger merger = new ModelMerger(file);
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
		file = null;
		isBm = false;
		isScreen = false;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object o = ss.getFirstElement();
			if (o instanceof IFile) {
				file = (IFile) o;
				if ("bmq".equalsIgnoreCase(file.getFileExtension())) {
					isBm = true;
				}
				if ("uip".equalsIgnoreCase(file.getFileExtension())) {
					isScreen = true;
				}
			}
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		page = targetPart.getSite().getPage();
	}

}
