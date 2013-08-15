package aurora.ide.prototype.consultant.product.fsd.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.swt.util.UWizard;

public class ExportWizard extends UWizard {

	public ExportWizard(Shell shell) {
		super(shell);
		 setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		// mainConfigPage = new BMMainConfigPage(selection, this);
		// tablePage = new BMTablePage(selection, this);
		// fieldsPage = new BMFieldsPage(selection, this);
		// fieldsPage.setPageComplete(false);

		FunctionDescPage page1 = new FunctionDescPage("FunctionDescPage",
				"功能定义", null);
		ContentDescPage page2 = new ContentDescPage("FunctionDescPage",
				"文档内容定义", null);
		addPage(page1);
		addPage(page2);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(false, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					
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
