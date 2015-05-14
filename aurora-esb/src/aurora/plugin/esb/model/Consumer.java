package aurora.plugin.esb.model;

public class Consumer {

	private String name;
	private TO to;
	
	private Producer producer;
	public TO getTo() {
		return to;
	}
	public void setTo(TO to) {
		this.to = to;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Producer getProducer() {
		return producer;
	}
	public void setProducer(Producer producer) {
		this.producer = producer;
	}
}
