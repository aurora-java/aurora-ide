package aurora.ide.prototype.consultant.view.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import uncertain.composite.CompositeMap;
import aurora.ide.editor.editorInput.PathEditorInput;
import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.prototype.consultant.product.ICommandIds;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.swt.util.UWizard;
import aurora.plugin.source.gen.screen.model.ScreenBody;
import aurora.plugin.source.gen.screen.model.io.Object2CompositeMap;

public class CreateUIPWizard extends UWizard {

	private CreateUIPPage page;
	private File parent;

	public CreateUIPWizard(Shell shell, File parent) {
		super(shell);
		this.parent = parent;
		page = new CreateUIPPage("CreateUIPPage", parent); //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		try {
			createFile();
			openFile();
		} catch (IOException e) {
			page.verifyPage(Messages.CreateUIPWizard_1);
			return false;
		} catch (PartInitException e) {
			page.verifyPage(Messages.CreateUIPWizard_2);
			return false;
		}

		return true;

	}

	public File getUIPFile() {
		String mName = page.getModel().getStringPropertyValue(
				CreateUIPPage.properties[0]);
		File m = new File(parent, mName+".uip"); //$NON-NLS-1$
		return m;
	}

	public void createFile() throws IOException {
		String mName = page.getModel().getStringPropertyValue(
				CreateUIPPage.properties[0]);
		Object2CompositeMap o2c = new Object2CompositeMap();
		CompositeMap map = o2c.createCompositeMap(new ScreenBody());
		ResourceUtil.createFile(parent, mName+".uip", map); //$NON-NLS-1$
	}

	public void openFile() throws PartInitException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			PathEditorInput ei = new PathEditorInput(new Path(getUIPFile()
					.getPath()));
			window.getActivePage().openEditor(ei, ICommandIds.EDITOR_ID, true);
		}
	}

}
