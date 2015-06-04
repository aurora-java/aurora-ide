package aurora.plugin.esb.console;

import java.util.Date;

import org.apache.camel.Exchange;

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
		System.out.println(new Date() + "  " + msg);
	}
}
