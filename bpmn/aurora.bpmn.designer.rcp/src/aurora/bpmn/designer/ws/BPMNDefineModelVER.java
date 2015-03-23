package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.List;

import aurora.bpmn.designer.rcp.viewer.INode;
import aurora.bpmn.designer.rcp.viewer.IParent;

public class BPMNDefineModelVER implements IParent {

	private String name;
	private String process_code;
	private String category_id;

	private IParent parent;

	private ServiceModel serviceModel;

	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
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

	public void addBPMNDefineModel(BPMNDefineModel define) {
		define.setParent(this);
		defines.add(define);
	}

	@Override
	public INode[] getChildren() {
		return defines.toArray(new INode[defines.size()]);

	}

	@Override
	public void removeChild(INode node) {
		if (node instanceof BPMNDefineModel) {
			this.removeDefine((BPMNDefineModel) node);
		}
	
	}

	public void removeDefine(BPMNDefineModel define) {
		defines.remove(define);
	}

	@Override
	public void addChild(INode node) {
		if (node instanceof BPMNDefineModel) {
			this.addBPMNDefineModel((BPMNDefineModel) node);
		}
	}

}
