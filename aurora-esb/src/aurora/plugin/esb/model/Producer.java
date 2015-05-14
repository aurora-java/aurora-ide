package aurora.plugin.esb.model;

import java.util.ArrayList;
import java.util.List;

public class Producer {
	private String name;
	private From from;

	private List<Consumer> consumers = new ArrayList<Consumer>();

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Consumer> getConsumers() {
		return consumers;
	}

	public void addConsumer(Consumer consumer) {
//		consumer.setProducer(this);
		this.consumers.add(consumer);
	}

	public void removeConsumer(Consumer consumer) {
//		consumer.setProducer(null);
		this.consumers.remove(consumer);
	}
}
