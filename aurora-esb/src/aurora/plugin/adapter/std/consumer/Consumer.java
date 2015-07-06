package aurora.plugin.adapter.std.consumer;

import org.apache.camel.Exchange;

public class Consumer {

	public void consumer(Exchange ex) {
		String body = ex.getIn().getBody(String.class);
		System.out.println(body);
	}

}
