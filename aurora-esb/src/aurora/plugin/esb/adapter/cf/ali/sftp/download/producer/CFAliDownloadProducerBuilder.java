package aurora.plugin.esb.adapter.cf.ali.sftp.download.producer;

import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Producer;

public class CFAliDownloadProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	// private RouteBuilder rb;
	private AuroraEsbContext esbContext;
	// private Router r;
	// private DirectConfig config;
	private Producer producer;

	public CFAliDownloadProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	// public ConsumerBuilder(RouteBuilder rb, AuroraEsbContext esbContext,
	// Router r, DirectConfig config) {
	// this.rb = rb;
	// this.esbContext = esbContext;
	// this.r = r;
	// this.config = config;
	// }

	// private Task updateTaskStatus(Exchange exchange, String status) {
	// String task_id = (String) exchange.getIn().getHeader("task_id");
	// String task_name = (String) exchange.getIn().getHeader("task_name");
	// TaskManager tm = new TaskManager(esbContext);
	// Task task = tm.updateTaskStatus(task_id, task_name, status);
	// return task;
	// }
	//
	// private void keepTaskAlive(Exchange exchange) {
	//
	// String task_id = (String) exchange.getIn().getHeader("task_id");
	// String task_name = (String) exchange.getIn().getHeader("task_name");
	// exchange.getOut().setHeader("task_id", task_id);
	// exchange.getOut().setHeader("task_name", task_name);
	// }

	@Override
	public void configure() throws Exception {

		String ftp_server_url = "sftp://115.124.16.69:22/"
				+ "download"
				+ "/"
				+ "CFCar"
				+ "?username=cfcar&password=123456&noop=true&delay=100s&recursive=true"
				// + "&charset=utf-8"
				+ "";
		// ftp.client=sftp://115.124.16.69:22/mypath?username=cfcar&password=123456&noop=true
		// #idempotent=true
		//
		// # for the server we want to delay 5 seconds between polling the
		// server
		// # and move downloaded files to a done sub directory
		// ftp.server={{ftp.client}}&delay=100s

		// lets shutdown faster in case of in-flight messages stack up
		getContext().getShutdownStrategy().setTimeout(10);

		from(ftp_server_url)
				.to("file:/Users/shiliyan/Desktop/esb/download" + "/" + "CFCar"
				// + "?charset=utf-8"
						+ "").bean(new LogBean(esbContext), "log")
				.log("Downloaded file ${file:name} complete.");
	}
}
