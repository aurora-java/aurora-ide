package aurora.plugin.esb;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerConsumer;
import aurora.plugin.esb.router.builder.ConsumerBuilder;
import aurora.plugin.esb.router.builder.ProducerBuilder;

public class AuroraEsbContext {
	private AuroraEsbServer server;
	private List<DirectConfig> task_configs = new ArrayList<DirectConfig>();
	private DefaultCamelContext context;

	private List<Producer> producers = new ArrayList<Producer>();

	private List<Consumer> consumers = new ArrayList<Consumer>();

	private List<ProducerConsumer> producerConsumer = new ArrayList<ProducerConsumer>();

	//

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

	public void addConsumer(Consumer consumer) {
		// producer.addConsumer(consumer);
		// consumer.setProducer(producer);
		this.consumers.add(consumer);
	}

	public void removeConsumer(Consumer consumer) {
		// consumer.setProducer(null);
		// producer.removeConsumer(consumer);
		this.consumers.remove(consumer);
	}

	public void addProducer(DirectConfig dc) {
		Producer pro = new Producer();
		pro.setName(dc.getName());
		pro.setFrom(dc.getRouter().getFrom());
		this.addProducer(pro);
	}

	public void addConsumer(DirectConfig dc) {
		Consumer co = new Consumer();
		co.setName(dc.getName());
		co.setTo(dc.getRouter().getTo());
		this.addConsumer(co);
	}

	public List<ProducerConsumer> getProducerConsumer() {
		return producerConsumer;
	}

	public void setProducerConsumer(ProducerConsumer producerConsumer) {
		this.producerConsumer.add(producerConsumer);
	}

	public Producer getProducer(String p_name) {
		List<Producer> producers = this.getProducers();
		for (Producer producer : producers) {
			if (p_name.equals(producer.getName())) {
				return producer;
			}
		}
		return null;
	}

	public Consumer getConsumer(String c_name) {
		List<Consumer> consumers = this.getConsumers();
		for (Consumer consumer : consumers) {
			if (c_name.equals(consumer.getName())) {
				return consumer;
			}
		}
		return null;
	}

	public ProducerConsumer getProducerConsumer(String name) {

		List<ProducerConsumer> producerConsumer = this.getProducerConsumer();
		for (ProducerConsumer pc : producerConsumer) {
			if (name.equals(pc.getProducer().getName())) {
				return pc;
			}
		}
		return null;
	}

	public void bind(Producer producer, Consumer consumer) throws Exception {
		ProducerConsumer pc = this.getProducerConsumer(producer.getName());
		if (pc != null) {
			pc.addConsumer(consumer);
			this.context.addRoutes(new ConsumerBuilder(this, consumer));
		}

	}

	public boolean isActive(Producer producer) {
		ProducerConsumer pc = this.getProducerConsumer(producer.getName());
		return pc != null;
	}

	public void bind(Producer producer) throws Exception {
		ProducerConsumer pc = new ProducerConsumer();
		pc.setProducer(producer);
		this.getProducerConsumer().add(pc);
		this.context.addRoutes(new ProducerBuilder(this, producer));
	}

}
