package aurora.ide.prototype.consultant.product.demonstrate;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.prototype.consultant.product.Activator;
import aurora.ide.swt.util.UWizard;
import aurora.plugin.source.gen.screen.model.DemonstrateBind;
import aurora.plugin.source.gen.screen.model.DemonstrateDS;
import aurora.plugin.source.gen.screen.model.DemonstrateData;

public class DemonstrateLOVWizard extends UWizard {

	private DemonstrateDSPage dsPage;
	private DemonstrateLOVPage lovPage;
	private DemonstrateSettingManager sm;

	public DemonstrateLOVWizard(Shell shell, DemonstrateSettingManager sm) {
		super(shell);
		setNeedsProgressMonitor(true);
		this.sm = sm;
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {

		lovPage = new DemonstrateLOVPage("DemonstrateLOVPage", sm);
		dsPage = new DemonstrateDSPage(
				"FunctionDescPage", Messages.DemonstrateDSWizard_0, null, sm.getDemonstrateData()); //$NON-NLS-1$
		addPage(lovPage);
		addPage(dsPage);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					DemonstrateData data = dsPage.getData();
					String dsName = data.getDemonstrateDSName();
					if (dsName != null && "".equals(dsName) == false) { //$NON-NLS-1$
						DemonstrateDS demonstrateDS = Activator.getDefault()
								.getDemonstrateDSManager()
								.getDemonstrateDS(dsName);
						if (demonstrateDS != null) {
							demonstrateDS.setData(data.getDemonstrateData());
						} else {
							Activator
									.getDefault()
									.getDemonstrateDSManager()
									.addDemonstrateDS(
											new DemonstrateDS(data
													.getDemonstrateDSName(),
													data.getDemonstrateData()));
						}
					}
					data.setPropertyValue(DemonstrateBind.BIND_COMPONENT,
							lovPage.getBindModels());
					sm.applyDemonData(data);
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
