package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.ExportProjectFSDProgress;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.UWizard;

public class ProjectExportFSDWizard extends UWizard {

	private ProjectFSDDescPage page1;
	private ProjectFSDContentPage page2;
	private Node projectNode;
	private CompositeMap loadProperties;
	private Node selectionNode;

	public ProjectExportFSDWizard(Shell shell, Node selectionNode) {
		super(shell);
		setNeedsProgressMonitor(true);
		this.projectNode = ResourceUtil.getProjectNode(selectionNode);
		loadProperties = loadProperties(selectionNode.getFile());
		this.selectionNode = selectionNode;
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		page1 = new ProjectFSDDescPage(
				"ProjectFSDDescPage", Messages.ExportWizard_1, null, loadProperties); //$NON-NLS-1$
		page2 = new ProjectFSDContentPage(
				"ProjectFSDContentPage", Messages.ExportWizard_3, null,selectionNode, projectNode, loadProperties); //$NON-NLS-1$
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

	protected void saveProperties(CompositeMap map) throws IOException {
		File file = selectionNode.getFile();
		if (ResourceUtil.isProject(file)) {
			ResourceUtil.createFile(file, CreateProjectWizard.QUICK_UI_PROJECT,
					map);
		}
		if (ResourceUtil.isModule(file)) {
			ResourceUtil.createFile(file, CreateModuleWizard.QUICK_UI_MODULE,
					map);
		}
	}

	@Override
	public boolean performFinish() {

		try {
			PageModel p2m = page2.getModel();
			PageModel p1m = page1.getModel();

			CompositeMap map = new CompositeMap("properties");
			page1.saveTOMap(map);
			page2.saveTOMap(map);
			try {
				saveProperties(map);
			} catch (IOException e) {
				return false;
			}
			
			this.getContainer()
					.run(false,
							false,
							new ExportProjectFSDProgress(
									p2m.getStringPropertyValue(FSDContentControl.FSD_DOCX_PATH),
									p1m,
									((List<String>) p2m
											.getPropertyValue(FSDContentControl.FSD_TABLE_INPUT)),
									p2m.getBooleanPropertyValue(FSDContentControl.ONLY_SAVE_LOGIC)));
			return true;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;

	}

}
