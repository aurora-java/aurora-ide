package aurora.plugin.adapter.ws.std.producer;

import java.util.Date;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.AMQMsg;
import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerTask;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.TaskStatus;
import aurora.plugin.esb.task.TaskManager;
import aurora.plugin.esb.ws.WSHelper;

public class WSProducerProcesser extends ProducerProcesser {

	private AuroraEsbContext esbContext;
	private Producer producer;

	public WSProducerProcesser(Producer o, AuroraEsbContext esbContext) {
		super(esbContext);
		this.producer = o;
		this.esbContext = esbContext;
	}

	public void prepareProcess(Exchange exchange) {

		From from = producer.getFrom();
		Map<String, Object> paras = WSHelper
				.createHeaderOptions(from.getUserName(),
						from.getPsd());
		TaskManager tm = new TaskManager(getEsbContext());
		ProducerTask t = tm.createTask(producer);
		t.setStartTime(new Date().getTime());
		t.getFrom().setExchangeID(exchange.getExchangeId());
		tm.saveTask(t);
		paras.put("task_id", t.getId());
		paras.put("task_name", t.getName());
		exchange.getOut().setHeaders(paras);
		exchange.getOut().setBody(exchange.getIn().getBody());
		exchange.getIn().setHeader("task_id", t.getId());
		exchange.getIn().setHeader("task_name", t.getName());
		new ConsoleLog().log2Console(exchange, t.getStatus());
	
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
		Task task = updateTaskStatus(exchange,
				TaskStatus.SERVER_DATA_LOADED);
		exchange.getOut().setBody(exchange.getIn().getBody());
		new ConsoleLog().log2Console(exchange,
				TaskStatus.SERVER_DATA_LOADED);
	
	}

	@Override
	public void dataSavedProcess(Exchange exchange) {


		keepTaskAlive(exchange);

		Task task = updateTaskStatus(exchange,
				TaskStatus.SERVER_WORK_FINISH);
		task.setEndTime(new Date().getTime());
		TaskManager tm = new TaskManager(esbContext);
		tm.updateTask(task);

		AMQMsg msg = new AMQMsg();
		msg.setTask(task);
		exchange.getOut().setBody(AMQMsg.toXML(msg));
		new ConsoleLog().log2Console(exchange,
				TaskStatus.SERVER_WORK_FINISH);
	
	}

	@Override
	public void endProcess(Exchange exchange) {

		String task_id = (String) exchange.getIn().getHeader(
				"task_id");
		String task_name = (String) exchange.getIn().getHeader(
				"task_name");
		TaskManager tm = new TaskManager(esbContext);
		Task task = tm.loadTask(task_id, task_name);
		task.setEndTime(new Date().getTime());
		tm.updateTask(task);
	
	}

}
