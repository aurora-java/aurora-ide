package aurora.ide.prototype.consultant.view.action;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.prototype.consultant.view.NavigationView;
import aurora.ide.prototype.consultant.view.Node;
import aurora.ide.prototype.consultant.view.wizard.CreateUIPWizard;

public class CreateUIPAction extends Action implements
		ISelectionChangedListener {

	private final TreeViewer commonViewer;
	private NavigationView viewer;

	public CreateUIPAction(NavigationView viewer) {
		super(Messages.CreateUIPAction_0);
		setToolTipText(Messages.CreateUIPAction_1);

		this.setImageDescriptor(AuroraImagesUtils
				.getImageDescriptor("/meta.png"));

		this.viewer = viewer;
		commonViewer = viewer.getViewer();
		commonViewer.addSelectionChangedListener(this);
		selectionChanged();
	}

	public void run() {
		Node selectionNode = viewer.getSelectionNode();
		if (selectionNode == null)
			return;

		File file = selectionNode.getFile();
		File parent = file.isDirectory() ? file : file.getParentFile();
		CreateUIPWizard w = new CreateUIPWizard(commonViewer.getControl()
				.getShell(), parent);
		if (WizardDialog.OK == w.open()) {
			Node newNode = new Node(new Path(w.getUIPFile().getPath()));
			viewer.addNewNode(file.isDirectory() ? selectionNode
					: selectionNode.getParent(), newNode);
			// new RefreshLocalFileSystemAction(viewer).run();
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		selectionChanged();
	}

	protected void selectionChanged() {
		Node node = viewer.getSelectionNode();
		if (node != null && node.getFile().exists()
		// .isDirectory()
		) {
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
	}

}
