package aurora.plugin.sap.sync.idoc;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

public class SyncProcessor implements Runnable {

	private IDocServerManager serverManager;
	private IDocProcessManager iDocProcessManager;
	private DatabaseTool dbTool;
	private Set<IDocType> errorIdocTypes = new HashSet<IDocType>();
	private ILogger logger;

	public SyncProcessor(IDocServerManager serverManager, IDocProcessManager iDocProcessManager) {
		this.serverManager = serverManager;
		this.iDocProcessManager = iDocProcessManager;
		this.logger = serverManager.getLogger();
	}

	public void run() {
		while (serverManager.isRunning()) {
			IDocFile idocFile = iDocProcessManager.pollSyncFile();
			if (idocFile == null) {
				sleepOneSecond();
				continue;
			}
			sync(idocFile);
		}
	}

	private void sync(IDocFile idocFile) {
		try {
			dbTool = serverManager.getDatabaseTool();
			//同步数据
			String exception_message = syncFileData(idocFile);
			//更新数据库状态
			String status = exception_message == null ? DatabaseTool.DONE_STATUS : DatabaseTool.EXCEPTION_STATUS;
			updateDBStatus(idocFile, status, exception_message);
			//进入下一流程操作
			boolean successful = exception_message == null ? true : false;
			iDocProcessManager.syncDone(idocFile, successful);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, getIDocFileInfo(idocFile), e);
		}finally{
			serverManager.closeDatabaseTool(dbTool);
		}

	}

	private void sleepOneSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	private void updateDBStatus(IDocFile idocFile, String status, String exception) {
		try {
			int idocFileId = idocFile.getIdocFileId();
			dbTool.updateIdocFileStatus(idocFileId, status, exception);
		} catch (AuroraIDocException e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

	private String syncFileData(IDocFile idocFile) {
		String exception_message = null;
		try {
			dbTool.disableAutoCommit();
			syncMapTables(idocFile);
			syncTrxTables(idocFile);
			dbTool.commit();
			logger.log(getIDocFileInfo(idocFile) + " SYNC Successful.");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, getIDocFileInfo(idocFile), e);
			addErrorIdocType(idocFile);
			dbTool.rollback();
			exception_message = "SYNC EXCEPTION:" + AuroraIDocException.getExceptionStackTrace(e);
		} finally {
			dbTool.enableAutoCommit();
		}
		return exception_message;
	}

	private void syncMapTables(IDocFile idocFile) throws Exception {
		int idoc_file_Id = idocFile.getIdocFileId();
		CompositeLoader loader = new CompositeLoader();
		CompositeMap iDocData = loader.loadByFile(idocFile.getFileFullPath());
		idocFile.setFileContent(iDocData);
		List<CompositeMap> childList = iDocData.getChilds();
		if (childList == null)
			return;
		for (CompositeMap idoc_node : childList) {
			if (idoc_node == null || idoc_node.getChilds() == null || idoc_node.getChilds().size() < 2) {
				return;
			}
			CompositeMap control_node = (CompositeMap) idoc_node.getChilds().get(0);
			IDocType idocType = dbTool.getIdocType(control_node);
			idocFile.setIdocType(idocType);
			if (isIdocTypeStop(idocFile))
				throw new AuroraIDocException("This idocType:" + idocType + " has error before");
			dbTool.updateIdocFileInfo(idoc_file_Id, control_node);
			for (int i = 1; i < idoc_node.getChilds().size(); i++) {
				CompositeMap content_node = (CompositeMap) idoc_node.getChilds().get(i);
				dbTool.syncMapTables(idoc_file_Id, content_node);
			}
		}
	}

	private void syncTrxTables(IDocFile idocFile) throws Exception {
		int idoc_file_Id = idocFile.getIdocFileId();
		String executePkg = dbTool.queryExecutePkg(idoc_file_Id);
		if (executePkg == null || "".equals(executePkg))
			throw new IllegalStateException(getIDocFileInfo(idocFile) + " please define execute_pkg first!");
		String errorMessage = dbTool.executePkg(executePkg, idoc_file_Id);
		if (errorMessage != null && !"".equals(errorMessage)) {
			throw new AuroraIDocException(getIDocFileInfo(idocFile) + " execute pkg " + executePkg + " failed:" + errorMessage);
		}
	}

	private String getIDocFileInfo(IDocFile idocFile) {
		return "idoc_file_id=" + idocFile.getIdocFileId();
	}

	private boolean isIdocTypeStop(IDocFile idocFile) throws SQLException, AuroraIDocException {
		IDocType idocType = idocFile.getIdocType();
		if (idocType == null)
			return false;
		boolean isOrdinal = dbTool.isOrdinal(idocType.getIdoctyp(), idocType.getCimtyp());
		if (isOrdinal && errorIdocTypes.contains(idocType)) {
			return true;
		}
		return false;
	}

	private void addErrorIdocType(IDocFile idocFile) {
		IDocType idocType = idocFile.getIdocType();
		if (idocType != null) {
			errorIdocTypes.add(idocType);
		}
	}
}
