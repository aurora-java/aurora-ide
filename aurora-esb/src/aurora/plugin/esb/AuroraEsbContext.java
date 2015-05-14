package aurora.plugin.esb;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Producer;

public class AuroraEsbContext {
	private AuroraEsbServer server;
	private List<DirectConfig> task_configs = new ArrayList<DirectConfig>();
	private DefaultCamelContext context;

	private List<Producer> producers = new ArrayList<Producer>();
	
	private List<Consumer> consumers = new ArrayList<Consumer>();
	
	
	
	private String workPath = null;

	public AuroraEsbServer getServer() {
		return server;
	}

	public void setServer(AuroraEsbServer server) {
		this.server = server;
	}

	public List<DirectConfig> getDirectConfigs() {
		return task_configs;
	}

	public void addTaskConfig(DirectConfig config) {
		task_configs.add(config);

	}

	public void setCamelContext(DefaultCamelContext context) {
		this.context = context;
	}

	public DefaultCamelContext getCamelContext() {
		return context;
	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		if (workPath.endsWith("/"))
			this.workPath = workPath;
		else
			this.workPath = workPath + "/";
	}

	public List<Producer> getProducers() {
		return producers;
	}

	public void addProducer(Producer producer) {
		this.producers.add(producer);
	}

	public List<Consumer> getConsumers() {
		return consumers;
	}

	public void addConsumer(Producer producer,Consumer consumer) {
		producer.addConsumer(consumer);
		consumer.setProducer(producer);
		this.consumers.add(consumer);
	}
	
	public void removeConsumer(Producer producer,Consumer consumer) {
		consumer.setProducer(null);
		producer.removeConsumer(consumer);
		this.consumers.remove(consumer);
	}
	
	
	

}
