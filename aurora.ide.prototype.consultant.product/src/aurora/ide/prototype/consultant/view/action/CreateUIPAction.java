package aurora.ide.prototype.consultant.view.action;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;

import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.wizard.CreateUIPWizard;

public class CreateUIPAction extends Action implements
		ISelectionChangedListener {

	private final TreeViewer commonViewer;
	private NavigationView viewer;

	public CreateUIPAction(NavigationView viewer) {
		super("新建UIP");
		setToolTipText("新建UIP");
		this.viewer = viewer;
		commonViewer = viewer.getViewer();
		commonViewer.addSelectionChangedListener(this);
		selectionChanged();
	}

	public void run() {
		Node selectionNode = viewer.getSelectionNode();
		if (selectionNode == null)
			return;
		CreateUIPWizard w = new CreateUIPWizard(commonViewer
				.getControl().getShell(), selectionNode.getFile());
		if (WizardDialog.OK == w.open()) {
			Node newNode = new Node(new Path(w.getUIPFile().getPath()));
			viewer.addNewNode(selectionNode, newNode);
//			new RefreshLocalFileSystemAction(viewer).run();
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selectionChanged();
	}

	protected void selectionChanged() {
		Node node = viewer.getSelectionNode();
		if (node != null && node.getFile().isDirectory()) {
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
	}

}
