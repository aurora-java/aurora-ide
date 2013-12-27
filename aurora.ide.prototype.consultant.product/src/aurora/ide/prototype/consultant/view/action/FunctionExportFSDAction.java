package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;

import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.wizard.FunctionExportFSDWizard;

public class FunctionExportFSDAction extends Action {

	private NavigationView nv;

	public FunctionExportFSDAction(NavigationView navigationView, String string) {
		super(string);
		this.nv = navigationView;
	}

	public void run() {
		FunctionExportFSDWizard fw = new FunctionExportFSDWizard(nv.getSite()
				.getShell(), nv.getSelectionNode());
		fw.open();
	}
}
