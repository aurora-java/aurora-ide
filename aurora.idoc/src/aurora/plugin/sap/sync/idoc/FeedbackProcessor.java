package aurora.plugin.sap.sync.idoc;

import java.sql.Connection;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.service.IServiceFactory;
import aurora.service.ServiceInvoker;
import aurora.service.ServiceThreadLocal;

public class FeedbackProcessor implements Runnable {

	private IObjectRegistry registry;
	private IDocServerManager serverManager;
	private IDocProcessManager iDocProcessManager;
	private DatabaseTool dbTool;
	private ILogger logger;

	public FeedbackProcessor(IDocServerManager serverManager, IDocProcessManager iDocProcessManager) {
		this.serverManager = serverManager;
		this.iDocProcessManager = iDocProcessManager;
		this.registry = serverManager.getRegistry();
		this.logger = serverManager.getLogger();
	}

	@Override
	public void run() {
		while (serverManager.isRunning()) {
			IDocFile idocFile = iDocProcessManager.pollFeedbackFile();
			if (idocFile == null) {
				sleepOneSecond();
				continue;
			}
			executeFeedbackProc(idocFile);
		}
	}

	private void executeFeedbackProc(IDocFile idocFile) {
		int idocFileId = idocFile.getIdocFileId();
		try {
			dbTool = serverManager.getDatabaseTool();
			String feedback_proc = dbTool.queryFeedbackProc(idocFileId);
			if (feedback_proc == null)
				return;
			executeProc(idocFile, feedback_proc, dbTool.getConnection());
			logger.log("idoc_file_id=" + idocFile.getIdocFileId() + " load procedure:{0} successful.", new Object[] { feedback_proc });
			recordFeedback(idocFileId, DatabaseTool.DONE_STATUS, "");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "idoc_file_id=" + idocFile.getIdocFileId(), e);
			recordFeedback(idocFileId, DatabaseTool.EXCEPTION_STATUS, AuroraIDocException.getExceptionStackTrace(e));
		} finally {
			serverManager.closeDatabaseTool(dbTool);
		}
	}

	private void recordFeedback(int idoc_file_id, String status, String message) {
		try {
			dbTool.recordFeedback(idoc_file_id, status, message);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

	private void executeProc(IDocFile idocFile, String procedure_name, Connection connection) throws Exception {
		CompositeMap context = new CompositeMap("context");
		int idoc_file_id = idocFile.getIdocFileId();
		context.putObject("/parameter/@idoc_file_id", idoc_file_id, true);
		context.putObject("/session/@user_id", 0, true);
		ServiceThreadLocal.setCurrentThreadContext(context);
		IProcedureManager procedureManager = (IProcedureManager) registry.getInstanceOfType(IProcedureManager.class);
		if (procedureManager == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IProcedureManager.class, this.getClass().getName());
		IServiceFactory serviceFactory = (IServiceFactory) registry.getInstanceOfType(IServiceFactory.class);
		if (serviceFactory == null)
			throw BuiltinExceptionFactory.createInstanceNotFoundException(null, IServiceFactory.class, this.getClass().getName());
		Procedure proc = procedureManager.loadProcedure(procedure_name);
		ServiceInvoker.invokeProcedureWithTransaction(procedure_name, proc, serviceFactory, context, connection);
	}

	private void sleepOneSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
