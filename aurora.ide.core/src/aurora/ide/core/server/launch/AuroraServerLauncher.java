package aurora.ide.core.server.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener;
import org.eclipse.debug.ui.DebugUITools;

public class AuroraServerLauncher {

	public static void launch() throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchManager manager = launchManager;
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType("aurora.server.launchType");
		ILaunchConfiguration[] configurations = manager
				.getLaunchConfigurations(type);
		for (int i = 0; i < configurations.length; i++) {
			ILaunchConfiguration configuration = configurations[i];
			if (configuration.getName().equals("Start Aurora Server")) {
				configuration.delete();
				break;
			}
		}
		// DebugPlugin.getDefault()..getDefault().asyncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// System.out.println("xxxxx");
		// }
		// });
		
		DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
			
			@Override
			public void handleDebugEvents(DebugEvent[] events) {
//				DebugEvent.TERMINATE
				System.out.println(events);
			}
		});
		launchManager.addLaunchListener(new ILaunchListener() {

			@Override
			public void launchRemoved(ILaunch launch) {
//				launch.terminate();
				System.out.println("1 : launchRemoved");
			}

			@Override
			public void launchChanged(ILaunch launch) {
				System.out.println("1 : launchChanged");
			}

			@Override
			public void launchAdded(ILaunch launch) {

				System.out.println("1 :launchAdded ");
			}
		});
		launchManager.addLaunchListener(new ILaunchesListener() {

			@Override
			public void launchesRemoved(ILaunch[] launches) {
				System.out.println("2 :launchesRemoved ");
			}

			@Override
			public void launchesChanged(ILaunch[] launches) {
				System.out.println("2 :launchesChanged ");
			}

			@Override
			public void launchesAdded(ILaunch[] launches) {
				System.out.println("2 :launchesAdded ");

			}
		});
		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null,
				"Start Aurora Server");
		ILaunchConfiguration configuration = workingCopy.doSave();
		DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
	}
}
