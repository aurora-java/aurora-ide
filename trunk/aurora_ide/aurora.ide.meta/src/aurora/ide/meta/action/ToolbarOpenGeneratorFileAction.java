package aurora.ide.meta.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

import aurora.ide.meta.gef.designer.OpenBMAction;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.popup.actions.OpenGeneratorFileAction;

public class ToolbarOpenGeneratorFileAction implements
		IWorkbenchWindowPulldownDelegate2 {

	private Shell shell;
	private IWorkbenchWindow window;
	private OpenGeneratorFileAction ogfa;

	public void run(IAction action) {
		// if (ogfa.isBm() || ogfa.isScreen())
		ogfa.run(action);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		boolean empty = selection.isEmpty();
		if (empty) {
			initWithActiveEditor(action, selection);
			return;
		}
		if (selection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof ComponentPart) {
				initWithActiveEditor(action, selection);
				return;
			}
		}
		ogfa.selectionChanged(action, selection);
	}

	public void initWithActiveEditor(IAction action, ISelection selection) {
		IEditorPart activeEditor = window.getActivePage().getActiveEditor();
		if (activeEditor != null) {
			IFile file = (IFile) activeEditor.getAdapter(IFile.class);
			if (file != null) {
				StructuredSelection structuredSelection = new StructuredSelection(
						file);
				ogfa.selectionChanged(action, structuredSelection);
			} else {
				ogfa.selectionChanged(action, selection);
			}
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.shell = window.getShell();
		ogfa = new OpenGeneratorFileAction();
		ogfa.setPage(window.getActivePage());
	}

	public Menu getMenu(Control parent) {
		if (ogfa.isBm()) {
			OpenBMAction openBMAction = new OpenBMAction(ogfa.getFile(),
					ogfa.getPage());
			return openBMAction.getMenuCreator().getMenu(parent);
		}
		return null;
	}

	public Menu getMenu(Menu parent) {
		if (ogfa.isBm()) {
			OpenBMAction openBMAction = new OpenBMAction(ogfa.getFile(),
					ogfa.getPage());
			return openBMAction.getMenuCreator().getMenu(parent);
		}
		return null;
	}

}
