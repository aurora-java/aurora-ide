package aurora.plugin.sap.sync.idoc;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.sql.DataSource;

import aurora.plugin.sap.ISapConfig;

import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

public class IDocServerManager extends AbstractLocatableObject implements ILifeCycle {

	public static final String PLUGIN = IDocServerManager.class.getCanonicalName();
	public static final String SERVER_NAME_SEPARATOR = ",";
	public static final String AURORA_IDOC_PLUGIN_VERSION = "2.1";

	private IObjectRegistry registry;

	private String serverNameList;
	private String idocFileDir;
	private boolean keepIdocFile = true;
	private long keepIdocFileTime = 1;
	private boolean interfaceEnabledFlag = true;
	private boolean enabledJCo = true;
	private int reconnectTime = 20000;// 1 minute
	private int maxReconnectTime = 3600000;// 1 hour
	private boolean debug = false;

	private List<IDocServer> idocServerList = new LinkedList<IDocServer>();
	private ILogger logger;
	private DataSource datasource;
	private DestinationProvider destinationProvider;
	private boolean running = true;

	public IDocServerManager(IObjectRegistry registry) {
		this.registry = registry;
	}

	private void initParameters() {

		logger = LoggingContext.getLogger(PLUGIN, registry);
		logger.info("Aurora IDoc Plugin Version: " + AURORA_IDOC_PLUGIN_VERSION);

		datasource = (DataSource) registry.getInstanceOfType(DataSource.class);
		if (datasource == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(this, DataSource.class, this.getClass().getCanonicalName());
		if (serverNameList == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "serverNameList");
		if (idocFileDir == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "idocFileDir");
		File idocDir = new File(idocFileDir);
		if (!idocDir.exists()) {
			throw BuiltinExceptionFactory.createRequiredFileNotFound(idocFileDir);
		}
	}

	@Override
	public boolean startup() {
		initParameters();
		if(isEnabledJCo()){
			destinationProvider = new DestinationProvider();
			registry.registerInstance(ISapConfig.class, destinationProvider);
		}
		
		String[] servers = serverNameList.split(SERVER_NAME_SEPARATOR);
		for (int i = 0; i < servers.length; i++) {
			String serverName = servers[i];
			IDocServer server = new IDocServer(this, serverName);
			server.startup();
			idocServerList.add(server);
		}
		return true;
	}

	public IObjectRegistry getRegistry() {
		return registry;
	}
	
	public DatabaseTool getDatabaseTool() throws SQLException {
		Connection connection = datasource.getConnection();
		DatabaseTool dbTool = new DatabaseTool(connection, logger);
		return dbTool;
	}
	public void closeDatabaseTool(DatabaseTool databaseManager){
		if (databaseManager != null)
			databaseManager.close();
	}

	@Override
	public void shutdown() {
		running = false;
		if (idocServerList != null && !idocServerList.isEmpty()) {
			for (IDocServer server : idocServerList) {
				try {
					server.shutdown();
				} catch (Throwable e) {
					logger.log(Level.SEVERE, "shutdown server " + server.getServerName() + " failed!", e);
				}
			}
			idocServerList = null;
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getServerNameList() {
		return serverNameList;
	}

	public void setServerNameList(String serverNameList) {
		this.serverNameList = serverNameList;
	}

	public String getIdocFileDir() {
		return idocFileDir;
	}

	public void setIdocFileDir(String idocFileDir) {
		this.idocFileDir = idocFileDir;
	}

	public boolean isKeepIdocFile() {
		return keepIdocFile;
	}

	public boolean getKeepIdocFile() {
		return keepIdocFile;
	}

	public void setKeepIdocFile(boolean keepIdocFile) {
		this.keepIdocFile = keepIdocFile;
	}

	public boolean isInterfaceEnabledFlag() {
		return interfaceEnabledFlag;
	}

	public boolean getInterfaceEnabledFlag() {
		return interfaceEnabledFlag;
	}

	public void setInterfaceEnabledFlag(boolean interfaceEnabledFlag) {
		this.interfaceEnabledFlag = interfaceEnabledFlag;
	}

	public int getReconnectTime() {
		return reconnectTime;
	}

	public void setReconnectTime(int reconnectTime) {
		this.reconnectTime = reconnectTime;
	}

	public int getMaxReconnectTime() {
		return maxReconnectTime;
	}

	public void setMaxReconnectTime(int maxReconnectTime) {
		this.maxReconnectTime = maxReconnectTime;
	}

	public ILogger getLogger() {
		return logger;
	}

	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public boolean isEnabledJCo() {
		return enabledJCo;
	}

	public boolean getEnabledJCo() {
		return enabledJCo;
	}

	public void setEnabledJCo(boolean enabledJCo) {
		this.enabledJCo = enabledJCo;
	}

	public boolean isDebug() {
		return debug;
	}
	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void addDestination(String destinationName){
		if(destinationProvider != null)
			destinationProvider.addDestination(destinationName);
	}

	public long getKeepIdocFileTime() {
		return keepIdocFileTime;
	}

	public void setKeepIdocFileTime(long keepIdocFileTime) {
		this.keepIdocFileTime = keepIdocFileTime;
	}
}
