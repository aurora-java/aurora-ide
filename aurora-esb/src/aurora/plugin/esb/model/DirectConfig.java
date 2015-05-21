package aurora.plugin.esb.model;

public class DirectConfig {
	private String name;
	private String type;
	private Router router;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Router getRouter() {
		return router;
	}
	public void setRouter(Router router) {
		this.router = router;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
