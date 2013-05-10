package aurora.ide.prototype.consultant.product;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import aurora.ide.editor.editorInput.StringEditorInput;

public class OpenViewAction extends Action {

	private final IWorkbenchWindow window;
	private int instanceNum = 0;
	private final String viewId;

	public OpenViewAction(IWorkbenchWindow window, String label, String viewId) {
		this.window = window;
		this.viewId = viewId;
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
				window.getActivePage().openEditor(
						(IEditorInput) new StringEditorInput("", "utf-8"),
						"aurora.ide.meta.gef.editors.VScreenEditor", true);
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
