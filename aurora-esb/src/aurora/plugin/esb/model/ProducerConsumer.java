package aurora.plugin.esb.model;

import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeMap;

public class ProducerConsumer {

	private Producer producer;
	private List<Consumer> consumers = new ArrayList<Consumer>();
	private CompositeMap producerMap;

	public List<Consumer> getConsumers() {
		return consumers;
	}

	public void addConsumer(Consumer consumer) {
		this.consumers.add(consumer);
	}

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}


	public CompositeMap getProducerMap() {
		return producerMap;
	}

	public void setProducerMap(CompositeMap producerMap) {
		this.producerMap = producerMap;
	}
}
