package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.List;

public class BPMServiceResponse {

	public static final int fail = -1;
	public static final int sucess = 1;
	
	private String status_msg;
	private int status;
	private int bpmSize;
	private String serviceType;
	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();
	private List<BPMNDefineCategory> categorys = new ArrayList<BPMNDefineCategory>();

	public BPMServiceResponse(int status, String status_msg) {
		super();
		this.status_msg = status_msg;
		this.status = status;
	}

	public String getStatus_msg() {
		return status_msg;
	}

	public void setStatus_msg(String status_msg) {
		this.status_msg = status_msg;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getBpmSize() {
		return bpmSize;
	}

	public void setBpmSize(int bpmSize) {
		this.bpmSize = bpmSize;
	}

	public List<BPMNDefineModel> getDefines() {
		return defines;
	}

	public void addDefine(BPMNDefineModel define) {
		this.defines.add(define);
	}

	public List<BPMNDefineCategory> getCategorys() {
		return categorys;
	}

	public void addCategory(BPMNDefineCategory category) {
		this.categorys.add(category);
	}

}
