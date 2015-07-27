package aurora.plugin.adapter.std.ws.consumer;

import org.apache.camel.Exchange;

public class ConsumerHolder {

	public void consumer(Exchange ex) {
		String body = ex.getIn().getBody(String.class);
		System.out.println(body);
	}

}
