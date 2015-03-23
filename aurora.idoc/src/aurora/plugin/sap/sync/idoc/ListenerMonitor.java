package aurora.plugin.sap.sync.idoc;


public class ListenerMonitor extends Thread {

	private IDocServerManager idocServerManager;
	private IDocFileListener listener;
	private int minReconnectTime;
	private int maxReconnectTime;

	private int nextReconnectTime = 0;

	public ListenerMonitor(IDocServerManager idocServerManager, IDocFileListener listener) {
		this.idocServerManager = idocServerManager;
		this.listener = listener;
		this.minReconnectTime = idocServerManager.getReconnectTime();
		this.maxReconnectTime = idocServerManager.getMaxReconnectTime();
	}

	public void run() {
		while (idocServerManager.isRunning()) {
			if (listener.isRunning()) {
				nextReconnectTime = minReconnectTime;
				sleepOneSecond();
			} else {
				startServer();
			}
		}
	}

	private void sleepOneSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	private void startServer() {
		int thisReconnectTime = computeConnectTime();
		try {
			Thread.sleep(thisReconnectTime);
		} catch (InterruptedException e) {
		}
		listener.start();
	}

	private int computeConnectTime() {
		int thisReconnectTime = nextReconnectTime;
		if (thisReconnectTime == 0)
			nextReconnectTime = minReconnectTime;
		else {
			if (nextReconnectTime < maxReconnectTime) {
				if (nextReconnectTime * 2 <= maxReconnectTime) {
					nextReconnectTime = nextReconnectTime * 2;
				} else {
					nextReconnectTime = maxReconnectTime;
				}
			}
		}
		return thisReconnectTime;

	}

}
