package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;

public class ProjectExportFSDWizard extends UWizard {

	private ProjectFSDDescPage page1;
	private ProjectFSDContentPage page2;
	private Node projectNode;
	private CompositeMap loadProperties;

	public ProjectExportFSDWizard(Shell shell, Node selectionNode) {
		super(shell);
		setNeedsProgressMonitor(true);
		this.projectNode = ResourceUtil.getProjectNode(selectionNode);
		loadProperties = loadProperties(selectionNode.getFile());
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		page1 = new ProjectFSDDescPage(
				"ProjectFSDDescPage", Messages.ExportWizard_1, null, loadProperties); //$NON-NLS-1$
		page2 = new ProjectFSDContentPage(
				"ProjectFSDContentPage", Messages.ExportWizard_3, null, projectNode, loadProperties); //$NON-NLS-1$
		addPage(page1);
		addPage(page2);
	}

	protected CompositeMap loadProperties(File file) {
		if (ResourceUtil.isProject(file)) {
			return ResourceUtil.loadProjectProperties(file);
		}
		if (ResourceUtil.isModule(file)) {
			CompositeMap pp = ResourceUtil.loadModuleProperties(file);
			ResourceUtil.copyProjectProperties(file, pp);
			return pp;
		}
		return new CompositeMap();
	}

	@Override
	public boolean performFinish() {
//		try {
//			this.getContainer().run(
//					false,
//					false,
//					// new ExportFSDProgress(page2.getSavePath(),
//					// page1.getModel(), page2.getUipFiles())
//					new ExportFSDProgress(page2.getSavePath(),
//							page1.getModel(), page2.getUipFiles(), page2
//									.isOnlySaveLogic()));
//			return true;
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return false;
	}

}
