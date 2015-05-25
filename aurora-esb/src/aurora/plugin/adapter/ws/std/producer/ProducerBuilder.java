package aurora.plugin.adapter.ws.std.producer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.AdapterManager;
import aurora.plugin.esb.config.DataStore;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.Producer;

public class ProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private Producer producer;

	// private Router r;
	// private DirectConfig config;

	public ProducerBuilder(AuroraEsbContext esbContext, Producer producer) {
		this.esbContext = esbContext;
		// this.r = r;
		this.producer = producer;
		// this.config = config;
	}

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
		From from = producer.getFrom();
		{
			RouteDefinition begin = from("direct:" + from.getName());
			final ProducerProcesser processor = new WSProducerProcesser(
					producer, esbContext);

			// AdapterManager.getProcessor(
			// producer, esbContext);
			if (processor != null) {
				begin = begin.process(new Processor() {

					@Override
					public void process(Exchange arg0) throws Exception {
						processor.prepareProcess(arg0);
					}

				});
				begin = begin.to("" + from.getEndpoint()).process(
						new Processor() {

							@Override
							public void process(Exchange arg0) throws Exception {
								processor.dataLoadedProcess(arg0);
							}
						});
				// store file
				begin = esbContext.getDataStore().storeData(begin, esbContext,
						producer);
				begin.process(new Processor() {
					public void process(Exchange arg0) throws Exception {
						processor.dataSavedProcess(arg0);
					}
				});

				begin.to("test-jms:get_data_record").process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						processor.endProcess(exchange);
					}
				});
			}

		}

		// // webservice
		// // save result
		// // amq notice
		// from("direct:" + from.getName())
		// .process(new Processor() {
		//
		// @Override
		// public void process(Exchange arg0) throws Exception {
		// Map<String, Object> paras = WSHelper
		// .createHeaderOptions(from.getUserName(),
		// from.getPsd());
		// TaskManager tm = new TaskManager(esbContext);
		// ProducerTask t = tm.createTask(producer);
		// // Router router = new Router();
		// // router.setName(producer.getName());
		// // router.setFrom(producer.getFrom());
		// // t.setRouter(router);
		// t.setStartTime(new Date().getTime());
		// t.getFrom().setExchangeID(arg0.getExchangeId());
		// tm.saveTask(t);
		// // tm.updateTask(t);
		// paras.put("task_id", t.getId());
		// paras.put("task_name", t.getName());
		// arg0.getOut().setHeaders(paras);
		// arg0.getOut().setBody(arg0.getIn().getBody());
		// arg0.getIn().setHeader("task_id", t.getId());
		// arg0.getIn().setHeader("task_name", t.getName());
		// clog.log2Console(arg0, t.getStatus());
		// }
		// })
		// .to("" + from.getEndpoint())
		// .process(new Processor() {
		//
		// @Override
		// public void process(Exchange exchange) throws Exception {
		//
		// keepTaskAlive(exchange);
		// Task task = updateTaskStatus(exchange,
		// TaskStatus.SERVER_DATA_LOADED);
		// exchange.getOut().setBody(exchange.getIn().getBody());
		// clog.log2Console(exchange,
		// TaskStatus.SERVER_DATA_LOADED);
		// }
		// })
		// .to("file:" + esbContext.getWorkPath() + producer.getName()
		// + "/" + from.getName()).process(new Processor() {
		//
		// @Override
		// public void process(Exchange exchange) throws Exception {
		//
		// keepTaskAlive(exchange);
		//
		// Task task = updateTaskStatus(exchange,
		// TaskStatus.SERVER_WORK_FINISH);
		// task.setEndTime(new Date().getTime());
		// TaskManager tm = new TaskManager(esbContext);
		// tm.updateTask(task);
		//
		// AMQMsg msg = new AMQMsg();
		// msg.setTask(task);
		// exchange.getOut().setBody(AMQMsg.toXML(msg));
		// clog.log2Console(exchange,
		// TaskStatus.SERVER_WORK_FINISH);
		// }
		// }).to("test-jms:get_data_record").process(new Processor() {
		//
		// @Override
		// public void process(Exchange exchange) throws Exception {
		// String task_id = (String) exchange.getIn().getHeader(
		// "task_id");
		// String task_name = (String) exchange.getIn().getHeader(
		// "task_name");
		// TaskManager tm = new TaskManager(esbContext);
		// Task task = tm.loadTask(task_id, task_name);
		// task.setEndTime(new Date().getTime());
		// tm.updateTask(task);
		// }
		// });
	}
}
