package aurora.plugin.adapter.std.producer;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import aurora.plugin.esb.model.BusinessModel;

public class ConsumerDispatch {

	private BusinessModel businessModel;

	public ConsumerDispatch(BusinessModel businessModel) {
		this.businessModel = businessModel;
	}

	public void dispatch(Exchange exchange) {

		CamelContext context = exchange.getContext();
		List<String> consumerList = this.getConsumerList();
		for (String string : consumerList) {
			directStartTask(context, string, exchange);
		}

	}

	private void directStartTask(CamelContext context, String consumer,
			Exchange exchange) {

		try {

			ProducerTemplate template = context.createProducerTemplate();
			// Router createRouter = directConfig.getRouter();
			String body = exchange.getIn().getBody(String.class);
			System.out.println(body);
			template.sendBody("direct:" + consumer, body);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<String> getConsumerList() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		return list;
	}
}
