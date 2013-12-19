package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;

public class CreateModuleWizard extends UWizard {

	public static final String QUICK_UI_MODULE = "quick_ui.module";
	private File parent;
	private CreateModulePage page;

	public CreateModuleWizard(Shell shell, File parent) {
		super(shell);
		this.parent = parent;
		page = new CreateModulePage("CreateModulePage", parent);
	}

	@Override
	public void addPages() {
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {

		File m = createModule();
		if (m == null) {
			page.verifyPage("新建模块失败");
			return false;
		}
		try {
			createProperties(m);
		} catch (IOException e) {
			page.verifyPage("新建属性文件失败");
			return false;
		}
		return true;
	}

	private File createModule() {
		File m = getModule();
		return ResourceUtil.createFolder(m);
	}

	public File getModule() {
		String mName = page.getModel().getStringPropertyValue(
				CreateModulePage.properties[1]);

		File m = new File(parent, mName);
		return m;
	}

	private void createProperties(File m) throws IOException {
		ResourceUtil.createFile(m, QUICK_UI_MODULE, new CompositeMap("module"));
	}

}
