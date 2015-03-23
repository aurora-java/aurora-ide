package aurora.plugin.sap.sync.idoc;

import java.util.List;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

public class BackupToInterfaceProcessor implements Runnable {

	private IDocServerManager serverManager;
	private IDocProcessManager iDocProcessManager;
	private DatabaseTool dbTool;
	private ILogger logger;

	public BackupToInterfaceProcessor(IDocServerManager serverManager, IDocProcessManager iDocProcessManager) {
		this.serverManager = serverManager;
		this.iDocProcessManager = iDocProcessManager;
		this.logger = serverManager.getLogger();
	}

	public void run() {
		while (serverManager.isRunning()) {
			IDocFile idocFile = iDocProcessManager.pollBackupFile();
			if (idocFile == null) {
				sleepOneSecond();
				continue;
			}
			backup(idocFile);
		}
	}

	private void backup(IDocFile idocFile) {
		try {
			dbTool = serverManager.getDatabaseTool();
			//同步数据
			String exception_message = backupFileData(idocFile);
			//更新数据库状态
			String status = exception_message == null ? DatabaseTool.DONE_STATUS : DatabaseTool.EXCEPTION_STATUS;
			updateDBStatus(idocFile, status, exception_message);
			//进入下一流程操作
			boolean successful = exception_message == null ? true : false;
			iDocProcessManager.backupDone(idocFile, successful);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, getIDocFileInfo(idocFile), e);
		} finally {
			serverManager.closeDatabaseTool(dbTool);
		}
		
	}

	private void sleepOneSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
	private String backupFileData(IDocFile idocFile) {
		String exception_message = null;
		try {
			dbTool.disableAutoCommit();
			insertInterface(idocFile);
			dbTool.commit();
			logger.config(getIDocFileInfo(idocFile) + " Backup To Interface Successful.");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, getIDocFileInfo(idocFile), e);
			dbTool.rollback();
			exception_message = "BACKUP TO INTERFACE TABLE EXCEPTION:" + AuroraIDocException.getExceptionStackTrace(e);
		} finally {
			dbTool.enableAutoCommit();
		}
		return exception_message;
	}

	private String getIDocFileInfo(IDocFile idocFile) {
		return "idoc_file_id=" + idocFile.getIdocFileId();
	}
	
	private void updateDBStatus(IDocFile idocFile, String status,String exception) {
		try {
			int idocFileId = idocFile.getIdocFileId();
			dbTool.updateIdocFileStatus(idocFileId, status,exception);
		} catch (AuroraIDocException e) {
			logger.log(Level.SEVERE, "", e);
		}
	}

	private void insertInterface(IDocFile idocFile) throws AuroraIDocException {
		int header_id = -1;
		int idocFileId = idocFile.getIdocFileId();
		CompositeMap iDocData = idocFile.getFileContent();
		List<CompositeMap> childList = iDocData.getChilds();
		if (childList == null)
			return;
		for (CompositeMap idoc_node : childList) {
			if (idoc_node == null || idoc_node.getChildIterator() == null || idoc_node.getChilds().size() < 2) {
				return;
			}
			CompositeMap control_node = (CompositeMap) idoc_node.getChilds().get(0);
			header_id = dbTool.addInterfaceHeader(idocFileId, control_node);
			for (int i = 1; i < idoc_node.getChilds().size(); i++) {
				CompositeMap content_node = (CompositeMap) idoc_node.getChilds().get(i);
				dbTool.addInterfaceLine(header_id, content_node);
			}
		}
	}
}
