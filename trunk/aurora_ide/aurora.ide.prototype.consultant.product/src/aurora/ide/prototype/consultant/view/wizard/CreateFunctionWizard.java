package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;

import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;

public class CreateFunctionWizard extends UWizard {

	public static final String QUICK_UI_FUNCTION = "quick_ui.function";
	private CreateFunctionPage page;
	private File parent;

	public CreateFunctionWizard(Shell shell, File parent) {
		super(shell);
		this.parent = parent;
		page = new CreateFunctionPage("CreateFunctionPage", parent);
	}

	@Override
	public void addPages() {
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {

		File m = createFunction();
		if (m == null) {
			page.verifyPage("新建功能失败");
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

	private File createFunction() {
		File m = getFunction();
		return ResourceUtil.createFolder(m);
	}

	public File getFunction() {
		String mName = page.getModel().getStringPropertyValue(
				CreateFunctionPage.properties[0]);
		File m = new File(parent, mName);
		return m;
	}

	private void createProperties(File m) throws IOException {
		CompositeMap map = new CompositeMap("properties");
		map.createChild(CreateFunctionPage.properties[0]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[0]));
		map.createChild(CreateFunctionPage.properties[1]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[1]));
		map.createChild(CreateFunctionPage.properties[2]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[2]));
		map.createChild(CreateFunctionPage.properties[3]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[3]));
		map.createChild(CreateFunctionPage.properties[4]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[4]));
		map.createChild(CreateFunctionPage.properties[5]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[5]));
		map.createChild(CreateFunctionPage.properties[6]).setText(
				page.getModel().getStringPropertyValue(
						CreateFunctionPage.properties[6]));

		ResourceUtil.createFile(m, QUICK_UI_FUNCTION, map);
	}
}
