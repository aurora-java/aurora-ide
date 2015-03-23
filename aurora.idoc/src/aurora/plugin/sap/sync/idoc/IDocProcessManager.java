package aurora.plugin.sap.sync.idoc;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import uncertain.core.ILifeCycle;
import uncertain.logging.ILogger;

public class IDocProcessManager implements ILifeCycle {

	private IDocServerManager serverManager;
	private ILogger logger;

	private ExecutorService idocServerThreadMananger;

	public LinkedList<IDocFile> syncFileList = new LinkedList<IDocFile>();
	public LinkedList<IDocFile> feedbackFileList = new LinkedList<IDocFile>();
	public LinkedList<IDocFile> backupFileList = new LinkedList<IDocFile>();
	public LinkedList<IDocFile> deleteFileList = new LinkedList<IDocFile>();

	private boolean keepIdocFile = true;
	private boolean interfaceEnabledFlag = true;

	public IDocProcessManager(IDocServerManager serverManager) {
		this.serverManager = serverManager;
		this.logger = serverManager.getLogger();
		// this.keepIdocFile = serverManager.isKeepIdocFile();
		this.interfaceEnabledFlag = serverManager.isInterfaceEnabledFlag();
	}

	@Override
	public boolean startup() {
		int threadCount = getThreadCount();
		idocServerThreadMananger = Executors.newFixedThreadPool(threadCount);

		SyncProcessor sync = new SyncProcessor(serverManager, this);
		idocServerThreadMananger.execute(sync);

		FeedbackProcessor feedback = new FeedbackProcessor(serverManager, this);
		idocServerThreadMananger.execute(feedback);

		if (interfaceEnabledFlag) {
			BackupToInterfaceProcessor backup = new BackupToInterfaceProcessor(
					serverManager, this);
			idocServerThreadMananger.execute(backup);
		}
		if (!keepIdocFile) {
			DeleteIDocFileProcessor delete = new DeleteIDocFileProcessor();
			idocServerThreadMananger.execute(delete);
		}
		DeleteIDocFileProcessorBYCreateTime d = new DeleteIDocFileProcessorBYCreateTime();
		idocServerThreadMananger.execute(d);
		return true;
	}

	@Override
	public void shutdown() {
		List<Runnable> threadList = idocServerThreadMananger.shutdownNow();
		for (Runnable thread : threadList) {
			if (thread instanceof ILifeCycle) {
				((ILifeCycle) thread).shutdown();
			} else {
				logger.log(Level.SEVERE, "thread " + thread.toString()
						+ " can not shutdown!");
			}
		}
	}

	public synchronized void syncDone(IDocFile idocFile, boolean successful) {
		if (successful) {
			if (interfaceEnabledFlag)
				addBackupFile(idocFile);
			else {
				freeResource(idocFile);
			}
		} else {
			idocFile.clear();
		}
		addFeedbackFile(idocFile);
	}

	public synchronized void backupDone(IDocFile idocFile, boolean successful) {
		if (successful)
			freeResource(idocFile);
		else
			idocFile.clear();
	}

	public synchronized void freeResource(IDocFile idocFile) {
		idocFile.clear();
		if (!keepIdocFile)
			addDeleteFile(idocFile);
	}

	private int getThreadCount() {
		int threadCount = 2;
		if (interfaceEnabledFlag)
			threadCount++;
		if (!keepIdocFile)
			threadCount++;
		threadCount++;
		return threadCount;
	}

	public synchronized boolean isSyncFileEmpty() {
		int size = syncFileList.size();
		if (size == 0)
			return true;
		return false;
	}

	public synchronized void addSyncFileAll(List<IDocFile> idocFileList) {
		syncFileList.addAll(idocFileList);
	}

	public synchronized void addSyncFile(IDocFile idocFile) {
		syncFileList.addLast(idocFile);
	}

	public synchronized IDocFile pollSyncFile() {
		IDocFile idocFile = syncFileList.poll();
		return idocFile;
	}

	public synchronized void addFeedbackFile(IDocFile idocFile) {
		feedbackFileList.addLast(idocFile);
	}

	public synchronized IDocFile pollFeedbackFile() {
		IDocFile idocFile = feedbackFileList.poll();
		return idocFile;
	}

	private synchronized void addBackupFile(IDocFile idocFile) {
		backupFileList.addLast(idocFile);
	}

	public synchronized IDocFile pollBackupFile() {
		IDocFile idocFile = backupFileList.poll();
		return idocFile;
	}

	private synchronized void addDeleteFile(IDocFile idocFile) {
		deleteFileList.addLast(idocFile);
	}

	private synchronized IDocFile pollDeleteFile() {
		IDocFile idocFile = deleteFileList.poll();
		return idocFile;
	}

	class DeleteIDocFileProcessor implements Runnable {
		@Override
		public void run() {
			while (serverManager.isRunning()) {
				if (!serverManager.isKeepIdocFile()) {
					IDocFile idocFile = pollDeleteFile();
					if (idocFile == null) {
						sleepOneSecond();
						continue;
					}
					File deleteFile = new File(idocFile.getFileFullPath());
					if (deleteFile.exists()) {
						logger.log("delete file " + idocFile.getFileFullPath()
								+ " " + deleteFile.delete());
					}
				}
			}

		}

		private void sleepOneSecond() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	class DeleteIDocFileProcessorBYCreateTime implements Runnable {
		@Override
		public void run() {
			while (serverManager.isRunning()) {
				String idocFileDir = serverManager.getIdocFileDir();
				int day = 1000*60*60*24 ;
				long keep = serverManager.getKeepIdocFileTime() * day;
				File dir = new File(idocFileDir);
				File[] listFiles = dir.listFiles();
				for (File file : listFiles) {
					
					if (file.lastModified() + keep < new Date().getTime()) {
						File deleteFile = file;
						if (deleteFile.exists()) {
							logger.log("delete file " + deleteFile.getPath()
									+ " " + deleteFile.delete());
						}
					}
				}
				sleepOneSecond();
			}

		}

		private void sleepOneSecond() {
			try {
				Thread.sleep(1000*60);
			} catch (InterruptedException e) {
			}
		}
	}
}
