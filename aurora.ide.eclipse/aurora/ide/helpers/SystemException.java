package aurora.ide.helpers;

public class SystemException extends ApplicationException {
	private static final long serialVersionUID = 5893583058631870043L;
	public final static String MessageDesc="Error occurred in system call.";
	public SystemException() {
		super(MessageDesc);
	}

	public SystemException(String message) {
		super(MessageDesc+message);
	}

	public SystemException(String message, Throwable cause) {
		super(MessageDesc+message,cause);
	}
	public SystemException(Throwable cause) {
		super(MessageDesc,cause);
	}
}
