package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurora.bpmn.designer.rcp.viewer.INode;
import aurora.bpmn.designer.rcp.viewer.IParent;

public class ServiceModel implements IParent {

	public static final String SERVICE_NAME = "service_name";
	public static final String HOST = "host";
	public static final String PSD = "psd";
	public static final String USER_NAME = "user_name";
	// Authorization
	private String userName = "abc";
	private String password = "cba";

	private String serviceName = "HEC BPM Service";

	private String host;

//	private String saveServiceUrl;
//	private String listServiceUrl;
//	private String fetchServiceUrl;
//	private String deleteServiceUrl;

	private boolean isLoaded;

	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();
	private List<BPMNDefineCategory> categorys = new ArrayList<BPMNDefineCategory>();
	private Map<String, BPMNDefineCategory> mcs;
	private Map<String, BPMNDefineModelVER> vers = new HashMap<String, BPMNDefineModelVER>();

//	public String getSaveServiceUrl() {
//		return Endpoints.getSaveService(host);
//	}
//
//	public void setSaveServiceUrl(String saveServiceUrl) {
//		this.saveServiceUrl = saveServiceUrl;
//	}
//
//	public String getListServiceUrl() {
//		return Endpoints.getListService(host);
//	}
//
//	public void setListServiceUrl(String listServiceUrl) {
//		this.listServiceUrl = listServiceUrl;
//	}
//
//	public String getFetchServiceUrl() {
//		return Endpoints.getFetchService(host);
//	}
//
//	public void setFetchServiceUrl(String fetchServiceUrl) {
//		this.fetchServiceUrl = fetchServiceUrl;
//	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public List<BPMNDefineModel> getDefines() {
		return defines;
	}

	public void addDefine(BPMNDefineModel define) {
		if (define != null) {
			String process_code = define.getProcess_code();
			BPMNDefineModelVER ver = vers.get(process_code);
			if (ver == null) {
				ver = new BPMNDefineModelVER();
				// ver.setCategory_id(this.id);
				ver.setParent(this);
				ver.setProcess_code(process_code);
				ver.setName(define.getName());
				ver.setServiceModel(this);
				vers.put(process_code, ver);
			}
			ver.addBPMNDefineModel(define);

//			define.setParent(this);
			define.setServiceModel(this);
			this.defines.add(define);
		}
	}

	public void reload() {
		// List<BPMNDefineModel> unSaveDefines = new
		// ArrayList<BPMNDefineModel>();
		// for (BPMNDefineModel define : defines) {
		// if (define.getDefine_id() == null) {
		// unSaveDefines.add(define);
		// }
		// }
		this.isLoaded = true;
		defines = new ArrayList<BPMNDefineModel>();
		categorys = new ArrayList<BPMNDefineCategory>();
		vers = new HashMap<String, BPMNDefineModelVER>();
		// defines.addAll(unSaveDefines);
	}

//	public String getDeleteServiceUrl() {
//		return Endpoints.getDeleteService(host);
//	}
//
//	public void setDeleteServiceUrl(String deleteServiceUrl) {
//		this.deleteServiceUrl = deleteServiceUrl;
//	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<BPMNDefineCategory> getCategorys() {
		return categorys;
	}

	public void addCategory(BPMNDefineCategory category) {
		category.setParent(this);
		category.setServiceModel(this);
		this.categorys.add(category);
	}

//	public String getlistBPMCategoryServiceUrl() {
//		return Endpoints.getlistBPMCategoryService(host);
//	}

	@Override
	public IParent getParent() {
		return null;
	}

	public INode[] getChildren() {
		List<INode> nodes = new ArrayList<INode>();
		nodes.addAll(getCategorys());
		nodes.addAll(vers.values());
		return nodes.toArray(new INode[nodes.size()]);
	}

	public void setAllBPMNDefineCategory(Map<String, BPMNDefineCategory> mcs) {
		this.mcs = mcs;
	}

	public BPMNDefineCategory getBPMNDefineCategory(String id) {
		return mcs.get(id);
	}

	public Collection<BPMNDefineCategory> getAllBPMNDefineCategory() {
		return mcs.values();
	}

	@Override
	public void removeChild(INode node) {
		if (node instanceof BPMNDefineCategory) {
			this.removeDefineCategory((BPMNDefineCategory) node);
		}
		if (node instanceof BPMNDefineModel) {
			this.removeDefine((BPMNDefineModel) node);
		}
	}

	public void removeDefineCategory(BPMNDefineCategory c) {
		this.categorys.remove(c);
	}

	@Override
	public void addChild(INode node) {
		if (node instanceof BPMNDefineCategory) {
			this.addCategory((BPMNDefineCategory) node);
		}
		if (node instanceof BPMNDefineModel) {
			this.addDefine((BPMNDefineModel) node);
		}
	}

	public void removeDefine(BPMNDefineModel define) {

		if (define != null) {
			String process_code = define.getProcess_code();
			BPMNDefineModelVER ver = vers.get(process_code);
			if (ver != null) {

				ver.removeDefine(define);
				if (ver.getChildren().length == 0) {
					vers.remove(process_code);
				}
			}
			this.defines.remove(define);
		}
		defines.remove(define);
	}
}
