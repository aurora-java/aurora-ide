package aurora.bpmn.designer.rcp.viewer;

import java.util.ArrayList;
import java.util.List;

import aurora.bpmn.designer.ws.ServiceModel;

public class ViewerInput {
	private List<ServiceModel> services = new ArrayList<ServiceModel>();

	public List<ServiceModel> getServices() {
		return services;
	}

	public void addService(ServiceModel service) {
		this.services.add(service);
	}

	public void removeService(ServiceModel service) {
		this.services.remove(service);
	}

}
