package aurora.bpmn.designer.rcp.viewer;

import java.io.IOException;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import aurora.bpmn.designer.rcp.Activator;
import aurora.bpmn.designer.ws.ServiceModel;

public class BPMServiceViewerStore {
	static public void saveViewerInput(ViewerInput input) {
		List<ServiceModel> services = input.getServices();
		CompositeMap viewerInput = new CompositeMap("ViewerInput");
		for (ServiceModel serviceModel : services) {
			CompositeMap smap = new CompositeMap("ServiceModel");
			smap.putString(ServiceModel.USER_NAME, serviceModel.getUserName());
			smap.putString(ServiceModel.PSD, serviceModel.getPassword());
			smap.putString(ServiceModel.SERVICE_NAME,
					serviceModel.getServiceName());
			smap.putString(ServiceModel.HOST, serviceModel.getHost());
			viewerInput.addChild(smap);
		}
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		preferenceStore.setValue("ViewerInput", viewerInput.toXML());
	}

	static public ViewerInput loadViewerInput() {
		IPreferenceStore preferenceStore = Activator.getDefault()
				.getPreferenceStore();
		String viewInputs = preferenceStore.getString("ViewerInput");
		ViewerInput input = new ViewerInput();
		if(viewInputs == null || "".equals(viewInputs))
			return input;
		try {
			CompositeMap map = CompositeLoader.createInstanceForOCM()
					.loadFromString(viewInputs);
			List childsNotNull = map.getChildsNotNull();
			for (int i = 0; i < childsNotNull.size(); i++) {
				CompositeMap m = (CompositeMap) childsNotNull.get(i);
				ServiceModel smodel = new ServiceModel();
				smodel.setPassword(m.getString(ServiceModel.PSD, ""));
				smodel.setHost(m.getString(ServiceModel.HOST, ""));
				smodel.setServiceName(m
						.getString(ServiceModel.SERVICE_NAME, ""));
				smodel.setUserName(m.getString(ServiceModel.USER_NAME, ""));
				input.addService(smodel);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return input;
	}
}
