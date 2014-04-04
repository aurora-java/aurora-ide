package aurora.ide.prototype.consultant.view.action;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import aurora.ide.prototype.consultant.view.NavViewSetting;
import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.wizard.CreateProjectWizard;

public class CreateProjectAction extends Action implements
		ISelectionChangedListener {

	private final TreeViewer commonViewer;
	private NavigationView viewer;

	public CreateProjectAction(NavigationView viewer) {
		super(Messages.CreateProjectAction_0);
		setToolTipText(Messages.CreateProjectAction_1);
		// if (((Node) element).getFile().isDirectory()) {
		// return PlatformUI.getWorkbench().getSharedImages()
		// .getImage(ISharedImages.IMG_OBJ_FOLDER);
		// } else {
		// return AuroraImagesUtils.getImage("/meta.png");
		// }
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));

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
			Node newNode = new Node(new Path(w.getProject().getPath()));
			viewer.addNewNode(viewer.getViewer().getInput(), newNode);
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

	}

}
