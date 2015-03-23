package aurora.bpmn.designer.rcp.viewer.action;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

import aurora.bpmn.designer.rcp.viewer.action.dialog.BPMServicePropertyDialog;
import aurora.bpmn.designer.ws.ServiceModel;

public class ServicePropertyAction extends ViewAction {

	private TreeViewer viewer;
	private ServiceModel model;

	public ServicePropertyAction(String text, TreeViewer viewer) {
		this.setText(text);
		this.viewer = viewer;
	}

	
	
	
	@Override
	public void run() {
//		viewer.getTree().getShell();viewer
//		PropertyDialogAction a = new PropertyDialogAction(viewer.getTree().getShell(), viewer);
//		a.createDialog();
		BPMServicePropertyDialog d = new BPMServicePropertyDialog(viewer.getTree().getShell(),model);
		d.open();
	}
	



	@Override
	public void init() {

		TreeItem[] selection = viewer.getTree().getSelection();
		if (selection.length > 0) {
			Object data = selection[0].getData();
			if (data instanceof ServiceModel) {
				this.model = (ServiceModel) data;
			}
		}
		this.setVisible(model instanceof ServiceModel);

	}

}
