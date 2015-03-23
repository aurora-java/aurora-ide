package aurora.bpmn.designer.rcp.viewer.action.wizard;

import org.eclipse.swt.widgets.Shell;

import aurora.bpmn.designer.ws.ServiceModel;
import aurora.ide.swt.util.UWizard;

public class CreateBPMServiceWizard extends UWizard {

	private ServiceModel serviceModel;
	private CreateBPMServicePage page;

	public CreateBPMServiceWizard(Shell shell) {
		super(shell);
	}

	@Override
	public void addPages() {
		page = new CreateBPMServicePage("CreateBPMServicePage");
		this.addPage(page);
	}

	@Override
	public boolean performFinish() {
		serviceModel = new ServiceModel();
		serviceModel.setUserName(page.getModel().getStringPropertyValue(
				ServiceModel.USER_NAME));
		serviceModel.setPassword(page.getModel().getStringPropertyValue(
				ServiceModel.PSD));
		serviceModel.setHost(page.getModel().getStringPropertyValue(
				ServiceModel.HOST));
		serviceModel.setServiceName(page.getModel().getStringPropertyValue(
				ServiceModel.SERVICE_NAME));
		return true;
	}

	public ServiceModel getServiceModel() {
		return serviceModel;
	}
}
