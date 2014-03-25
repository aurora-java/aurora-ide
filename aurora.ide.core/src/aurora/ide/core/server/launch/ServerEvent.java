package aurora.ide.core.server.launch;

import org.eclipse.debug.core.DebugEvent;

public class ServerEvent {
	private DebugEvent debugEvent;
	private int kind;
	private String pjName;
	private ServerInfo serverInfo;

	public DebugEvent getDebugEvent() {
		return debugEvent;
	}

	public void setDebugEvent(DebugEvent debugEvent) {
		this.debugEvent = debugEvent;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public String getPjName() {
		return pjName;
	}

	public void setPjName(String pjName) {
		this.pjName = pjName;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

}
