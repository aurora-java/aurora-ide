package aurora.plugin.sap.sync.idoc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.Statement;

public class AuroraIDocException extends Exception {
	private static final long serialVersionUID = -3184478964424768398L;

	public AuroraIDocException() {
		super("Error occurred in aurora idoc application.");
	}

	public AuroraIDocException(String message) {
		super(message);
	}

	public AuroraIDocException(String message, Throwable cause) {
		super(message,cause);
	}
	public AuroraIDocException(Throwable cause) {
		super(cause);
	}
	
	public static AuroraIDocException createSQLException(String sql, SQLException e) {
		if (sql != null)
			return new AuroraIDocException("execute sql:" + sql + " failed.", e);
		return new AuroraIDocException(e);
	}

	public static AuroraIDocException createStatementException(Statement statement, SQLException e) {
		if (statement != null)
			return new AuroraIDocException("execute sql:" + statement.toString() + " failed.", e);
		return new AuroraIDocException(e);
	}

	public static AuroraIDocException createStatementsException(Statement[] statements, SQLException e) {
		if (statements != null) {
			for (int i = 0; i < statements.length; i++) {
				Statement statement = statements[i];
				if (statement != null)
					return new AuroraIDocException("execute sql:" + statement.toString() + " failed.", e);
			}
		}
		return new AuroraIDocException(e);
	}
	
	public static String getExceptionStackTrace(Throwable exception) {
		if (exception == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pw = new PrintStream(baos);
		exception.printStackTrace(pw);
		pw.close();
		return baos.toString();
	}
}
