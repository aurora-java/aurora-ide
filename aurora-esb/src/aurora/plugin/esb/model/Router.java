package aurora.plugin.esb.model;

public class Router {
	private From from;
	private TO to;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public TO getTo() {
		return to;
	}

	public void setTo(TO to) {
		this.to = to;
	}

}
