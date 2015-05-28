package aurora.plugin.esb;

import org.apache.camel.Exchange;


public class LogBean {

	private String msg, clazzName;
	private AuroraEsbContext esbContext;


	public LogBean(String msg, AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
		this.msg = msg;
	}

	public void log(Exchange exchange) {
		Object header = exchange.getIn().getHeader("CamelFileName");
		esbContext.getmLogger().log(""+header);
	}
}
