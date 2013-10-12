package aurora.ide.prototype.consultant.product.demonstrate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.swt.util.UWizard;

public class DemonstrateDSWizard extends UWizard {

	private DemonstrateDSPage page1;
	private DemonstrateSettingManager sm;

	public DemonstrateDSWizard(Shell shell, DemonstrateSettingManager sm) {
		super(shell);
		setNeedsProgressMonitor(true);
		this.sm = sm;
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		page1 = new DemonstrateDSPage(
				"FunctionDescPage", "演示", null, sm.getDemonstrateData()); //$NON-NLS-1$
		addPage(page1);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					// page1.getData();
					// ds is modify??
					// alert save ds,是否新增数据源，是否修改数据源
					// if ==cancel throw InterruptedException
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
