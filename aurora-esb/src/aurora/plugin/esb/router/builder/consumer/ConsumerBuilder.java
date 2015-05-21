package aurora.plugin.esb.router.builder.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.config.DataStore;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.TO;
import aurora.plugin.esb.router.processer.ProcessorManager;

public class ConsumerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	// private RouteBuilder rb;
	private AuroraEsbContext esbContext;
	// private Router r;
	// private DirectConfig config;
	private Consumer consumer;

	public ConsumerBuilder(AuroraEsbContext esbContext, Consumer consumer) {
		this.esbContext = esbContext;
		this.consumer = consumer;
	}

	// public ConsumerBuilder(RouteBuilder rb, AuroraEsbContext esbContext,
	// Router r, DirectConfig config) {
	// this.rb = rb;
	// this.esbContext = esbContext;
	// this.r = r;
	// this.config = config;
	// }

//	private Task updateTaskStatus(Exchange exchange, String status) {
//		String task_id = (String) exchange.getIn().getHeader("task_id");
//		String task_name = (String) exchange.getIn().getHeader("task_name");
//		TaskManager tm = new TaskManager(esbContext);
//		Task task = tm.updateTaskStatus(task_id, task_name, status);
//		return task;
//	}
//
//	private void keepTaskAlive(Exchange exchange) {
//
//		String task_id = (String) exchange.getIn().getHeader("task_id");
//		String task_name = (String) exchange.getIn().getHeader("task_name");
//		exchange.getOut().setHeader("task_id", task_id);
//		exchange.getOut().setHeader("task_name", task_name);
//	}

	@Override
	public void configure() throws Exception {
		TO to = consumer.getTo();
		
		{
			RouteDefinition begin = from("direct:" + to.getName());
			
			final ConsumerProcesser processor = ProcessorManager.getProcessor(
					consumer, esbContext);
			if (processor != null) {
				begin = begin.process(new Processor() {
					public void process(Exchange arg0) throws Exception {
						processor.prepareProcess(arg0);
					}
				});
				
				begin = begin.to("" + to.getEndpoint()).process(
						new Processor() {

							@Override
							public void process(Exchange arg0) throws Exception {
								processor.dataLoadedProcess(arg0);
							}
						});
				begin = esbContext.getDataStore().storeData(begin, esbContext, consumer);
				begin.process(
						new Processor() {
							public void process(Exchange arg0) throws Exception {
								processor.dataSavedProcess(arg0);
							}
						});
				begin.to("test-jms:send_data_record").process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						processor.endProcess(exchange);
					}
				});
			}
			
		}
		
		
		
//		
//		from("direct:" + to.getName())
//				.process(new Processor() {
//
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						Map<String, Object> paras = WSHelper
//								.createHeaderOptions(to.getUserName(),
//										to.getPsd());
//
//
//						TaskManager tm = new TaskManager(esbContext);
//						ConsumerTask t = tm.createTask(consumer);
//					
//						t.setStartTime(new Date().getTime());
//						t.getTo().setExchangeID(exchange.getExchangeId());
//						tm.saveTask(t);
////						tm.updateTask(t);
//						paras.put("task_id", t.getId());	
//						paras.put("task_name", t.getName());
//						exchange.getIn().setHeader("task_id", t.getId());
//						exchange.getIn().setHeader("task_name", t.getName());
//						exchange.getOut().setHeaders(paras);
//						exchange.getOut().setBody(exchange.getIn().getBody());
//						clog.log2Console(exchange,
//								TaskStatus.INVOKE_CLIENT_POINT);
//
//					}
//				})
//				.to("" + to.getEndpoint())
//				.process(new Processor() {
//
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						keepTaskAlive(exchange);
//						updateTaskStatus(exchange, TaskStatus.CLIENT_RESPONSED);
//						exchange.getOut().setBody(exchange.getIn().getBody());
//						clog.log2Console(exchange, TaskStatus.CLIENT_RESPONSED);
//					}
//				})
//				.to("file:" + this.esbContext.getWorkPath()
//						+ consumer.getName() + "/" + to.getName())
//				.process(new Processor() {
//
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						keepTaskAlive(exchange);
//						Task task = updateTaskStatus(exchange,
//								TaskStatus.CLIENT_RESPONSED_SAVED);
//
//						AMQMsg msg = new AMQMsg();
//						msg.setTask(task);
//						exchange.getOut().setBody(AMQMsg.toXML(msg));
//						clog.log2Console(exchange,
//								TaskStatus.CLIENT_RESPONSED_SAVED);
//					}
//				}).to("test-jms:send_data_record").process(new Processor() {
//
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						Thread.sleep(1000);
//						String task_id = (String) exchange.getIn().getHeader(
//								"task_id");
//						String task_name = (String) exchange.getIn().getHeader(
//								"task_name");
//						TaskManager tm = new TaskManager(esbContext);
//						Task task = tm.loadTask(task_id, task_name);
//						task.setEndTime(new Date().getTime());
//						task.setStatus(TaskStatus.FINISH);
//						tm.updateTask(task);
//
//						clog.log2Console(exchange, TaskStatus.FINISH);
//					}
//				});
	}
}
