package aurora.plugin.esb.adapter;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.Producer;

public class AdapterManager {

	private Map<String, ProducerAdapter> producerAdapters = new HashMap<String, ProducerAdapter>();

	private Map<String, ConsumerAdapter> consumerAdapters = new HashMap<String, ConsumerAdapter>();

	public void registry(ProducerAdapter ad) {
		producerAdapters.put(ad.getType(), ad);
	}

	public RouteBuilder createRouteBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		String type = producer.getType();
		ProducerAdapter producerAdapter = producerAdapters.get(type);
		if (producerAdapter != null)
			return producerAdapter.createProducerBuilder(esbContext, producer);
		return null;
	}

	public void registry(ConsumerAdapter cd) {
		consumerAdapters.put(cd.getType(), cd);
	}

	public RouteBuilder createRouteBuilder(AuroraEsbContext esbContext,
			Consumer consumer) {
		String type = consumer.getType();
		ConsumerAdapter consumerAdapter = consumerAdapters.get(type);
		if (consumerAdapter != null)
			return consumerAdapter.createConsumerBuilder(esbContext, consumer);
		return null;
	}
	// public static ProducerProcesser getProcessor(Producer o,
	// AuroraEsbContext esbContext) {
	// if (ws.equals(((Producer) o).getType())) {
	// return new WSProducerProcesser((Producer) o, esbContext);
	// }
	// return null;
	// }

	// public static ConsumerProcesser getProcessor(Consumer consumer,
	// AuroraEsbContext esbContext) {
	// if (ws.equals(((Consumer) consumer).getType())) {
	// return new WSConsumerProcesser((Consumer) consumer, esbContext);
	// }
	// return null;
	// }
}
