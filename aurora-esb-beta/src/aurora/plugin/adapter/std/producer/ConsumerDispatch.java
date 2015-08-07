package aurora.plugin.adapter.std.producer;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

import aurora.plugin.esb.model.BusinessModelConsumer;
import aurora.plugin.esb.model.BusinessModelProducer;
import aurora.plugin.esb.util.BusinessModelUtil;

public class ConsumerDispatch {

	private BusinessModelProducer businessModelProducer;

	public ConsumerDispatch(BusinessModelProducer businessModelProducer) {
		this.businessModelProducer = businessModelProducer;
	}

	public void dispatch(Exchange exchange) {

		CamelContext context = exchange.getContext();
		List<BusinessModelConsumer> consumerList = this.getConsumerList();
		for (BusinessModelConsumer string : consumerList) {
			directStartTask(context, string, exchange);
		}

	}

	private void directStartTask(CamelContext context,
			BusinessModelConsumer consumer, Exchange exchange) {

		try {

			ProducerTemplate template = context.createProducerTemplate();
			// Router createRouter = directConfig.getRouter();
			// String body = exchange.getIn().getBody(String.class);
			// System.out.println(body);
			template.sendBody("direct:" + consumer.getId(), "Start Consumer");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<BusinessModelConsumer> getConsumerList() {
		List<BusinessModelConsumer> businessModelConsumer = BusinessModelUtil
				.getBusinessModelConsumer(businessModelProducer);
		return businessModelConsumer;
	}
}
