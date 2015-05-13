package aurora.plugin.esb.model;

public class Task {

	private String id = Integer.toHexString(hashCode());;
	private String name;
	private Router router;

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getFeedbackTime() {
		return feedbackTime;
	}

	public void setFeedbackTime(int feedbackTime) {
		this.feedbackTime = feedbackTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private int startTime;
	private int endTime;
	private int feedbackTime;
	private String status = TaskStatus.STARTING;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

}
