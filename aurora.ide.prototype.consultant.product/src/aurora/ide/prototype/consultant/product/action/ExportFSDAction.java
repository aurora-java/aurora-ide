package aurora.ide.prototype.consultant.product.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import aurora.ide.prototype.consultant.product.fsd.wizard.ExportWizard;

public class ExportFSDAction extends Action implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow;

	public ExportFSDAction() {
		setEnabled(true);
	}

	public ExportFSDAction(IWorkbenchWindow window, String label) {
		this.fWindow = window;
		this.setId("aurora.ide.prototype.consultant.product.action.ExportFSDAction");
		setText(label);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/export_fsd.gif"));
		this.setToolTipText(label);
	}

	public void dispose() {
		fWindow = null;
	}

	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	@Override
	public void run() {

		ExportWizard wizard = new ExportWizard(fWindow.getShell());
		int open = wizard.open();
		if (WizardDialog.OK == open) {

		}
		// WizardDialog wd = new WizardDialog(fWindow.getShell(),wizard);
		// File file= queryFile();
		// if (file != null) {
		// IEditorInput input= createEditorInput(file);
		// String editorId= getEditorId(file);
		// IWorkbenchPage page= fWindow.getActivePage();
		// try {
		// page.openEditor(input, editorId);
		// } catch (PartInitException e) {
		// e.printStackTrace();
		// }
		// } else {
		////			MessageDialog.openWarning(fWindow.getShell(), "Problem", "File is 'null'"); //$NON-NLS-1$
		// }
	}


}