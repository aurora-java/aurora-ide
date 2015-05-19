package aurora.plugin.esb.router.builder;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.AMQMsg;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Router;
import aurora.plugin.esb.model.TO;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.TaskStatus;
import aurora.plugin.esb.task.TaskManager;
import aurora.plugin.esb.ws.WSHelper;

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

	private Task updateTaskStatus(Exchange exchange, String status) {
		String task_id = (String) exchange.getIn().getHeader("task_id");
		String task_name = (String) exchange.getIn().getHeader("task_name");
		TaskManager tm = new TaskManager(esbContext);
		Task task = tm.updateTaskStatus(task_id, task_name, status);
		return task;
	}

	private void keepTaskAlive(Exchange exchange) {

		String task_id = (String) exchange.getIn().getHeader("task_id");
		String task_name = (String) exchange.getIn().getHeader("task_name");
		exchange.getOut().setHeader("task_id", task_id);
		exchange.getOut().setHeader("task_name", task_name);
	}

	@Override
	public void configure() throws Exception {
		TO to = consumer.getTo();
		from("direct:" + to.getName())
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						Map<String, Object> paras = WSHelper
								.createHeaderOptions(to.getUserName(),
										to.getPsd());

						// String task_id = (String) exchange.getIn().getHeader(
						// "task_id");
						// String task_name = (String)
						// exchange.getIn().getHeader(
						// "task_name");
						// paras.put("task_id", task_id);
						// paras.put("task_name", task_name);

						
						// Task task = updateTaskStatus(exchange,
						// TaskStatus.INVOKE_CLIENT_POINT);
						// task.getRouter().getTo()
						// .setExchangeID(exchange.getExchangeId());
						// tm.updateTask(task);

						TaskManager tm = new TaskManager(esbContext);
						// TaskManager tm = new TaskManager(esbContext);
						Task t = tm.createTask(consumer.getName());
						Router router = new Router();
						router.setName(consumer.getName());
						router.setTo(consumer.getTo());
						// router.setFrom(producer.getFrom());
						t.setRouter(router);
						t.getRouter().getTo()
								.setExchangeID(exchange.getExchangeId());

						tm.updateTask(t);
						paras.put("task_id", t.getId());
						paras.put("task_name", t.getName());
						exchange.getIn().setHeader("task_id", t.getId());
						exchange.getIn().setHeader("task_name", t.getName());
						exchange.getOut().setHeaders(paras);
						exchange.getOut().setBody(exchange.getIn().getBody());
						clog.log2Console(exchange,
								TaskStatus.INVOKE_CLIENT_POINT);

					}
				})
				.to("" + to.getEndpoint())
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						keepTaskAlive(exchange);
						updateTaskStatus(exchange, TaskStatus.CLIENT_RESPONSED);
						exchange.getOut().setBody(exchange.getIn().getBody());
						clog.log2Console(exchange, TaskStatus.CLIENT_RESPONSED);
					}
				})
				.to("file:" + this.esbContext.getWorkPath()
						+ consumer.getName() + "/" + to.getName())
				.process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						keepTaskAlive(exchange);
						Task task = updateTaskStatus(exchange,
								TaskStatus.CLIENT_RESPONSED_SAVED);

						AMQMsg msg = new AMQMsg();
						msg.setTask(task);
						exchange.getOut().setBody(AMQMsg.toXML(msg));
						clog.log2Console(exchange,
								TaskStatus.CLIENT_RESPONSED_SAVED);
					}
				}).to("test-jms:send_data_record").process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						Thread.sleep(1000);
						Task task = updateTaskStatus(exchange,
								TaskStatus.FINISH);
						clog.log2Console(exchange, TaskStatus.FINISH);
					}
				});
	}
}
