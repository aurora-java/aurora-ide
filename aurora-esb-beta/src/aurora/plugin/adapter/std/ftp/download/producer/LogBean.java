package aurora.plugin.adapter.std.ftp.download.producer;

import java.util.logging.Level;

import org.apache.camel.Exchange;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import uncertain.logging.ILogger;

public class LogBean {

	private AuroraEsbContext esbContext;

	public LogBean(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	public void log(Exchange exchange) {
		Object header = exchange.getIn().getHeader("CamelFileName");

		ILogger logger = esbContext.getmLogger();
		String msg = "[Downloaded File] " 
				+ "Downloaded File " + header + " Complete.";
		logger.log(Level.SEVERE, "" + msg);
		new ConsoleLog().log2Console(msg);
	}
}
