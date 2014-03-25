package aurora.ide.core.server.launch;

import org.eclipse.debug.core.ILaunch;

public class ServerInfo {

	private int port;
	// private String deployPath;
	// private IProject project;
	// private String dbInfo;

	private ILaunch launch;

	private String pjName;

	private boolean isRuning = false;

	private boolean isDebugStatus = false;

	public boolean isRuning() {
		return isRuning;
	}

	public void setRuning(boolean isRuning) {
		this.isRuning = isRuning;
	}

	public boolean isDebugStatus() {
		return isDebugStatus;
	}

	public void setDebugStatus(boolean isDebugStatus) {
		this.isDebugStatus = isDebugStatus;
	}

	public String getPjName() {
		return pjName;
	}

	public void setPjName(String pjName) {
		this.pjName = pjName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

}
