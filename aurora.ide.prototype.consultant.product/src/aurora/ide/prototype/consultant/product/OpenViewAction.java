package aurora.ide.prototype.consultant.product;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.meta.gef.editors.ConsultantVScreenEditor;

public class OpenViewAction extends Action {

	
	private final IWorkbenchWindow window;

	public OpenViewAction(IWorkbenchWindow window, String label, String viewId) {
		this.window = window;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/sample2.gif"));
	}

	public void run() {
		if (window != null) {
			try {
				PathEditorInput ei = new PathEditorInput(ConsultantVScreenEditor.PATH);
				window.getActivePage().openEditor(ei,
						"aurora.ide.meta.gef.editors.ConsultantVScreenEditor", true);
				// window.getActivePage().showView(viewId,
				// Integer.toString(instanceNum++),
				// IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error",
						"Error opening view:" + e.getMessage());
			}
		}
	}
}
