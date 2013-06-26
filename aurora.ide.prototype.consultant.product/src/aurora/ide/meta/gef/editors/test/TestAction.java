package aurora.ide.meta.gef.editors.test;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class TestAction extends Action implements
		IWorkbenchWindowActionDelegate {

	public static RGB toRGB(String color) {
		String[] split = color.split(",");
		return new RGB(Integer.valueOf(split[0]), Integer.valueOf(split[1]),
				Integer.valueOf(split[2]));
	}
	

	public static String toString(RGB color) {
		return color.red + "," + color.green + "," + color.blue;
	}

	private IWorkbenchWindow fWindow;

	public TestAction() {
		setEnabled(true);
	}

	public TestAction(IWorkbenchWindow window, String label) {
		this.fWindow = window;
		setText(label);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/open.gif"));
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

	@Override
	public void run() {
//		TextEditDialog ted = new TextEditDialog(fWindow.getShell());
//		ted.setStyledStringText(sst);
//		ted.open();
//		sst = ted.getStyledStringText();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}