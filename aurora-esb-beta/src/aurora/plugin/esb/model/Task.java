package aurora.plugin.esb.model;

public class Task {

	private String id = Integer.toHexString(hashCode());;
	private String name;
//	private Router router;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getFeedbackTime() {
		return feedbackTime;
	}

	public void setFeedbackTime(long feedbackTime) {
		this.feedbackTime = feedbackTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private long startTime;
	private long endTime;
	private long feedbackTime;
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

//	public Router getRouter() {
//		return router;
//	}
//
//	public void setRouter(Router router) {
//		this.router = router;
//	}

}
