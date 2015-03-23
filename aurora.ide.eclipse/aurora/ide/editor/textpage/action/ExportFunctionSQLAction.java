package aurora.ide.editor.textpage.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import aurora.ide.editor.textpage.TextPage;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.PathUtil;

public class ExportFunctionSQLAction extends Action implements
		IEditorActionDelegate {

	private IEditorPart activeEditor;

	public ExportFunctionSQLAction() {
		this.setActionDefinitionId("aurora.ide.text.editor.export.function.register.sql");
	}

	public ExportFunctionSQLAction(TextPage textPage) {
		this();
		activeEditor = textPage;
	}

	public void run() {
		run(null);
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			DialogUtil.showErrorMessageBox("找不到功能文件，不能继续");
			return;
		}
		IFile file = (IFile) activeEditor.getEditorInput().getAdapter(
				IFile.class);
		if (PathUtil.isAuroraFile(file)) {

			Shell shell = activeEditor.getSite().getShell();
			FunctionRegisterWizard frw = new FunctionRegisterWizard(file);

			WizardDialog wd = new WizardDialog(shell, frw);
			if (WizardDialog.OK != wd.open()) {
			}

		} else {
			DialogUtil.showErrorMessageBox("找不到功能文件，不能继续");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

}
