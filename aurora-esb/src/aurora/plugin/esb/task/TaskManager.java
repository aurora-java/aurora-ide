package aurora.plugin.esb.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.ConsumerTask;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerTask;
import aurora.plugin.esb.model.Router;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.TaskStatus;

public class TaskManager {
	private CamelContext context;
	private String workPath = null;
	private AuroraEsbContext esbContext;

	public TaskManager(AuroraEsbContext esbContext) {
		super();
		this.context = esbContext.getCamelContext();
		this.workPath = esbContext.getWorkPath();
		this.esbContext = esbContext;
	}

	public List<Task> getAllTask() {
		List<Task> tasks = new ArrayList<Task>();
		TaskStore s = new TaskStore(workPath);

		String[] taskNames = s.getTaskNames();

		for (String t : taskNames) {
			if (t.equals(".DS_Store"))
				continue;
			List<Task> load = s.load(t);
			tasks.addAll(load);
		}

		return tasks;
	}

	// public Task createTask(DirectConfig config) {
	// // timer task
	// // direct task
	// Task createTask = new Task();
	// createTask.setName(config.getName());
	// createTask.setRouter(config.getRouter());
	// TaskStore s = new TaskStore(workPath);
	// s.save(createTask);
	// return createTask;
	// }

	// public Task createTask(String name) {
	// // timer task
	// // direct task
	// Task createTask = new Task();
	// createTask.setName(name);
	// // createTask.setRouter(config.getRouter());
	// TaskStore s = new TaskStore(workPath);
	// s.save(createTask);
	// return createTask;
	// }

	public void configDirectRouter(DirectConfig config) {
		// Task createTask = Demo.createTask();
		// try {
		// // context.addRoutes(new DirectWSRouteBuilder(config, esbContext));
		// context.addRoutes(new MonitorDirectRouteBuilder(config, esbContext));
		// } catch (Exception e) {
		// e.printStackTrace();
		// // this.updateTaskStatus(createTask, TaskStatus.ERROR_ON_STARTING);
		// }
	}

	public void directStartTask(DirectConfig directConfig) {
		try {
			ProducerTemplate template = context.createProducerTemplate();
			Router createRouter = directConfig.getRouter();
			template.sendBody("direct:" + createRouter.getFrom().getName(),
					createRouter.getFrom().getParaText());
		} catch (Exception e) {
			e.printStackTrace();
			// this.updateTaskStatus(task, TaskStatus.ERROR_ON_STARTING);
		}
	}

	// public void directStartTask(Task task) {
	// try {
	// ProducerTemplate template = context.createProducerTemplate();
	// Router createRouter = task.getRouter();
	// template.sendBody("direct:" + createRouter.getFrom().getName(),
	// createRouter.getFrom().getParaText());
	// } catch (Exception e) {
	// e.printStackTrace();
	// this.updateTaskStatus(task, TaskStatus.ERROR_ON_STARTING);
	// }
	// }

	public void finishTask(Task task) {
		updateTaskStatus(task, TaskStatus.FINISH);
	}

	public void stopTask(Task task) {
		updateTaskStatus(task, TaskStatus.STOPED);
	}

	public void updateTaskStatus(Task task, String status) {
		task.setStatus(status);
		updateTask(task);
	}

	public void updateTask(Task task) {
		new TaskStore(workPath).update(task);
	}

	public Task updateTaskStatus(String task_id, String task_name, String status) {
		Task t = loadTask(task_id, task_name);
		updateTaskStatus(t, status);
		return t;
	}

	public Task loadTask(String task_id, String task_name) {
		return new TaskStore(workPath).getTask(task_id, task_name);
	}

	public ProducerTask createTask(Producer producer) {
		ProducerTask t = new ProducerTask();
		t.setName(producer.getName());
		t.setFrom(producer.getFrom());
		return t;
	}

	public void saveTask(Task t) {
		TaskStore s = new TaskStore(workPath);
		s.save(t);
	}

	public ConsumerTask createTask(Consumer consumer) {
		ConsumerTask t = new ConsumerTask();
		t.setTo(consumer.getTo());
		t.setName(consumer.getName());
		return t;
	}

}
