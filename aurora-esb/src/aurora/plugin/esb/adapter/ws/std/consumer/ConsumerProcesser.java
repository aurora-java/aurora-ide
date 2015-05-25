package aurora.plugin.esb.adapter.ws.std.consumer;

import org.apache.camel.Exchange;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.task.TaskManager;

public abstract class ConsumerProcesser {
	private AuroraEsbContext esbContext;

	public ConsumerProcesser(AuroraEsbContext esbContext) {
		super();
		this.esbContext = esbContext;
	}

	abstract public void prepareProcess(Exchange exchange);
	
	abstract public void dataLoadedProcess(Exchange exchange);
	
	abstract public void dataSavedProcess(Exchange exchange);
	
	abstract public void endProcess(Exchange exchange);
	
	public Task updateTaskStatus(Exchange exchange, String status) {
		String task_id = (String) exchange.getIn().getHeader("task_id");
		String task_name = (String) exchange.getIn().getHeader("task_name");
		TaskManager tm = new TaskManager(esbContext);
		Task task = tm.updateTaskStatus(task_id, task_name, status);
		return task;
	}

	public void keepTaskAlive(Exchange exchange) {

		String task_id = (String) exchange.getIn().getHeader("task_id");
		String task_name = (String) exchange.getIn().getHeader("task_name");
		exchange.getOut().setHeader("task_id", task_id);
		exchange.getOut().setHeader("task_name", task_name);
	}

	
}
