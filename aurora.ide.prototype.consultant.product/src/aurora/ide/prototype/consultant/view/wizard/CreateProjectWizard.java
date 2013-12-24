package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;
import aurora.ide.swt.util.PageModel;

public class CreateProjectWizard extends UWizard {

	public static final String QUICK_UI_PROJECT = "quick_ui.project";
	private CreateProjectPage page = new CreateProjectPage("CreateProjectPage",
			new PageModel());

	public CreateProjectWizard(Shell shell) {
		super(shell);
	}

	@Override
	public void addPages() {
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		File pj = createProject();
		if (pj == null) {
			page.verifyPage("新建项目失败");
			return false;
		}
		try {
			createPJProperties(pj);
		} catch (IOException e) {
			page.verifyPage("新建属性文件失败");
			return false;
		}
		return true;
	}

	private File createProject() {
		File pj = getProject();
		return ResourceUtil.createFolder(pj);
	}

	public File getProject() {
		String pjName = page.getModel().getStringPropertyValue(
				CreateProjectPage.properties[0]);
		String pjPath = page.getModel().getStringPropertyValue(
				CreateProjectPage.properties[1]);
		File pj = new File(new File(pjPath), pjName);
		return pj;
	}

	private void createPJProperties(File pj) throws IOException {
		CompositeMap map = new CompositeMap("project");
		map.put(CreateProjectPage.properties[2], page.getModel()
				.getStringPropertyValue(CreateProjectPage.properties[2]));
		map.put(CreateProjectPage.properties[3], page.getModel()
				.getStringPropertyValue(CreateProjectPage.properties[3]));
		map.put(CreateProjectPage.properties[4], page.getModel()
				.getStringPropertyValue(CreateProjectPage.properties[4]));
		ResourceUtil.createFile(pj, QUICK_UI_PROJECT, map);
	}

}
