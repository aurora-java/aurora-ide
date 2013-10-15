package aurora.ide.prototype.consultant.product.demonstrate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.swt.util.UWizard;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;

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
				"FunctionDescPage", Messages.DemonstrateDSWizard_0, null, sm.getDemonstrateData()); //$NON-NLS-1$
		addPage(page1);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					String dsName = page1.getData().getDemonstrateDSName();
					if (dsName != null && "".equals(dsName) == false) { //$NON-NLS-1$
						DemonstrateDS demonstrateDS = Activator.getDefault()
								.getDemonstrateDSManager()
								.getDemonstrateDS(dsName);
						if (demonstrateDS != null) {
							demonstrateDS.setData(page1.getData()
									.getDemonstrateData());
						} else {
							Activator
									.getDefault()
									.getDemonstrateDSManager()
									.addDemonstrateDS(
											new DemonstrateDS(
													page1.getData()
															.getDemonstrateDSName(),
													page1.getData()
															.getDemonstrateData()));
						}
					}
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
