package aurora.plugin.sap.sync.idoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;

import uncertain.logging.ILogger;

import aurora.plugin.sap.idoc.db.log.DBLog;

import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.idoc.jco.JCoIDocHandler;
import com.sap.conn.idoc.jco.JCoIDocHandlerFactory;
import com.sap.conn.idoc.jco.JCoIDocServer;
import com.sap.conn.idoc.jco.JCoIDocServerContext;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;
import com.sap.conn.jco.server.JCoServerTIDHandler;

public class IDocFileListener {
	private IDocServerManager serverManager;
	private IDocProcessManager iDocProcessManager;
	private String serverName;

	private ILogger logger;
	public JCoIDocServer jcoIDocServer;
	private int idocServerId = -1;

	public IDocFileListener(IDocServerManager serverManager, String serverName,
			IDocProcessManager iDocProcessManager) {
		this.serverManager = serverManager;
		this.serverName = serverName;
		this.iDocProcessManager = iDocProcessManager;
		this.logger = serverManager.getLogger();
	}

	public void start() {
		if (!serverManager.isRunning())
			return;
		String processMessage = "";
		DatabaseTool dbTool = null;
		try {
			dbTool = serverManager.getDatabaseTool();
			if (jcoIDocServer == null
					|| JCoServerState.DEAD.equals(jcoIDocServer.getState())) {
				processMessage = "start IDocServer " + serverName;
				jcoIDocServer = JCoIDoc.getServer(serverName);
				jcoIDocServer
						.addServerStateChangedListener(new JCoServerStateChangedListener() {
							public void serverStateChangeOccurred(
									JCoServer arg0, JCoServerState arg1,
									JCoServerState arg2) {
								DBLog.ln("State Changed [" + arg1.name()
										+ "] >> [" + arg2.name() + "]");

							}
						})  ;
				addListeners();
				logger.config(serverName + " ConnectionCount= "
						+ jcoIDocServer.getConnectionCount());
				if (jcoIDocServer.getConnectionCount() == 0) {
					jcoIDocServer.setConnectionCount(1);
				}
			}
			if (idocServerId < 0) {
				processMessage = "fetch idocServerId for IDocServer "
						+ serverName;
				idocServerId = dbTool.addIDocServer(jcoIDocServer, serverName);
				logger.log("IDocServer " + serverName
						+ " 's idoc_server_id is " + idocServerId);
				String programID = jcoIDocServer.getProgramID();
				processMessage = "fetch fetchUnsettledIdocFiles for programID "
						+ programID;

				if (serverManager.isDebug()) {
					FetchUnsettleIDocFile idocFile = new FetchUnsettleIDocFile(
							programID);
					idocFile.start();
				} else
					fetchUnsettledIdocFiles(dbTool, programID);
			}
			JCoServerState state = jcoIDocServer.getState();
			jcoIDocServer.start();

			if (!isRunning()) {
				System.err.println("Connect IDocServer " + serverName
						+ " failed!");
				logger.log(serverName + "'s status is " + state);
				dbTool.updateIDocServerStatus(idocServerId,
						"Error occurred:please check the console or log for details.");
				DBLog.idocErrorOnStarting(serverManager, jcoIDocServer,
						serverName, idocServerId, idocServerId);
			} else {
				if (serverManager.isEnabledJCo()) {
					serverManager.addDestination(jcoIDocServer
							.getRepositoryDestination());
				}
				System.out.println("Connect IDocServer " + serverName
						+ " successful!");
				logger.log("Connect IDocServer " + serverName + " successful!");
				dbTool.updateIDocServerStatus(idocServerId, "OK");

				DBLog.idocServerListenerStarted(serverManager, jcoIDocServer,
						serverName, idocServerId);

			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("Error! Connect IDocServer " + serverName
					+ " failed!");
			logger.log(Level.SEVERE, processMessage, e);
			DBLog.idocErrorOnStarting(serverManager, jcoIDocServer, serverName,
					idocServerId, e);
		} finally {
			serverManager.closeDatabaseTool(dbTool);
		}
	}

	class FetchUnsettleIDocFile extends Thread {
		String programID;

		public FetchUnsettleIDocFile(String programID) {
			this.programID = programID;
		}

		@Override
		public void run() {
			while (serverManager.isRunning()) {
				sleepOneSecond();
				if (!iDocProcessManager.isSyncFileEmpty())
					continue;
				DatabaseTool dbTool = null;
				try {
					dbTool = serverManager.getDatabaseTool();
					fetchUnsettledIdocFiles(dbTool, programID);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (dbTool != null)
						dbTool.close();
				}
			}
		}

		private void sleepOneSecond() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void fetchUnsettledIdocFiles(DatabaseTool databaseManager,
			String programID) throws AuroraIDocException {
		List<IDocFile> unsettledIdocFiles = databaseManager
				.fetchUnsettledIdocFiles(programID);
		if (unsettledIdocFiles != null)
			iDocProcessManager.addSyncFileAll(unsettledIdocFiles);
	}

	private void addListeners() {
		IDocHandlerFactory idocHanlerFactory = new IDocHandlerFactory();
		jcoIDocServer.setIDocHandlerFactory(idocHanlerFactory);
		jcoIDocServer.setTIDHandler(new TidHandler());

		ThrowableListener listener = new ThrowableListener();
		jcoIDocServer.addServerErrorListener(listener);
		jcoIDocServer.addServerExceptionListener(listener);
	}

	class IDocHandler implements JCoIDocHandler {
		private String idocFileDir = serverManager.getIdocFileDir();

		public void handleRequest(JCoServerContext serverCtx,
				IDocDocumentList idocList) {
			FileOutputStream fos = null;
			OutputStreamWriter osw = null;
			DatabaseTool dbTool = null;
			try {
				IDocXMLProcessor xmlProcessor = JCoIDoc.getIDocFactory()
						.getIDocXMLProcessor();
				String fileName = serverCtx.getTID() + "_idoc.xml";
				File file = new File(idocFileDir, fileName);

				String fileFullPath = file.getCanonicalPath();
				DBLog.ln("file handle [ " + fileFullPath + " ]");
				fos = new FileOutputStream(file);
				osw = new OutputStreamWriter(fos, "UTF8");
				xmlProcessor.render(idocList, osw,
						IDocXMLProcessor.RENDER_WITH_TABS_AND_CRLF);
				osw.flush();
				osw.close();
				dbTool = serverManager.getDatabaseTool();
				int idocFileId = dbTool.addIDocFile(idocServerId, fileFullPath);
				logger.config("Receive idoc file. fileName=" + fileName
						+ " and id=" + idocFileId);
				IDocFile idocFile = new IDocFile(fileFullPath, idocFileId,
						idocServerId);
				iDocProcessManager.addSyncFile(idocFile);
				DBLog.receiveIdocFile(serverManager, fileFullPath, idocFileId,
						idocFileId);
			} catch (Throwable e) {
				e.printStackTrace();
				DBLog.otherError(serverManager, e.getMessage());
				logger.log(Level.SEVERE, "", e);
			} finally {
				closeOutputStreamWriter(osw);
				closeFileOutputStream(fos);
				closeDatabaseManager(dbTool);
			}
		}

		private void closeOutputStreamWriter(OutputStreamWriter osw) {
			if (osw != null)
				try {
					osw.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "", e);
				}
		}

		private void closeFileOutputStream(FileOutputStream fos) {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "", e);
				}
		}

		private void closeDatabaseManager(DatabaseTool dm) {
			if (dm != null)
				dm.close();
		}
	}

	public void addCDATATag(String idocFile) throws IOException {
		BufferedReader br = null;
		PrintWriter pw = null;
		File sourceFile = new File(idocFile);
		File destFile = new File(idocFile + ".temp");
		boolean containSpecialChar = false;
		try {
			br = new BufferedReader(new FileReader(sourceFile));
			pw = new PrintWriter(new FileWriter(destFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				int index = line.indexOf("&");
				if (index != -1) {
					containSpecialChar = true;
					int firstTagEnd = line.indexOf(">");
					int secondTagbegin = line.indexOf("<", index);
					if (firstTagEnd < index && secondTagbegin != -1) {
						String text = "<![CDATA["
								+ line.substring(firstTagEnd + 1,
										secondTagbegin) + "]]>";
						line = line.substring(0, firstTagEnd + 1) + text
								+ line.substring(secondTagbegin);
					}
				}
				pw.println(line);
			}
			br.close();
			pw.flush();
			pw.close();
			if (containSpecialChar) {
				if (sourceFile.delete()) {
					destFile.renameTo(new File(idocFile));
				}
			} else {
				destFile.delete();
			}
		} finally {
			if (br != null) {
				br.close();
			}
			if (pw != null) {
				pw.close();
			}
		}
	}

	public boolean isRunning() {
		if (jcoIDocServer == null)
			return false;
		JCoServerState jCoServerState = jcoIDocServer.getState();
		if (JCoServerState.ALIVE.equals(jCoServerState)
				|| JCoServerState.STARTED.equals(jCoServerState)) {
			return true;
		}
		return false;
	}

	public boolean isStoped() {
		if (jcoIDocServer == null)
			return true;
		JCoServerState jCoServerState = jcoIDocServer.getState();
		if (JCoServerState.STOPPED.equals(jCoServerState)) {
			return true;
		}
		return false;
	}

	public int getIDocServerId() {
		return idocServerId;
	}

	public void setIDocServerId(int idocServerId) {
		this.idocServerId = idocServerId;
	}

	public JCoIDocServer getJCoIDocServer() {
		return jcoIDocServer;
	}

	public void shutdown() {
		if (idocServerId != -1) {
			DatabaseTool dbTool = null;
			try {
				dbTool = serverManager.getDatabaseTool();
				dbTool.updateIDocServerStatus(idocServerId, "disconnect");
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "", e);
			} finally {
				serverManager.closeDatabaseTool(dbTool);
			}
		}
		logger.log("disconnect iDocServer " + serverName);
		stopIDocServer();
	}

	private void stopIDocServer() {
		try {
			if (!isStoped()) {
				jcoIDocServer.stop();
				DBLog.idocServerListenerStoped(serverManager, jcoIDocServer);
			}
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "", e);
			DBLog.idocErrorOnStoping(serverManager, jcoIDocServer, e);
		}
	}

	class IDocHandlerFactory implements JCoIDocHandlerFactory {
		private JCoIDocHandler handler = new IDocHandler();

		public JCoIDocHandler getIDocHandler(JCoIDocServerContext serverCtx) {
			return handler;
		}

		public JCoIDocHandler getIDocHandler() {
			return handler;
		}
	}

	class ThrowableListener implements JCoServerErrorListener,
			JCoServerExceptionListener {

		public void serverErrorOccurred(JCoServer server, String connectionId,
				JCoServerContextInfo ctx, Error error) {
			String message = ">>> Error occured on " + server.getProgramID()
					+ " connection " + connectionId;
			DBLog.otherError(null, message + "  " + error.getMessage());

			logger.log(Level.SEVERE, message, error);
		}

		public void serverExceptionOccurred(JCoServer server,
				String connectionId, JCoServerContextInfo ctx, Exception error) {
			String message = ">>> Exception occured on "
					+ server.getProgramID() + " connection " + connectionId;
			DBLog.otherError(null, message + "  " + error.getMessage());
			logger.log(Level.SEVERE, message, error);
		}
	}

	class TidHandler implements JCoServerTIDHandler {
		public boolean checkTID(JCoServerContext serverCtx, String tid) {
			DBLog.ln(" TidHandler : checkTID + " + tid);
			return true;
		}

		public void confirmTID(JCoServerContext serverCtx, String tid) {
			DBLog.ln(" TidHandler : confirmTID + " + tid);
		}

		public void commit(JCoServerContext serverCtx, String tid) {
			DBLog.ln(" TidHandler : commit + " + tid);
		}

		public void rollback(JCoServerContext serverCtx, String tid) {
			DBLog.ln(" TidHandler : rollback + " + tid);
		}
	}
}
