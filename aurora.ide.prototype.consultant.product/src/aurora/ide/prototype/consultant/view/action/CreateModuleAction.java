package aurora.ide.prototype.consultant.view.action;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.util.ResourceUtil;
import aurora.ide.prototype.consultant.view.wizard.CreateModuleWizard;

public class CreateModuleAction extends Action implements
		ISelectionChangedListener {

	private final TreeViewer commonViewer;
	private NavigationView viewer;

	public CreateModuleAction(NavigationView viewer) {
		super(Messages.CreateModuleAction_0);
		setToolTipText(Messages.CreateModuleAction_1);
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		this.viewer = viewer;
		commonViewer = viewer.getViewer();
		commonViewer.addSelectionChangedListener(this);
		selectionChanged();
	}

	public void run() {
		Node selectionNode = viewer.getSelectionNode();
		if (selectionNode == null)
			return;
		CreateModuleWizard w = new CreateModuleWizard(commonViewer.getControl()
				.getShell(), selectionNode.getFile());
		if (WizardDialog.OK == w.open()) {
			Node newNode = new Node(new Path(w.getModule().getPath()));
			viewer.addNewNode(selectionNode, newNode);
			// new RefreshLocalFileSystemAction(viewer).run();
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selectionChanged();
	}

	protected void selectionChanged() {
		Node node = viewer.getSelectionNode();
		if (node != null) {
			if (ResourceUtil.isProject(node.getFile())
					|| ResourceUtil.isModule(node.getFile())) {
				this.setEnabled(true);
				return;
			}
		}
		this.setEnabled(false);
	}
}
