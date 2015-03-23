package aurora.ide.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.AuroraPlugin;

public class StatusUtil {
	static String defaultPluginId = AuroraPlugin.PLUGIN_ID;

	/**
	 * create a {@code Status} from a Throwable instance
	 * 
	 * @param thr
	 * @param pluginID
	 *            if {@code null} pased in,then use
	 *            {@code AuroraPlugin.PLUGIN_ID}
	 * @return
	 */
	public static IStatus createStatus(Throwable thr, String pluginID) {
		return new Status(Status.ERROR, getPluginID(pluginID),
				thr.getLocalizedMessage(), thr);
	}

	static String getPluginID(String id) {
		if (id == null)
			return defaultPluginId;
		return id;
	}

	/**
	 * 
	 * @param shell
	 *            can be {@code null}
	 * @param title
	 * @param shortDesc
	 * @param showInCurrentThread
	 *            if dialog can not be opened in current thread,please pased
	 *            {@code false} to prevent
	 *            {@code SWTException: Invalid thread access}
	 * @param status
	 */
	public static void showStatusDialog(final Shell shell, final String title,
			final String shortDesc, boolean showInCurrentThread,
			final IStatus status) {
		if (showInCurrentThread) {
			StatusDialog.openError(shell, title, shortDesc, status);
		} else {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					StatusDialog.openError(shell, title, shortDesc, status);
				}
			});
		}
	}

	/**
	 * @see #createStatus(Throwable, String)
	 * @see #showStatusDialog(Shell, String, String, boolean, IStatus)
	 */
	public static void showExceptionDialog(Shell shell, final String title,
			final String shortDesc, boolean showInCurrentThread, Throwable thr) {
		IStatus detail = createStatus(thr, null);
		showStatusDialog(shell, title, shortDesc, showInCurrentThread, detail);
	}

	/**
	 * create {@code MultiStatus} from {@code IStatus[]}
	 * 
	 * @param pluginID
	 *            if {@code null} pased in,then use
	 *            {@code AuroraPlugin.PLUGIN_ID}
	 * @param msg
	 * @param thr
	 * @param status
	 * @return
	 */
	public static MultiStatus createMultiStatus(String pluginID, String msg,
			Throwable thr, IStatus[] status) {
		return new MultiStatus(getPluginID(pluginID), IStatus.ERROR, status,
				msg, thr);
	}


	/**
	 * Answer a flat collection of the passed status and its recursive children
	 */
	protected static List flatten(IStatus aStatus) {
		List result = new ArrayList();

		if (aStatus.isMultiStatus()) {
			IStatus[] children = aStatus.getChildren();
			for (int i = 0; i < children.length; i++) {
				IStatus currentChild = children[i];
				if (currentChild.isMultiStatus()) {
					Iterator childStatiiEnum = flatten(currentChild).iterator();
					while (childStatiiEnum.hasNext()) {
						result.add(childStatiiEnum.next());
					}
				} else {
					result.add(currentChild);
				}
			}
		} else {
			result.add(aStatus);
		}

		return result;
	}

	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 */
	protected static IStatus newStatus(IStatus[] stati, String message,
			Throwable exception) {

		if (message == null || message.trim().length() == 0) {
			throw new IllegalArgumentException();
		}
		return new MultiStatus(aurora.ide.AuroraPlugin.PLUGIN_ID, IStatus.ERROR,
				stati, message, exception);
	}

	
	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 * @param severity
	 * @param message
	 * @param exception
	 * @return {@link IStatus}
	 */
	public static IStatus newStatus(int severity, String message,
			Throwable exception) {

		String statusMessage = message;
		if (message == null || message.trim().length() == 0) {
			if (exception == null) {
				throw new IllegalArgumentException();
			} else if (exception.getMessage() == null) {
				statusMessage = exception.toString();
			} else {
				statusMessage = exception.getMessage();
			}
		}

		return new Status(severity, aurora.ide.AuroraPlugin.PLUGIN_ID, severity,
				statusMessage, exception);
	}

	
	/**
	 * This method must not be called outside the workbench.
	 * 
	 * Utility method for creating status.
	 * @param children
	 * @param message
	 * @param exception
	 * @return {@link IStatus}
	 */
	public static IStatus newStatus(List children, String message,
			Throwable exception) {

		List flatStatusCollection = new ArrayList();
		Iterator iter = children.iterator();
		while (iter.hasNext()) {
			IStatus currentStatus = (IStatus) iter.next();
			Iterator childrenIter = flatten(currentStatus).iterator();
			while (childrenIter.hasNext()) {
				flatStatusCollection.add(childrenIter.next());
			}
		}

		IStatus[] stati = new IStatus[flatStatusCollection.size()];
		flatStatusCollection.toArray(stati);
		return newStatus(stati, message, exception);
	}

	

}
