package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurora.bpmn.designer.rcp.viewer.INode;
import aurora.bpmn.designer.rcp.viewer.IParent;

public class BPMNDefineCategory implements IParent {

	private List<BPMNDefineCategory> categorys = new ArrayList<BPMNDefineCategory>();
	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();

	private Map<String,BPMNDefineModelVER> vers  = new HashMap<String,BPMNDefineModelVER>();
	
	
	private IParent parent;

	private ServiceModel serviceModel;

	private String id;
	private String parent_id;
	private String name;

	public List<BPMNDefineCategory> getCategorys() {
		return categorys;
	}

	public void addCategory(BPMNDefineCategory category) {
		category.setParent(this);
		category.setServiceModel(serviceModel);
		this.categorys.add(category);
	}

	public List<BPMNDefineModel> getDefines() {
		return defines;
	}

	public void addDefine(BPMNDefineModel define) {
		
		String process_code = define.getProcess_code();
		
		BPMNDefineModelVER ver = vers.get(process_code);
		if(ver == null){
			ver = new BPMNDefineModelVER();
			ver.setCategory_id(this.id);
			ver.setParent(this);
			ver.setProcess_code(process_code);
			ver.setName(define.getName());
			ver.setServiceModel(serviceModel);
			vers.put(process_code, ver);
		}
		ver.addBPMNDefineModel(define);
		
		
		define.setParent(this);
		define.setServiceModel(serviceModel);
		this.defines.add(define);
	}

	public IParent getParent() {
		return parent;
	}

	public void setParent(IParent parent) {
		this.parent = parent;
	}

	public ServiceModel getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(ServiceModel serviceModel) {
		this.serviceModel = serviceModel;
	}

//	public void removeDefine(BPMNDefineModel define) {
//		defines.remove(define);
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	@Override
	public INode[] getChildren() {
		List<INode> nodes = new ArrayList<INode>();
		nodes.addAll(getCategorys());
		nodes.addAll(getDefineNodes());
		return nodes.toArray(new INode[nodes.size()]);
	}

	private Collection<? extends INode> getDefineNodes() {
		return vers.values();
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
