package aurora.ide.prototype.consultant.product.action;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.DefaultWorkbenchBrowserSupport;

public class UpdateQuickUIAction extends Action implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow;

	public UpdateQuickUIAction(IWorkbenchWindow window, String label,
			ImageDescriptor newImage) {

		this.fWindow = window;
		setText(label);
		this.setId("aurora.ide.prototype.consultant.product.action.UpdateQuickUIAction");
		setImageDescriptor(newImage);
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

		String os = "win";
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") != -1)
			os = "win";
		else if (osName.indexOf("Mac") != -1)
			os = "mac";

		DefaultWorkbenchBrowserSupport dbs = new DefaultWorkbenchBrowserSupport();
		try {
			dbs.createBrowser(
					"aurora.ide.prototype.consultant.product.action.UpdateQuickUIAction")
					.openURL(
							new URL(
									"http://aurora.hand-china.com/download/aurora_"
											+ os + ".zip"));
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}