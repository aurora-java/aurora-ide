package aurora.ide.helpers;

public class ExceptionUtil {

	public static Throwable getRootCause(Throwable e) {
		Throwable cause = e.getCause();
		if (cause == null)
			return e;
		return getRootCause(cause);
	}
	public static String getExceptionTraceMessage(Throwable e) {
		if (e == null)
			return null;
		String messageSeparator = "系统提示：" + AuroraResourceUtil.LineSeparator;
		String causeMessage = "";
		String tipMessage = "";
		if (e instanceof ApplicationException) {
			tipMessage = tipMessage + getMessage(e) + AuroraResourceUtil.LineSeparator;
		} else {
			causeMessage = getMessage(e) + AuroraResourceUtil.LineSeparator;
		}
		Throwable parent = e;
		Throwable child;
		while ((child = parent.getCause()) != null) {
			if (child instanceof ApplicationException) {
				tipMessage = tipMessage + getMessage(child) + AuroraResourceUtil.LineSeparator;
			} else {
				// just the root causeMessage is enough.
				causeMessage = getMessage(child) + AuroraResourceUtil.LineSeparator;
			}
			parent = child;
		}
		String message = causeMessage + (!"".equals(tipMessage) ? messageSeparator + tipMessage : "");
		return message;
	}

	private static String getMessage(Throwable e) {
		if (e instanceof ApplicationException) {
			return e.getLocalizedMessage();
		} else {
			return e.toString();
		}
	}
}
