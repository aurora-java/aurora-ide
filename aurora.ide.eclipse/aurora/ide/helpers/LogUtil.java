package aurora.ide.helpers;


import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

import aurora.ide.AuroraPlugin;


public class LogUtil {

	private static LogUtil instance = null;

	private ILog logger = null;

	private LogUtil() {
		logger = AuroraPlugin.getDefault().getLog();
	}

	public static LogUtil getInstance() {
		if (instance == null) {
			instance = new LogUtil();
		}

		return instance;
	}

	public void log(int severity, String message, Throwable exception) {
		logger.log(new Status(severity, AuroraPlugin.PLUGIN_ID, Status.OK,
				message, exception));
	}

	public void logCancel(String message, Throwable exception) {
		logger.log(new Status(Status.CANCEL, AuroraPlugin.PLUGIN_ID, Status.OK,
				message, exception));
	}

	public void logError(String message, Throwable exception) {
		logger.log(new Status(Status.ERROR, AuroraPlugin.PLUGIN_ID, Status.OK,
				message, exception));
	}

	public void logInfo(String message, Throwable exception) {
		logger.log(new Status(Status.INFO, AuroraPlugin.PLUGIN_ID, Status.OK,
				message, exception));
	}

	public void logOk(String message, Throwable exception) {
		logger.log(new Status(Status.OK, AuroraPlugin.PLUGIN_ID, Status.OK,
				message, exception));
	}

	public void logWarning(String message, Throwable exception) {
		logger.log(new Status(Status.WARNING, AuroraPlugin.PLUGIN_ID, Status.OK,
				message, exception));
	}
}
