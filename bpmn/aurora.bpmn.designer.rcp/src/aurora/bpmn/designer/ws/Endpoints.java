package aurora.bpmn.designer.ws;

public class Endpoints {
	// public static final String HOST = "http://localhost:8888/HAP_DBI/";

	public static final String SAVE_SERVICE = "/modules/bpm/ws/save_bpm_define.svc";
	public static final String LIST_SERVICE = "/modules/bpm/ws/query_bpm_define_list.svc";
	public static final String FETCH_SERVICE = "/modules/bpm/ws/fetch_bpm_define.svc";
	public static final String DEL_SERVICE = "/modules/bpm/ws/del_bpm_define.svc";
	public static final String LIST_CATEGORY_SERVICE = "/modules/bpm/ws/query_bpm_category_list.svc";

	public static final String T_FETCH_BPM = "fetchBPM";
	// public static final String T_SAVE_BPM = "saveBPM";
	public static final String T_CREATE_BPM = "createBPM";
	public static final String T_UPDATE_BPM = "updateBPM";
	public static final String T_LIST_BPM = "listBPM";
	public static final String T_DELETE_BPM = "deleteBPM";
	public static final String T_LIST_CATEGORY = "listCategory";
	public static final String T_SUBMIT_BPM = "submitBPM";
	public static final String T_ENABLE_BPM = "enableBPM";

	private String type;
	private String url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Endpoints(String url, String type) {
		super();
		this.type = type;
		this.url = url;
	}

	public static Endpoints getListService(String host, String type) {
		return new Endpoints(host + LIST_SERVICE, type);
	}

	public static Endpoints getSaveService(String host, String type) {
		return new Endpoints(host + SAVE_SERVICE, type);
	}

	public static Endpoints getFetchService(String host, String type) {
		return new Endpoints(host + FETCH_SERVICE, type);
	}

	public static Endpoints getDeleteService(String host, String type) {
		return new Endpoints(host + DEL_SERVICE, type);
	}

	public static Endpoints getlistBPMCategoryService(String host, String type) {
		return new Endpoints(host + LIST_CATEGORY_SERVICE, type);
	}

}
