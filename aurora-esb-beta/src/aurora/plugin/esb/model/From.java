package aurora.plugin.esb.model;

public class From {
	private String name;
	private String endpoint;
	private String feedbackPoint;
	private String paraText;
	private String userName;
	private String psd;
	private String exchangeID;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getParaText() {
		return paraText;
	}

	public void setParaText(String paraText) {
		this.paraText = paraText;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPsd() {
		return psd;
	}

	public void setPsd(String psd) {
		this.psd = psd;
	}

	public String getFeedbackPoint() {
		return feedbackPoint;
	}

	public void setFeedbackPoint(String feedbackPoint) {
		this.feedbackPoint = feedbackPoint;
	}

	public String getExchangeID() {
		return exchangeID;
	}

	public void setExchangeID(String exchangeID) {
		this.exchangeID = exchangeID;
	}
}
