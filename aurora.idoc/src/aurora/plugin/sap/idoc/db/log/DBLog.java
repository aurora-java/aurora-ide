package aurora.plugin.sap.idoc.db.log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import uncertain.composite.CompositeMap;
import uncertain.core.IContainer;
import uncertain.core.ILifeCycle;
import uncertain.event.Configuration;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.plugin.sap.sync.idoc.DatabaseTool;
import aurora.plugin.sap.sync.idoc.IDocServerManager;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.ServiceThreadLocal;
import aurora.service.http.HttpServiceInstance;

import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.jco.server.JCoServer;

public class DBLog extends AbstractLocatableObject implements ILifeCycle {
	// type,message,date

	private String logProc;
	private IObjectRegistry registry;

	public DBLog(IObjectRegistry registry) {
		registry.registerInstance(DBLog.class, this);
		this.registry = registry;
	}

	static public DBLog instance(IDocServerManager serverManager) {
		IObjectRegistry registry = serverManager.getRegistry();
		return (DBLog) registry.getInstanceOfType(DBLog.class);
	}

	static public void otherError(IDocServerManager serverManager, String msg) {
		executeProc("OtherError", msg, serverManager);
	}

	public static void idocServerListenerStarted(
			IDocServerManager serverManager, JCoIDocServer jcoIDocServer,
			String serverName, int idocServerId) {
		executeProc("ServerListenerStarted",
				"Host : [" + jcoIDocServer.getGatewayHost()
						+ "] ServerListenerStarted ", serverManager);
		System.out.println("idocServer" + serverName + "Started");

	}

	public static void idocServerListenerStoped(
			IDocServerManager serverManager, JCoIDocServer jcoIDocServer) {
		executeProc("ServerListenerStoped",
				"Host : [" + jcoIDocServer.getGatewayHost()
						+ "] ServerListenerStoped ", serverManager);
		System.out.println("idocServer" + jcoIDocServer.getMySncName()
				+ "Stoped");

	}

	public static void idocErrorOnStoping(IDocServerManager serverManager,
			JCoIDocServer jcoIDocServer, Throwable e) {
		executeProc("ErrorOnStoping",
				"Host : [" + jcoIDocServer.getGatewayHost()
						+ "] ErrorOnStoping ", serverManager);
		System.out.println("idocServer" + jcoIDocServer.getMySncName()
				+ "idocErrorOnStoping");
	}

	public static void idocErrorOnStarting(IDocServerManager serverManager,
			JCoIDocServer jcoIDocServer, String serverName, int idocServerId,
			int idocServerId2) {
		executeProc("ErrorOnStarting",
				"Host : [" + jcoIDocServer.getGatewayHost()
						+ "] ErrorOnStarting ", serverManager);
		System.out.println("idocServer" + jcoIDocServer.getMySncName()
				+ "idocErrorOnStarting");

	}

	static private void executeProc(String type, String message,
			IDocServerManager serverManager) {

		DatabaseTool databaseTool = null;
		try {
			databaseTool = serverManager.getDatabaseTool();
			instance(serverManager).executeProc(type, message,
					databaseTool.getConnection());
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			serverManager.closeDatabaseTool(databaseTool);
		}

	}

	public static void idocErrorOnStarting(IDocServerManager serverManager,
			JCoIDocServer jcoIDocServer, String serverName, int idocServerId,
			Throwable e) {
		executeProc("ErrorOnStarting", e.getMessage(), serverManager);
		System.out.println("idocServer" + jcoIDocServer == null ? ""
				: jcoIDocServer.getMySncName() + "idocErrorOnStarting"
						+ e.getMessage());
	}

	@Override
	public boolean startup() {
		return true;
	}

	@Override
	public void shutdown() {

	}

	public String getLogProc() {
		return logProc;
	}

	public void setLogProc(String logProc) {
		this.logProc = logProc;
	}

	public static void receiveIdocFile(IDocServerManager serverManager,
			String fileFullPath, int idocFileId, int idocFileId2) {
		System.out.println("receiveIdocFile {" + fileFullPath + " }");
	}

	public static void ln(String msg) {
		System.out.println(msg);
	}

	private void executeProc(String type, String message, Connection connection)
			throws Exception {
		// pp2(type, message, connection);
		executeProc(type, message);
	}

	private void pp2(String type, String message, Connection connection)
			throws Exception {
		CompositeMap context = new CompositeMap("context");
		context.putObject("/parameter/@type", type, true);
		context.putObject("/parameter/@message", message, true);
		context.putObject("/parameter/@date", new Date(), true);
		context.putObject("/session/@user_id", 0, true);
		ServiceThreadLocal.setCurrentThreadContext(context);
		IProcedureManager procedureManager = (IProcedureManager) registry
				.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null,
					IProcedureManager.class, this.getClass().getName());
		IServiceFactory serviceFactory = (IServiceFactory) registry
				.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null,
					IServiceFactory.class, this.getClass().getName());
		Procedure proc = procedureManager.loadProcedure(logProc);
		ServiceInvoker.invokeProcedureWithTransaction(logProc, proc,
				serviceFactory, context, connection);
	}

	public void executeProc(String type, String message) throws Exception {
		IProcedureManager procedureManager = (IProcedureManager) registry
				.getInstanceOfType(IProcedureManager.class);
		IServiceFactory serviceFactory = (IServiceFactory) registry
				.getInstanceOfType(IServiceFactory.class);
		String autoLoginProc = logProc;
		Procedure proc = procedureManager.loadProcedure(autoLoginProc);
		// CompositeMap auroraContext = new CompositeMap("context");
		// CompositeMap createChild = auroraContext.createChild("parameter");
		CompositeMap auroraContext = new CompositeMap("context");
		auroraContext.putObject("/parameter/@type", type, true);
		auroraContext.putObject("/parameter/@message", message, true);
		auroraContext.putObject("/parameter/@date", new Date(), true);
		auroraContext.putObject("/session/@user_id", 0, true);
		// createChild.put("LCR", lcr);
		HttpServiceInstance svc = createHttpService(autoLoginProc,
				procedureManager, auroraContext);

		ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		ServiceInvoker.invokeProcedureWithTransaction(autoLoginProc, proc,
				serviceFactory, svc, auroraContext);

		// ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		// HttpRequestTransfer.copyRequest(svc);
		// HttpSessionCopy.copySession(auroraContext,
		// request.getSession(false));
	}

	public HttpServiceInstance createHttpService(String service_name,

	IProcedureManager procedureManager, CompositeMap context) {
		HttpServiceInstance svc = new HttpServiceInstance(service_name,
				procedureManager);
		// svc.setRequest(request);
		// svc.setResponse(response);
		svc.setContextMap(context);
		svc.setName(service_name);
		// HttpRequestTransfer.copyRequest(svc);
		// HttpSessionCopy.copySession(svc.getContextMap(),
		// request.getSession(false));
		IContainer container = (IContainer) registry
				.getInstanceOfType(IContainer.class);
		Configuration config = (Configuration) container.getEventDispatcher();
		if (config != null)
			svc.setRootConfig(config);
		return svc;
	}

	public static void idocServerIsDead(JCoServer jcoIDocServer,
			IDocServerManager serverManager) {
		executeProc("ServerIsDead", "Host : [" + jcoIDocServer.getGatewayHost()
				+ "] ServerIsDead ", serverManager);
	}
}
