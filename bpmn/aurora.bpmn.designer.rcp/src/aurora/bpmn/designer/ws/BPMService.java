package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.util.Base64;
import org.apache.commons.httpclient.Header;

public class BPMService {
	private ServiceModel sm;
//	private BPMNDefineModel bdm;
	
	private Map<String,String> paras = new HashMap<String,String>();

	private String serviceType;

	public BPMService(ServiceModel sm) {
		super();
		this.sm = sm;
	}

	public ServiceModel getServiceModel() {
		return sm;
	}


	protected void addAuthorization(String userName, String password,
			Options options) {
		String encoded = new String(Base64.encode(new String(userName + ":"
				+ password).getBytes()));
		List list = new ArrayList();
		// Create an instance of org.apache.commons.httpclient.Header
		Header header = new Header();
		header.setName("Authorization");
		header.setValue("Basic " + encoded);
		list.add(header);
		options.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.HTTP_HEADERS,
				list);
	}

	protected OMElement makeRequest( String type) {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement request = factory.createOMElement(new QName("", "parameter"));
		addAttribute(request, "serviceType", type, null);

		if(this.paras!=null &&paras.size()>0){
			Set<String> keySet = paras.keySet();
			for (String key : keySet) {
				addAttribute(request, key, paras.get(key), null);
			}
		}
		return request;
	}

	protected void addAttribute(OMElement request, String att, String value,
			OMNamespace omns) {
		if (value != null)
			request.addAttribute(att, value, null);
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public OMElement send(Endpoints endpoint) throws AxisFault {

		ServiceClient client = new ServiceClient();
		Options options = new Options();
		options.setTo(new EndpointReference(endpoint.getUrl()));// 修正为实际工程的URL
		// addAuthorization("linjinxiao", "ok", options);
		addAuthorization(sm.getUserName(), sm.getPassword(), options);

		client.setOptions(options);
		OMElement request = makeRequest(endpoint.getType());
		OMElement response = client.sendReceive(request);
		return response;
		// System.out.println("response:" + response.toString());

	}

	public Map<String,String> getParas() {
		return paras;
	}

	public void setParas(Map<String,String> paras) {
		this.paras = paras;
	}

}
