package aurora.bpmn.designer.rcp.viewer.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewerStore;
import aurora.bpmn.designer.rcp.viewer.ViewerInput;
import aurora.bpmn.designer.ws.ServiceModel;

public class DelServiceAction extends ViewAction {

	private TreeViewer viewer;
	private ServiceModel serviceModel;

	public DelServiceAction(String text, TreeViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {
		boolean openConfirm = MessageDialog.openConfirm(viewer.getTree()
				.getShell(), "Confirm", "是否确定");
		if (openConfirm == false)
			return;
		ViewerInput viewerInput = (ViewerInput) viewer.getInput();
		viewerInput.removeService(serviceModel);
		BPMServiceViewerStore.saveViewerInput(viewerInput);
		viewer.refresh(viewerInput);

	}

	@Override
	public void init() {

		TreeItem[] selection = viewer.getTree().getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof ServiceModel) {
				this.serviceModel = (ServiceModel) data;
			}
		}
		this.setVisible(serviceModel instanceof ServiceModel);

	}

}
