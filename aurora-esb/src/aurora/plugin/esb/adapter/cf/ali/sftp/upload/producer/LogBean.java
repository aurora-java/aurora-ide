package aurora.plugin.esb.adapter.cf.ali.sftp.upload.producer;

import java.util.logging.Level;

import org.apache.camel.Exchange;

import aurora.plugin.esb.AuroraEsbContext;
import uncertain.logging.ILogger;

public class LogBean {

	private AuroraEsbContext esbContext;

	public LogBean(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	public void log(Exchange exchange) {
		Object header = exchange.getIn().getHeader("CamelFileName");

		ILogger logger = esbContext.getmLogger();
		String msg = "Uploaded file " + header + " complete.";
		logger.log(Level.SEVERE, "" + msg);
	}
}
