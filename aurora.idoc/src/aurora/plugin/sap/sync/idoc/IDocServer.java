package aurora.plugin.sap.sync.idoc;

import uncertain.core.ILifeCycle;

public class IDocServer implements ILifeCycle {

	private IDocServerManager serverManager;
	private String serverName;

	private IDocProcessManager iDocProcessManager;
	private IDocFileListener iDocFileListener;
	private ListenerMonitor monitor;

	public IDocServer(IDocServerManager serverManager, String serverName) {
		this.serverManager = serverManager;
		this.serverName = serverName;
	}

	@Override
	public boolean startup() {
		iDocProcessManager = new IDocProcessManager(serverManager);
		iDocProcessManager.startup();

		iDocFileListener = new IDocFileListener(serverManager, serverName, iDocProcessManager);

		monitor = new ListenerMonitor(serverManager, iDocFileListener);
		monitor.setDaemon(true);
		monitor.start();
		
		return true;
	}

	@Override
	public void shutdown() {
		shutdownMonitor();
		shutdownListener();
		shutdownProcessManager();
	}

	private void shutdownProcessManager() {
		if (iDocProcessManager != null)
			iDocProcessManager.shutdown();
	}

	private void shutdownListener() {
		if (iDocFileListener != null)
			iDocFileListener.shutdown();
	}

	private void shutdownMonitor() {
		if (monitor != null)
			monitor.interrupt();
	}

	public String getServerName() {
		return serverName;
	}
}