package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;
import aurora.ide.swt.util.PageModel;

public class CreateProjectWizard extends UWizard {

	public static final String QUICK_UI_PROJECT = "quick_ui.project"; //$NON-NLS-1$
	private CreateProjectPage page = new CreateProjectPage("CreateProjectPage", //$NON-NLS-1$
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
			page.verifyPage(Messages.CreateProjectWizard_2);
			return false;
		}
		try {
			createPJProperties(pj);
		} catch (IOException e) {
			page.verifyPage(Messages.CreateProjectWizard_3);
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
		CompositeMap map = new CompositeMap("properties"); //$NON-NLS-1$
		map.createChild(CreateProjectPage.properties[2]).setText(
				page.getModel().getStringPropertyValue(
						CreateProjectPage.properties[2]));
		map.createChild(CreateProjectPage.properties[3]).setText(
				page.getModel().getStringPropertyValue(
						CreateProjectPage.properties[3]));
		map.createChild(CreateProjectPage.properties[4]).setText(
				page.getModel().getStringPropertyValue(
						CreateProjectPage.properties[4]));
		ResourceUtil.createFile(pj, QUICK_UI_PROJECT, map);
	}

}
