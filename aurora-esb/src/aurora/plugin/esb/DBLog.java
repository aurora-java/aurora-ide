package aurora.plugin.esb;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.console.ConsoleLog;

public class DBLog {

	private AuroraEsbContext esbContext;

	private ConsoleLog clog = new ConsoleLog();

	public DBLog(AuroraEsbContext esbContext) {
		super();
		this.esbContext = esbContext;
	}

	public void log(String message) {
		String log_proc = esbContext.getLog_proc();
		if (log_proc == null || "".equals(log_proc))
			return;
		CompositeMap header = new CompositeMap("result");

		header.put("file_error_msg".toLowerCase(), message);

		try {
			CompositeMap executeProc = esbContext.executeProc(log_proc, header);
			// return executeProc;
		} catch (Exception e) {
			String msg = e.getMessage();
			log("[LOG ERROR ] " + " errorMSG: " + msg);
			clog.log2Console("[LOG ERROR ]  " + " errorMSG: " + msg);
			// e.printStackTrace();
			return;
		}

	}
}
