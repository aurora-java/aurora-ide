package aurora.plugin.esb.router.builder.consumer;

import java.util.Date;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.AMQMsg;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.ConsumerTask;
import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerTask;
import aurora.plugin.esb.model.TO;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.TaskStatus;
import aurora.plugin.esb.task.TaskManager;
import aurora.plugin.esb.ws.WSHelper;

public class WSConsumerProcesser extends ConsumerProcesser {

	private AuroraEsbContext esbContext;
	private Consumer consumer;

	public WSConsumerProcesser(Consumer o, AuroraEsbContext esbContext) {
		super(esbContext);
		this.consumer = o;
		this.esbContext = esbContext;
	}
	
	

	public void prepareProcess(Exchange exchange) {
		TO to = consumer.getTo();
		Map<String, Object> paras = WSHelper
				.createHeaderOptions(to.getUserName(),
						to.getPsd());


		TaskManager tm = new TaskManager(esbContext);
		ConsumerTask t = tm.createTask(consumer);
	
		t.setStartTime(new Date().getTime());
		t.getTo().setExchangeID(exchange.getExchangeId());
		tm.saveTask(t);
//		tm.updateTask(t);
		paras.put("task_id", t.getId());	
		paras.put("task_name", t.getName());
		exchange.getIn().setHeader("task_id", t.getId());
		exchange.getIn().setHeader("task_name", t.getName());
		exchange.getOut().setHeaders(paras);
		exchange.getOut().setBody(exchange.getIn().getBody());
		new ConsoleLog().log2Console(exchange,
				TaskStatus.INVOKE_CLIENT_POINT);

	
	}

	public AuroraEsbContext getEsbContext() {
		return esbContext;
	}

	public void setEsbContext(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	@Override
	public void dataLoadedProcess(Exchange exchange) {

		keepTaskAlive(exchange);
		updateTaskStatus(exchange, TaskStatus.CLIENT_RESPONSED);
		exchange.getOut().setBody(exchange.getIn().getBody());
		new ConsoleLog().log2Console(exchange, TaskStatus.CLIENT_RESPONSED);
	
	}

	@Override
	public void dataSavedProcess(Exchange exchange) {
		keepTaskAlive(exchange);
		Task task = updateTaskStatus(exchange,
				TaskStatus.CLIENT_RESPONSED_SAVED);

		AMQMsg msg = new AMQMsg();
		msg.setTask(task);
		exchange.getOut().setBody(AMQMsg.toXML(msg));
		new ConsoleLog().log2Console(exchange,
				TaskStatus.CLIENT_RESPONSED_SAVED);
	
	}

	@Override
	public void endProcess(Exchange exchange) {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String task_id = (String) exchange.getIn().getHeader(
				"task_id");
		String task_name = (String) exchange.getIn().getHeader(
				"task_name");
		TaskManager tm = new TaskManager(esbContext);
		Task task = tm.loadTask(task_id, task_name);
		task.setEndTime(new Date().getTime());
		task.setStatus(TaskStatus.FINISH);
		tm.updateTask(task);

		new ConsoleLog().log2Console(exchange, TaskStatus.FINISH);
	
	}

}
