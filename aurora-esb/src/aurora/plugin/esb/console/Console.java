package aurora.plugin.esb.console;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerConsumer;
import aurora.plugin.esb.task.TaskManager;

public class Console {

	private AuroraEsbContext esbContext;

	final private static String start = "start ";
	final private static String producer = "producer ";
	final private static String consumer = "consumer ";
	
	final private static String test = "test";

	final private static String list = "list";

	public Console(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	private String excCmd(String cmd) {
		boolean istest = cmd.startsWith(test);
		if (istest) {
			return test(cmd);
		}
		boolean isStart = cmd.startsWith(start);
		if (isStart) {
			return start(cmd);
		}
		boolean isProducer = cmd.startsWith(producer);
		if (isProducer) {
			return bindProducer(cmd);
		}
		boolean isConsumer = cmd.startsWith(consumer);
		if (isConsumer) {
			return bindConsumer(cmd);
		}
		boolean isList = cmd.startsWith(list);
		if (isList) {
			return list(cmd);
		}
		return cmd + " is an invalid command.";

	}

	private String test(String cmd) {
		if(test.equals(cmd)){
			System.out.println("============bindProducer==========");
			
			bindProducer("producer task_name");
			
			
			System.out.println("============bindConsumer==========");
			this.bindConsumer("consumer task_name consumer1");
			
			System.out.println("============startTask==========");
			this.start("start task_name");
			
		}
		
		
		
		return null;
	}

	private String list(String cmd) {

		List<Producer> producers = esbContext.getProducers();
		System.out.println("============Producer==============");
		for (Producer producer : producers) {
			System.out.println(producer.getName());
		}
		List<Consumer> consumers = esbContext.getConsumers();
		System.out.println("============Consumer==============");
		for (Consumer consumer : consumers) {
			System.out.println(consumer.getName());
		}
		return "";
	}

	private String bindProducer(String cmd) {
		String p = cmd.replaceFirst(producer, "");
		p = p.trim();
		Producer producer = esbContext.getProducer(p);
		if (producer == null) {
			return "producer " + p + " is not exist.";
		}
		boolean active = esbContext.isActive(producer);
		if (active == true) {
			return "producer " + p + " is active.";
		}
		try {
			esbContext.bind(producer);
		} catch (Exception e) {
			 e.printStackTrace();
			return "error " + e.getMessage();
		}
		return p + " is activing.";
	}

	private String bindConsumer(String cmd) {

		String producer_consumer = cmd.replaceFirst(consumer, "");
		String[] split = producer_consumer.split(" ");
		if (split.length == 2) {
			// ;
			Producer producer = esbContext.getProducer(split[0]);
			if (producer == null) {
				return "producer " + split[0] + " is not exist.";
			}
			boolean active = esbContext.isActive(producer);
			if (active == false) {
				return "producer " + split[0] + " is not active.";
			}

			Consumer consumer = esbContext.getConsumer(split[1]);
			if (consumer == null) {
				return "consumer " + split[1] + " is not exist.";
			}
			try {
				esbContext.bind(producer, consumer);
				return split[1] + " is activing.";
			} catch (Exception e) {
				 e.printStackTrace();
				return "error " + e.getMessage();
			}
		}
		return cmd + " is invalid.";
	}

	public String start(String cmd) {
		String taskName = cmd.replaceFirst(start, "");
		Producer producer = esbContext.getProducer(taskName);
		if (producer == null) {

			return taskName + " is not exist.";
		} else {
			directStartTask(producer);
			return "TASK " + taskName + " is started.";
		}

		// List<DirectConfig> directConfigs = esbContext.getDirectConfigs();
		// for (DirectConfig directConfig : directConfigs) {
		// String name = directConfig.getName();
		// if (name.equals(taskName)) {
		// directStartTask(directConfig);
		// return "TASK " + taskName + " is started.";
		// }
		// }
		// return taskName + " is not exist.";
	}

	private void directStartTask(Producer producer) {

		try {

			ProducerTemplate template = esbContext.getCamelContext()
					.createProducerTemplate();
			// Router createRouter = directConfig.getRouter();
			template.sendBody("direct:" + producer.getFrom().getName(), producer
					.getFrom().getParaText());
			Thread.sleep(2000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run(String cmd) {

		String msg = excCmd(cmd);
		if (msg != null && ("".equals(msg.trim()) == false))
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
