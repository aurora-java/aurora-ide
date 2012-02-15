package aurora.ide.meta.gef.editors.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import aurora.ide.helpers.DialogUtil;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.search.ui.EditorOpener;

public class CreateMetaWizard extends Wizard implements INewWizard {
	private NewWizardPage newPage = new NewWizardPage();
	private SettingWizardPage settingPage = new SettingWizardPage();
	
	private IWorkbench workbench;
	private ViewDiagram viewDiagram;

	public void addPages() {
		addPage(newPage);
		addPage(settingPage);
	}

	@Override
	public boolean performFinish() {
		EditorOpener editorOpener = new EditorOpener();
		try {
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(newPage.getPath() + "/" + newPage.getFileName()));
			InputStream inputStream = new ByteArrayInputStream(ObjectToByte(viewDiagram));
			file.create(inputStream, true, null);
			editorOpener.open(workbench.getActiveWorkbenchWindow().getActivePage(), file, true);
			inputStream.close();
			return true;
		} catch (Exception e) {
			DialogUtil.showExceptionMessageBox(e);
			return false;
		}
	}

	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page instanceof NewWizardPage) {
			viewDiagram=((NewWizardPage)page).getViewDiagram();
			return true;
		}
		return false;
	}

	public boolean needsProgressMonitor() {
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	private byte[] ObjectToByte(Object obj) {
		if (obj != null) {
			byte[] bytes = null;
			try {
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream oo = new ObjectOutputStream(bo);
				oo.writeObject(obj);
				bytes = bo.toByteArray();
				bo.close();
				oo.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bytes;
		}
		return new byte[0];
	}
}
