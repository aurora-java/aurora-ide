package aurora.plugin.esb.console;

import java.util.List;

import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.task.TaskManager;

public class Console {


	private AuroraEsbContext esbContext;

	final private static String start = "start ";

	public Console(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	private String excCmd(String cmd) {
		boolean valid = cmd.startsWith(start);
		if (valid) {
			String taskName = cmd.replaceFirst(start, "");
			List<DirectConfig> directConfigs = esbContext.getDirectConfigs();
			for (DirectConfig directConfig : directConfigs) {
				String name = directConfig.getName();
				if (name.equals(taskName)) {
					directStartTask(directConfig);
					return "TASK " + taskName + " is started.";
				}
			}
			return taskName + " is not exist.";

		}
		return cmd + " is invalid. COMMAND name must be start.";

	}

	public void run(String cmd) {

		String msg = excCmd(cmd);
		System.out.println(msg);

		// System.out.println(cmd);
		// directStartTask();
	}

	private void directStartTask(DirectConfig directConfig) {
		try {

			TaskManager m = new TaskManager(esbContext);
			m.directStartTask(directConfig);

			// try {
			// ProducerTemplate template = context.createProducerTemplate();
			// Router createRouter = task.getRouter();
			// template.sendBody("direct:" + createRouter.getFrom().getName(),
			// createRouter.getFrom().getParaText());
			// } catch (Exception e) {
			// e.printStackTrace();
			// this.updateTaskStatus(task, TaskStatus.ERROR_ON_STARTING);
			// }
			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
