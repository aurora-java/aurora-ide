package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;

public class FunctionExportFSDWizard extends UWizard {

	private FunctionFSDDescPage page1;
	private FunctionFSDContentPage page2;
	private Node projectNode;
	private CompositeMap loadProperties;

	public FunctionExportFSDWizard(Shell shell, Node selectionNode) {
		super(shell);
		setNeedsProgressMonitor(true);
		this.projectNode = ResourceUtil.getProjectNode(selectionNode);
		loadProperties = loadProperties(selectionNode.getFile());
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		page1 = new FunctionFSDDescPage(
				"ProjectFSDDescPage", Messages.ExportWizard_1, null, loadProperties); //$NON-NLS-1$
		page2 = new FunctionFSDContentPage(
				"ProjectFSDContentPage", Messages.ExportWizard_3, null, projectNode, loadProperties); //$NON-NLS-1$
		addPage(page1);
		addPage(page2);
	}

	protected CompositeMap loadProperties(File file) {
		CompositeMap pp = ResourceUtil.loadFunctionProperties(file);
		ResourceUtil.copyProjectProperties(file, pp);
		return pp;
	}

	public boolean performFinish() {
		// try {
		// this.getContainer().run(
		// false,
		// false,
		// // new ExportFSDProgress(page2.getSavePath(),
		// // page1.getModel(), page2.getUipFiles())
		// new ExportFSDProgress(page2.getSavePath(),
		// page1.getModel(), page2.getUipFiles(), page2
		// .isOnlySaveLogic()));
		// return true;
		// } catch (InvocationTargetException e) {
		// e.printStackTrace();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		return false;
	}

}
