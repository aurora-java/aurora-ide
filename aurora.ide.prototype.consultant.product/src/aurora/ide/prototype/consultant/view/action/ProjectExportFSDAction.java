package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;

import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.wizard.ProjectExportFSDWizard;

public class ProjectExportFSDAction extends Action {
	private NavigationView nv;

	public ProjectExportFSDAction(NavigationView nv, String string) {
		super(string);
		setImageDescriptor(aurora.ide.prototype.consultant.product.Activator
				.getImageDescriptor("/icons/export_fsd.gif"));
		this.nv = nv;
	}

	public void run() {
		ProjectExportFSDWizard fw = new ProjectExportFSDWizard(nv.getSite()
				.getShell(), nv.getSelectionNode());
		fw.open();
	}
}
