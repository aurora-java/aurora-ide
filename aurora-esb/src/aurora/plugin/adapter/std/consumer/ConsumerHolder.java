package aurora.plugin.adapter.std.consumer;

import org.apache.camel.Exchange;

public class ConsumerHolder {

	public void consumer(Exchange ex) {
		String body = ex.getIn().getBody(String.class);
		System.out.println(body);
	}

}
