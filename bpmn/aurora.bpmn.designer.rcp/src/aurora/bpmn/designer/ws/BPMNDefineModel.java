package aurora.bpmn.designer.ws;

import aurora.bpmn.designer.rcp.viewer.INode;
import aurora.bpmn.designer.rcp.viewer.IParent;

public class BPMNDefineModel implements INode {

	private String name = "";
	private String define_id;
	private String process_code = "";
	private String process_version = "";
	//Y N
	private String current_version_flag ="N";
	private String defines;
	private String description = "";

	// 0,1,2
	private String approve_flag = "0";
	// Y N
	private String enable = "N";
	private String category_id;

	private IParent parent;

	private ServiceModel serviceModel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefine_id() {
		return define_id;
	}

	public void setDefine_id(String define_id) {
		this.define_id = define_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getProcess_version() {
		return process_version;
	}

	public void setProcess_version(String process_version) {
		this.process_version = process_version;
	}

	public String getCurrent_version_flag() {
		return current_version_flag;
	}

	public void setCurrent_version_flag(String current_version_flag) {
		this.current_version_flag = current_version_flag;
	}

	public String getDefines() {
		return defines;
	}

	public void setDefine(String defines) {
		this.defines = defines;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceModel getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(ServiceModel serviceModel) {
		this.serviceModel = serviceModel;
	}

	public void copy(BPMNDefineModel define) {
		this.current_version_flag = define.getCurrent_version_flag();
		this.define_id = define.getDefine_id();
		this.defines = define.getDefines();
		this.description = define.getDescription();
		this.name = define.getName();
		this.process_code = define.getProcess_code();
		this.process_version = define.getProcess_version();

	}

	public String getApprove_flag() {
		return approve_flag;
	}

	public void setApprove_flag(String approve_flag) {
		this.approve_flag = approve_flag;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
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
}
