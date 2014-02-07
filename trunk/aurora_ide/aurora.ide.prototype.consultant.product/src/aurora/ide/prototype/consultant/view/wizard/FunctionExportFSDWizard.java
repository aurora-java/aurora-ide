package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.product.fsd.ExportFSDProgress;
import aurora.ide.prototype.consultant.product.fsd.FunctionDesc;
import aurora.ide.prototype.consultant.product.fsd.wizard.FSDContentControl;
import aurora.ide.prototype.consultant.product.fsd.wizard.Messages;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.PageModel;
import aurora.ide.swt.util.UWizard;

public class FunctionExportFSDWizard extends UWizard {

	private FunctionFSDDescPage page1;
	private FunctionFSDContentPage page2;
	private Node projectNode;
	private CompositeMap loadProperties;
	private Node selectionNode;

	public FunctionExportFSDWizard(Shell shell, Node selectionNode) {
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

		page1 = new FunctionFSDDescPage(
				"ProjectFSDDescPage", Messages.ExportWizard_1, null, loadProperties); //$NON-NLS-1$
		page2 = new FunctionFSDContentPage(
				"ProjectFSDContentPage", Messages.ExportWizard_3, null,selectionNode, projectNode, loadProperties); //$NON-NLS-1$
		addPage(page1);
		addPage(page2);
	}

	protected CompositeMap loadProperties(File file) {
		CompositeMap pp = ResourceUtil.loadFunctionProperties(file);
		ResourceUtil.copyProjectProperties(file, pp);
		return pp;
	}

	protected void saveProperties(CompositeMap map) throws IOException {
		File file = selectionNode.getFile();
		ResourceUtil.createFile(file, CreateFunctionWizard.QUICK_UI_FUNCTION,
				map);
	}
	@SuppressWarnings("unchecked")
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
							new ExportFSDProgress(
									p2m.getStringPropertyValue(FSDContentControl.FSD_DOCX_PATH),
									FunctionDesc.create(p1m),
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
