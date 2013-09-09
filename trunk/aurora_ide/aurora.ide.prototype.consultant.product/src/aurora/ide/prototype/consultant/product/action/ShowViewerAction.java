package aurora.ide.prototype.consultant.product.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class ShowViewerAction extends Action implements IWorkbenchWindowActionDelegate {
	
	
	private IWorkbenchWindow fWindow;
	private String viewId;


	public ShowViewerAction(IWorkbenchWindow window, String label,ImageDescriptor newImage,String viewId) {
		
		this.viewId = viewId;
		this.fWindow = window;
		setText(label);
		this.setId("aurora.ide.prototype.consultant.product.action."+viewId);
		setImageDescriptor(newImage);
		this.setToolTipText(label);
	}


	public void dispose() {
		fWindow= null;
	}

	public void init(IWorkbenchWindow window) {
		fWindow= window;
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}


	@Override
    public void run() {
		try {
			fWindow.getActivePage().showView(viewId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}