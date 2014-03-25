package aurora.ide.core.server.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;

public class AuroraServerLauncher implements ILaunchConstants {

	public static void launch(IProject project) throws CoreException {
		AuroraServerManager.getInstance();
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchManager manager = launchManager;
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType("aurora.server.launchType");
		ILaunchConfiguration[] configurations = manager
				.getLaunchConfigurations(type);
//		for (int i = 0; i < configurations.length; i++) {
//			ILaunchConfiguration configuration = configurations[i];
//			// if (configuration.getName().equals("Start Aurora Server")) {
//			// configuration.delete();
//			// break;
//			// }
//		}
		int findFreePort = AuroraServerManager.findFreePort();

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null,
				"Start Aurora Server on " + findFreePort);
		workingCopy.setAttribute(DEPLOY_PROJECT, project.getName());
		workingCopy.setAttribute(SERVER_PORT, findFreePort);

		ILaunchConfiguration configuration = workingCopy.doSave();
		DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
	}
}
