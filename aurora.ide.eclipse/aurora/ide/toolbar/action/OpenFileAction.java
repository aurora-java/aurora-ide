/**
 * 
 */
package aurora.ide.toolbar.action;

/**
 * @author linjinxiao
 *
 */

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import aurora.ide.helpers.DialogUtil;


public class OpenFileAction implements IWorkbenchWindowActionDelegate {
	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		IHandlerService hs = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);
		try {
			hs.executeCommand("org.eclipse.ui.navigate.openResource", null);
		} catch (ExecutionException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (NotDefinedException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (NotEnabledException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (NotHandledException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
