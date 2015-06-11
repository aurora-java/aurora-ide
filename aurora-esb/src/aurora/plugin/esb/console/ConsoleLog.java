package aurora.plugin.esb.console;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import aurora.plugin.esb.AuroraEsbServer;

public class ConsoleLog {

	public void log2Console(Exchange exchange, String status) {
		String msg = "";

		String task_id = (String) exchange.getIn().getHeader("task_id");
		String task_name = (String) exchange.getIn().getHeader("task_name");
		msg = "TASK: {" + task_name + "[" + task_id + "]" + "}"
				+ " STATUS CHANGED " + ">>>>" + status;

		log2Console(msg);
	}

	public void log2Console(String msg) {

		Logger logger = Logger.getLogger(AuroraEsbServer.class);
//		logger.setAdditivity(true);
		// logger.setResourceBundle(bundle);
		// PropertyConfigurator..configure("esb_log4j.properties");
//		logger.debug("Here is DEBUG messgae");
//		logger.info("Here is INFO message");
//		logger.warn("Here is WARN message");
//		logger.error("Here is ERROR message");
//		logger.fatal("Here is FATAL message");
		logger.info( msg);
		System.out.println(new Date() + "  " + msg);
	}
}
