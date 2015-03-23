package aurora.bpmn.designer.rcp.viewer.action;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import aurora.bpmn.designer.rcp.viewer.BPMServiceViewer;
import aurora.bpmn.designer.rcp.viewer.BPMServiceViewerStore;
import aurora.bpmn.designer.rcp.viewer.ViewerInput;
import aurora.bpmn.designer.rcp.viewer.action.wizard.CreateBPMServiceWizard;
import aurora.bpmn.designer.ws.ServiceModel;

public class CreateServiceAction extends ViewAction {

	private BPMServiceViewer bpmServiceViewer;

	public CreateServiceAction(BPMServiceViewer bpmServiceViewer, String text) {
		super();
		this.bpmServiceViewer = bpmServiceViewer;
		this.setText(text);
	}

	public void run() {
		Shell shell = bpmServiceViewer.getSite().getShell();

		CreateBPMServiceWizard wizard = new CreateBPMServiceWizard(shell);
		int open = wizard.open();
		if (open == WizardDialog.OK) {
			ServiceModel serviceModel = wizard.getServiceModel();
			ViewerInput viewerInput = bpmServiceViewer.getViewerInput();
			viewerInput.addService(serviceModel);
			BPMServiceViewerStore.saveViewerInput(viewerInput);
			bpmServiceViewer.getTreeViewer().refresh(viewerInput);
		}
	}

	@Override
	public void init() {
		this.setVisible(true);
	}

}
