package aurora.ide.prototype.consultant.product.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.prototype.consultant.product.ICommandIds;

public class NewFileAction extends Action {

	private final IWorkbenchWindow window;

	public NewFileAction(IWorkbenchWindow window, String label) {
		this.window = window;
		setText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_OPEN);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/sample2.gif"));
		this.setToolTipText(label);
	}

	public void run() {
		if (window != null) {
			try {
				PathEditorInput ei = new PathEditorInput(
						PathEditorInput.UNTITLED_PATH);
				window.getActivePage().openEditor(ei, ICommandIds.EDITOR_ID,
						true);
			} catch (PartInitException e) {
				MessageDialog.openError(window.getShell(), "Error",
						"Error Create File:" + e.getMessage());
			}
		}
	}
}
