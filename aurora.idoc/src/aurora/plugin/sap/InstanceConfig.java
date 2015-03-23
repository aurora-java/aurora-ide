package aurora.plugin.sap;

import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

public class InstanceConfig {
	public static final String LOGGING_TOPIC = "aurora.plugin.sap";

	public String sid;
	public String userid;
	public String password;
	public String server_ip;
	public String default_lang;
	public int max_conn;
	public String sap_client;
	public String system_number;

	ILogger logger;

	IRepository repository;

	private boolean inited = false;

    IObjectRegistry mObjectRegistry;

	public InstanceConfig(IObjectRegistry registry) {
		this.mObjectRegistry=registry;
		logger=LoggingContext.getLogger(LOGGING_TOPIC,mObjectRegistry);	
	}

	public void prepare() {
		if (!inited) {
			JCO.addClientPool(sid, // Alias for this pool
					max_conn, // Max. number of connections
					sap_client, // SAP client
					userid, // userid
					password, // password
					default_lang, // language
					server_ip, // host name
					system_number);
			repository = JCO.createRepository("MYRepository", sid);
			if (logger != null)
				logger.info("SAP connection pool " + sid + " created");
			inited = true;
		}
	}

	public IRepository getRepository() {
		if (!inited)
			prepare();
		if (repository == null)
			throw new RuntimeException("SAP connection pool can't be created");
		return repository;
	}

	public void release() {
		JCO.removeClientPool(sid);
		if (logger != null)
			logger.info("SAP connection pool " + sid + " released");
		inited = false;
	}

	// private boolean inited = false;
	public JCO.Client getClient() {
		if (!inited)
			prepare();
		JCO.Client client = JCO.getClientPoolManager().getClient(sid);
		return client;
	}

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
