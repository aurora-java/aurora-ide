package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.cf.ali.sftp.download.producer.LogBean;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Producer;

public class CFAliFileReaderProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	// private RouteBuilder rb;
	private AuroraEsbContext esbContext;
	// private Router r;
	// private DirectConfig config;
	private Producer producer;

	public CFAliFileReaderProducerBuilder(AuroraEsbContext esbContext,
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
	// noop=true&
	@Override
	public void configure() throws Exception {

		getContext().getShutdownStrategy().setTimeout(10);

		from(
				"file:/Users/shiliyan/Desktop/esb/download"
						+ "/"
						+ "CFCar"
						+ "?"
//						+ "noop=true&idempotent=false&"
						+ "delete=true&"
						+ "delay=100s&recursive=true&charset=euc_cn")
				.bean(new CFAliServiceReader(esbContext), "read")
				.to("file:/Users/shiliyan/Desktop/esb/download/read"
						+ "/"
						+ "CFCar"
						+ "?"
						//+ "noop=true&idempotent=true&delay=5s&"
						+ "recursive=true&charset=euc_cn");
		// &idempotent=true
		// .to("file:/Users/shiliyan/Desktop/esb/download" + "/" + "CFCar")
		// .bean(new LogBean("Downloaded file ${file:name} complete.",
		// esbContext), "log")
		// .log("Downloaded file ${file:name} complete.");
		// noop=true&
		// &move=../
	}
}
