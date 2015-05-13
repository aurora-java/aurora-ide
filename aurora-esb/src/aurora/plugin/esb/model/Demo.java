package aurora.plugin.esb.model;

public class Demo {

	public static String para = "<soapenv:Envelope"
			+ " xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
			+ " >" + "<soapenv:Body><in  a=\"c\">Hello</in>"
			+ "</soapenv:Body>" + "</soapenv:Envelope>";

	public static Task createTask(){
		Task t = new Task();
		t.setId("task_id");
		t.setName("task_name");
		t.setRouter(createRouter());
		return t;
	}
	public static DirectConfig createDirectConfig(){
		DirectConfig t = new DirectConfig();
		t.setName("task_name");
		t.setRouter(createRouter());
		return t;
	}
	
	public static Router createRouter() {
		Router r = new Router();
		r.setName("demo");
		r.setFrom(createFrom());
		r.setTo(createTO());
		return r;
	}

	static public From createFrom() {
		From r = new From();
		r.setName("getData");
		r.setEndpoint("http://localhost:8888/HAP_DBI/ws/query.svc");
		r.setUserName("userName");
		r.setParaText(para);
		r.setPsd("psd");
		r.setFeedbackPoint("http://localhost:8888/HAP_DBI/ws/query.svc");
		return r;
	}

	static public TO createTO() {
		TO r = new TO();
		r.setName("sendData");
		r.setEndpoint("http://localhost:8888/HAP_DBI/ws/save.svc");
		r.setUserName("userName");
		r.setParaText(para);
		r.setPsd("psd");
		return r;
	}
	
//	static public String toXML(AMQMsg msg) {
//		return "amq msg";
//	}
//
//	static public AMQMsg toObject(String xml) {
//		AMQMsg amqMsg = new AMQMsg();
//		amqMsg.setTask(createTask());
//		return amqMsg;
//	}

}
