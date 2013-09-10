package aurora.ide.prototype.consultant.product.fsd.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Shell;

import aurora.ide.prototype.consultant.product.fsd.ExportFSDProgress;
import aurora.ide.swt.util.UWizard;

public class ExportWizard extends UWizard {

	private FunctionDescPage page1;
	private ContentDescPage page2;

	public ExportWizard(Shell shell) {
		super(shell);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		page1 = new FunctionDescPage(
				"FunctionDescPage", Messages.ExportWizard_1, null); //$NON-NLS-1$
		page2 = new ContentDescPage(
				"FunctionDescPage", Messages.ExportWizard_3, null); //$NON-NLS-1$
		addPage(page1);
		addPage(page2);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(
					false,
					false,
					// new ExportFSDProgress(page2.getSavePath(),
					// page1.getModel(), page2.getUipFiles())
					new ExportFSDProgress(page2.getSavePath(),
							page1.getModel(), page2.getUipFiles(), page2
									.isOnlySaveLogic()));
			return true;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

}
