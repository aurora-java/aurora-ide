package aurora.ide.prototype.consultant.product.demonstrate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.swt.util.UWizard;

public class DemonstrateOpeningWizard extends UWizard {

	private DemonstrateOpeningPage page1;
	private DemonstrateSettingManager sm;

	public DemonstrateOpeningWizard(Shell shell, DemonstrateSettingManager sm) {
		super(shell);
		this.sm = sm;
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		page1 = new DemonstrateOpeningPage(
				"DemonstrateOpeningPage", Messages.DemonstrateOpeningWizard_0, null, sm.getDemonstrateData()); //$NON-NLS-1$
		addPage(page1);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(
					false,
					false,
					new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							sm.applyDemonData(page1.getData());
						}
					});
			return true;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

}
