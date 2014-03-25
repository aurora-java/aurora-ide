package aurora.ide.core.server.launch;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import aurora.ide.project.AuroraProject;

public class AuroraServerManager implements ILaunchConstants {

	static private AuroraServerManager instance = new AuroraServerManager();

	private Map<String, ServerInfo> infos = new HashMap<String, ServerInfo>();

	private List<ServerListener> listeners = new ArrayList<ServerListener>();

	public static final int TERMINATE = DebugEvent.TERMINATE;

	public static final int CREATE = DebugEvent.CREATE;

	private AuroraServerManager() {
		// ILaunchManager launchManager = DebugPlugin.getDefault()
		// .getLaunchManager();
		DebugPlugin.getDefault().addDebugEventListener(
				new IDebugEventSetListener() {

					@Override
					public void handleDebugEvents(DebugEvent[] events) {
						for (DebugEvent debugEvent : events) {
							switch (debugEvent.getKind()) {
							case DebugEvent.CREATE: {
								serverCreate(debugEvent);
								break;
							}
							case DebugEvent.TERMINATE: {
								serverTerminate(debugEvent);
								break;
							}
							}
						}
					}
				});
	}

	protected void serverTerminate(DebugEvent debugEvent) {
		Object source = debugEvent.getSource();
		if (source instanceof org.eclipse.debug.core.model.RuntimeProcess) {
			ILaunchConfiguration launchConfiguration = ((org.eclipse.debug.core.model.RuntimeProcess) source)
					.getLaunch().getLaunchConfiguration();
			try {
				String pjName = launchConfiguration.getAttribute(
						DEPLOY_PROJECT, "");
				ServerInfo remove = infos.remove(pjName);

				ServerEvent serverEvent = new ServerEvent();
				serverEvent.setDebugEvent(debugEvent);
				serverEvent.setKind(DebugEvent.TERMINATE);
				serverEvent.setPjName(pjName);
				serverEvent.setServerInfo(remove);
				this.nodifyListener(serverEvent);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	protected void serverCreate(DebugEvent debugEvent) {
		Object source = debugEvent.getSource();
		if (source instanceof org.eclipse.debug.core.model.RuntimeProcess) {
			ILaunch launch = ((org.eclipse.debug.core.model.RuntimeProcess) source)
					.getLaunch();
			ILaunchConfiguration launchConfiguration = launch
					.getLaunchConfiguration();
			try {
				String pjName = launchConfiguration.getAttribute(
						DEPLOY_PROJECT, "");
				if ("".equals(pjName))
					return;
				int port = launchConfiguration.getAttribute(SERVER_PORT, 8888);
				ServerInfo si = new ServerInfo();
				si.setRuning(true);
				si.setPjName(pjName);
				si.setPort(port);
				si.setLaunch(launch);
				infos.put(pjName, si);

				ServerEvent serverEvent = new ServerEvent();
				serverEvent.setDebugEvent(debugEvent);
				serverEvent.setKind(DebugEvent.CREATE);
				serverEvent.setPjName(pjName);
				serverEvent.setServerInfo(si);
				this.nodifyListener(serverEvent);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void addListener(ServerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ServerListener listener) {
		listeners.remove(listener);
	}

	private void nodifyListener(final ServerEvent event) {

		for (ServerListener lis : listeners) {
			lis.handleEvent(event);
		}

	}

	public int getServerKind(IProject p) {
		ServerInfo serverInfo = this.infos.get(p.getName());
		return serverInfo == null ? TERMINATE : CREATE;
	}

	public ServerInfo getServerInfo(IProject p) {
		ServerInfo serverInfo = this.infos.get(p.getName());
		return serverInfo;
	}

	public static AuroraServerManager getInstance() {
		return instance;
	}

	public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return -1;
	}

	public void terminate(IProject selectionProject) {
		ServerInfo serverInfo = AuroraServerManager.getInstance()
				.getServerInfo(selectionProject);
		if (serverInfo == null)
			return;
		try {
			ILaunch launch = serverInfo.getLaunch();
			if (launch != null && launch.isTerminated() == false)
				launch.terminate();
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

	public String getURL(IFile file) {
		if (file == null)
			return "";
		IProject project = file.getProject();
		ServerInfo serverInfo = this.getServerInfo(project);
		if (serverInfo != null) {
			StringBuilder sb = new StringBuilder("");
//			http://
			sb.append("localhost:");
			sb.append(serverInfo.getPort());
			AuroraProject ap = new AuroraProject(project);
			IPath location = ap.getWeb_home().getLocation();
			IPath location2 = file.getLocation();
			IPath makeRelativeTo = location2.makeRelativeTo(location);
			IPath url = new Path(sb.toString());
			url = url.append(makeRelativeTo);
			return "http://"+ url.toString();

			// http://localhost:8888/pmg/manager.screen
		}
		return "";
	}

}
