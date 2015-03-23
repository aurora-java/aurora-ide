package aurora.plugin.sap.jco3;

public class InstanceConfig {
	
	public String sid;
	public String userid;
	public String password;
	public String server_ip;
	public String default_lang;
	public int max_conn;
	public String sap_client;
	public String system_number;
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServer_ip() {
		return server_ip;
	}

	public void setServer_ip(String server_ip) {
		this.server_ip = server_ip;
	}

	public String getDefault_lang() {
		return default_lang;
	}

	public void setDefault_lang(String default_lang) {
		this.default_lang = default_lang;
	}

	public int getMax_conn() {
		return max_conn;
	}

	public void setMax_conn(int max_conn) {
		this.max_conn = max_conn;
	}

	public String getSap_client() {
		return sap_client;
	}

	public void setSap_client(String sap_client) {
		this.sap_client = sap_client;
	}

	public String getSystem_number() {
		return system_number;
	}

	public void setSystem_number(String system_number) {
		this.system_number = system_number;
	}
}
