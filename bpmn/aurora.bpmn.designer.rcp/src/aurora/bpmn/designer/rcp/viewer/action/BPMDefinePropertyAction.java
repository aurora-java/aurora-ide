package aurora.bpmn.designer.rcp.viewer.action;

import org.eclipse.swt.widgets.TreeItem;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.rcp.viewer.action.dialog.BPMDefinePropertyDialog;
import aurora.bpmn.designer.ws.BPMNDefineModel;

public class BPMDefinePropertyAction extends ViewAction {
	private BPMNDefineModel model;
	private BPMServiceViewer viewer;

	public BPMDefinePropertyAction(String text, BPMServiceViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	public void run() {
		BPMDefinePropertyDialog dia = new BPMDefinePropertyDialog(viewer.getSite().getShell(),model);
		dia.open();
	}

	@Override
	public void init() {
		TreeItem[] selection = viewer.getTreeViewer().getTree().getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof BPMNDefineModel) {
				this.model = (BPMNDefineModel) data;
			}
		}
		this.setVisible(model instanceof BPMNDefineModel);
	}

}
