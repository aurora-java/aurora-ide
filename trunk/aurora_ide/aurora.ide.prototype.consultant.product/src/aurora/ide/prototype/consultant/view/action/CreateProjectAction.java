package aurora.ide.prototype.consultant.view.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;

import aurora.ide.prototype.consultant.view.NavViewSetting;
import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.wizard.CreateProjectWizard;

public class CreateProjectAction extends Action implements
		ISelectionChangedListener {

	private final TreeViewer commonViewer;
	private NavigationView viewer;

	public CreateProjectAction(NavigationView viewer) {
		super("新建项目");
		setToolTipText("新建项目");
		this.viewer = viewer;
		commonViewer = viewer.getViewer();
		commonViewer.addSelectionChangedListener(this);
	}

	public void run() {
		CreateProjectWizard w = new CreateProjectWizard(commonViewer
				.getControl().getShell());
		if (WizardDialog.OK == w.open()) {
			NavViewSetting vs = new NavViewSetting();
			vs.addFolder(w.getProject());
			new RefreshLocalFileSystemAction(viewer).run();
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

	}

}
