package aurora.plugin.esb.router.builder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Router;

public class MonitorDirectRouteBuilder extends RouteBuilder {

	private Router r;
	private DirectConfig config;
	private CamelContext context;
	private ConsoleLog clog = new ConsoleLog();
	private String workPath;
	private AuroraEsbContext esbContext;

	// static private String path = "/Users/shiliyan/Desktop/esb/";

	// get server data
	// save to file
	// send msg to amq

	// get amq msg
	// get local data

	// send data to client
	// get response
	// save to file
	// send msg to amq

	// feedback to server

	public MonitorDirectRouteBuilder(DirectConfig config,
			AuroraEsbContext esbContext) {
		this.config = config;
		this.r = config.getRouter();
		this.context = esbContext.getCamelContext();
		workPath = esbContext.getWorkPath();
		this.esbContext = esbContext;
	}

	private void msgBuilder() {
		MidMsgBuilder mmb = new MidMsgBuilder(this, esbContext, r);
		mmb.createBuilder();
	}

	private void producerBuilder() {
		ProducerBuilder pb = new ProducerBuilder(this,esbContext,r,config);
		pb.createBuilder();
	}

	private void consumerBuilder() {
		ConsumerBuilder cb = new ConsumerBuilder(this, esbContext, r, config);
		cb.createBuilder();
	}

	@Override
	public void configure() throws Exception {
		producerBuilder();
		msgBuilder();
		consumerBuilder();
	}

}
