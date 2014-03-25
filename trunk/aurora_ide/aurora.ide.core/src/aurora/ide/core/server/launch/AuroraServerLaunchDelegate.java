package aurora.ide.core.server.launch;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import aurora.ide.core.Activator;
import aurora.ide.project.AuroraProject;

public class AuroraServerLaunchDelegate extends LaunchConfigurationDelegate
		implements ILaunchConstants {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		// #ifdef ex1
		// # // TODO: Exercise 1 - Launch a command shell as a system process to
		// echo "foo"
		// #elseif ex1_answer
		// # Process process = DebugPlugin.exec(new String[]{"cmd", "/C",
		// "\"echo foo\""}, null);
		// # new RuntimeProcess(launch, process, "Hello", null);
		// #else
		String pjName = configuration.getAttribute(DEPLOY_PROJECT, "");
		int port = configuration.getAttribute(SERVER_PORT, 8888);

		if ("".equals(pjName)) {
			return;
		}

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(pjName);
		AuroraProject ap = new AuroraProject(project);
		String deployPath = ap.getWeb_home().getLocation().toString();

		List commandList = new ArrayList();

		// Get Java VM path
		String javaVMHome = System.getProperty("java.home");
		String javaVMExec = javaVMHome + File.separatorChar + "bin"
				+ File.separatorChar + "java";
		if (File.separatorChar == '\\') {
			javaVMExec += ".exe";
		}
		File exe = new File(javaVMExec);
		if (!exe.exists()) {
			abort(MessageFormat.format(
					"Specified java VM executable {0} does not exist.",
					new Object[] { javaVMExec }), null);
		}
		commandList.add(javaVMExec);

		commandList.add("-Dfile.encoding=UTF-8");

		commandList.add("-classpath");
		// commandList.add(File.pathSeparator
		// + DebugCorePlugin.getFileInPlugin(new Path("bin")));
		//
		// commandList.add("-jar");
		// commandList.add(File.pathSeparator
		// + DebugCorePlugin.getFileInPlugin(new Path("lib")));
		File libFolder = Activator.getFileInPlugin(new Path("lib"));
		commandList.add("." + File.pathSeparator + libFolder
				+ "/org.mortbay.jetty.server_6.1.23.v201012071420.jar"
				+ File.pathSeparator + libFolder
				+ "/org.mortbay.jetty.util_6.1.23.v201012071420.jar"
				+ File.pathSeparator + libFolder
				+ "/javax.servlet_2.5.0.v201103041518.jar" + File.pathSeparator
				+ Activator.getFileInPlugin(new Path("bin")));
		// commandList
		// .add(File.pathSeparator
		// +
		// "/Users/shiliyan/Desktop/work/aurora/eclipse3.7_64/plugins/org.mortbay.jetty.server_6.1.23.v201012071420.jar"
		// + File.pathSeparator
		// +
		// "/Users/shiliyan/Desktop/work/aurora/eclipse3.7_64/plugins/org.mortbay.jetty.util_6.1.23.v201012071420.jar"
		// + File.pathSeparator
		// +
		// "/Users/shiliyan/Desktop/work/aurora/eclipse3.7_64/plugins/javax.servlet_2.5.0.v201103041518.jar");

		// commandList.add("-cp");
		// commandList.add(File.pathSeparator
		// + DebugCorePlugin.getFileInPlugin(new Path("bin")));

		commandList.add("aurora.ide.core.server.launch.AuroraServerRunner");

		commandList.add(deployPath);
		// "/Users/shiliyan/Desktop/work/aurora/workspace/runtime-aurora_protypes/preview_test/webRoot");
		commandList.add("/");
		commandList.add("" + port);
		// program name
		// String program =
		// configuration.getAttribute(DebugCorePlugin.ATTR_PDA_PROGRAM,
		// (String)null);
		// if (program == null) {
		// abort("Perl program unspecified.", null);
		// }
		//
		// IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new
		// Path(program));
		// if (!file.exists()) {
		// abort(MessageFormat.format("Perl program {0} does not exist.", new
		// String[] {file.getFullPath().toString()}), null);
		// }
		//
		// commandList.add(file.getLocation().toOSString());

		// if in debug mode, add debug arguments - i.e. '-debug requestPort
		// eventPort'
		int requestPort = -1;
		int eventPort = -1;
		// if (mode.equals(ILaunchManager.DEBUG_MODE)) {
		// requestPort = findFreePort();
		// eventPort = findFreePort();
		// if (requestPort == -1 || eventPort == -1) {
		// abort("Unable to find free port", null);
		// }
		// commandList.add("-debug");
		// commandList.add("" + requestPort);
		// commandList.add("" + eventPort);
		// }

		String[] commandLine = (String[]) commandList
				.toArray(new String[commandList.size()]);
		Process process = DebugPlugin.exec(commandLine, null);

		IProcess p = DebugPlugin.newProcess(launch, process, javaVMExec);
		// if in debug mode, create a debug target
		// if (mode.equals(ILaunchManager.DEBUG_MODE)) {
		// IDebugTarget target = new PDADebugTarget(launch, p, requestPort,
		// eventPort);
		// launch.addDebugTarget(target);
		// }
		// #endif
	}

	private void abort(String message, Throwable e) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, Activator
				.getDefault().getDescriptor().getUniqueIdentifier(), 0,
				message, e));
	}
}
