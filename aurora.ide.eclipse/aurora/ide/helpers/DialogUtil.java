package aurora.ide.helpers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DialogUtil {

	public static final String MESSAGEBOX_WARNING = getLocaleMessage("messagebox.warning");
	public static final String MESSAGEBOX_ERROR = getLocaleMessage("messagebox.error");
	public static final String MESSAGEBOX_QUESTION = getLocaleMessage("messagebox.question");

	public static void showMessageBox(int style, String title, String message) {
		Shell shell = getShell();
		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText(title);
		messageBox.setMessage(getLocaleMessage(message));
		messageBox.open();
	}

	public static void showWarningMessageBox(String message) {
		showWarningMessageBox(MESSAGEBOX_WARNING, message);
	}

	public static void showWarningMessageBox(String title, String message) {
		Shell shell = getShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK
				| SWT.APPLICATION_MODAL);
		message = LocaleMessage.getString(message);
		messageBox.setText(title);
		messageBox.setMessage(getLocaleMessage(message));
		messageBox.open();
	}

	public static void showErrorMessageBox(String message) {
		showErrorMessageBox(MESSAGEBOX_ERROR, message);
	}

	public static void showErrorMessageBox(String title, String message) {
		Shell shell = getShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK
				| SWT.APPLICATION_MODAL);
		messageBox.setText(title);
		messageBox.setMessage(getLocaleMessage(message));
		messageBox.open();
	}

	public static void logErrorException(Throwable e) {
		logErrorException(MESSAGEBOX_ERROR, e);
	}
	public static void showExceptionMessageBox(Throwable e) {
		logErrorException(e);
		showExceptionMessageBox(MESSAGEBOX_ERROR, e);
	}
	public static void showExceptionMessageBox(final String title,
			final Throwable e) {
		Display current = Display.getCurrent();
		(current == null ? Display.getDefault() : current)
				.asyncExec(new Runnable() {
					public void run() {
						Shell shell = getShell();
						MessageBox messageBox = new MessageBox(shell,
								SWT.ICON_ERROR | SWT.OK | SWT.APPLICATION_MODAL);
						messageBox.setText(title);
						String message = ExceptionUtil
								.getExceptionTraceMessage(e);
						messageBox.setMessage(message);
						messageBox.open();
					}
				});
	}

	public static void logErrorException(final String title,
			final Throwable e) {
			Throwable full = new SystemException(e);
			LogUtil.getInstance().logError("aurora ide ", full);
	}

	public static int showConfirmDialogBox(String message) {
		return showConfirmDialogBox(MESSAGEBOX_QUESTION, message);
	}

	public static int showConfirmDialogBox(String title, String message) {
		Shell shell = getShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL | SWT.APPLICATION_MODAL);
		messageBox.setText(title);
		messageBox.setMessage(getLocaleMessage(message));
		int buttonID = messageBox.open();
		return buttonID;
	}

	private static String getLocaleMessage(String message) {
		return LocaleMessage.getString(message);
	}

	private static Shell getShell() {
		Display current = Display.getCurrent();
		Shell shell = (current == null ? Display.getDefault() : current)
				.getActiveShell();
		if (shell == null) {
			shell = new Shell(current);
		}
		return shell;
	}

}
