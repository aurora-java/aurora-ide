package aurora.plugin.esb.router.builder;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.AMQMsg;
import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.Router;
import aurora.plugin.esb.model.TO;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.TaskStatus;
import aurora.plugin.esb.task.TaskManager;
import aurora.plugin.esb.ws.WSHelper;

public class MidMsgBuilder {

	private RouteBuilder rb;
	private AuroraEsbContext esbContext;
	private Router r;
	private ConsoleLog clog = new ConsoleLog();

	public MidMsgBuilder(RouteBuilder rb, AuroraEsbContext esbContext,Router r) {
		this.rb = rb;
		this.esbContext = esbContext;
		this.r = r;
	}

	public void createBuilder() {

		TO to = r.getTo();
		rb.from("test-jms:get_data_record").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				String body = exchange.getIn().getBody(String.class);
				AMQMsg object = AMQMsg.toObject(body);
				Task task = object.getTask();
				exchange.getOut().setHeader("task_id", task.getId());
				exchange.getOut().setHeader("task_name", task.getName());

				exchange.getOut().setBody(
						WSHelper.loadPara(esbContext.getWorkPath(), task, task
								.getRouter().getFrom()));
			}

		}).to("direct:" + to.getName());
		From from = r.getFrom();
		if (from.getFeedbackPoint() != null
				&& "".equals(from.getFeedbackPoint().trim()) == false) {
			rb.from("test-jms:send_data_record").process(new Processor() {

				@Override
				public void process(Exchange arg0) throws Exception {
					Map<String, Object> paras = WSHelper.createHeaderOptions(
							from.getUserName(), from.getPsd());

					String body = arg0.getIn().getBody(String.class);
					AMQMsg object = AMQMsg.toObject(body);
					Task task = object.getTask();

					paras.put("task_id", task.getId());
					paras.put("task_name", task.getName());

					arg0.getOut().setHeaders(paras);

					arg0.getOut().setBody(
							WSHelper.loadPara(esbContext.getWorkPath(), task,
									task.getRouter().getTo()));

					updateTaskStatus(arg0, TaskStatus.FEEDBACKED);
					 clog.log2Console(arg0, TaskStatus.FEEDBACKED);
				}
			}).to(from.getFeedbackPoint());
		}

	}

	private Task updateTaskStatus(Exchange exchange, String status) {
		String task_id = (String) exchange.getIn().getHeader("task_id");
		String task_name = (String) exchange.getIn().getHeader("task_name");
		TaskManager tm = new TaskManager(esbContext);
		Task task = tm.updateTaskStatus(task_id, task_name, status);
		return task;
	}
}
