package aurora.plugin.esb.router.processer;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.router.builder.consumer.ConsumerProcesser;
import aurora.plugin.esb.router.builder.consumer.WSConsumerProcesser;
import aurora.plugin.esb.router.builder.producer.ProducerProcesser;
import aurora.plugin.esb.router.builder.producer.WSProducerProcesser;

public class ProcessorManager {

	public static final String ws = "ws";

	public static ProducerProcesser getProcessor(Producer o,
			AuroraEsbContext esbContext) {
		if (ws.equals(((Producer) o).getType())) {
			return new WSProducerProcesser((Producer) o, esbContext);
		}
		return null;
	}

	public static ConsumerProcesser getProcessor(Consumer consumer,
			AuroraEsbContext esbContext) {
		if (ws.equals(((Consumer) consumer).getType())) {
			return new WSConsumerProcesser((Consumer) consumer, esbContext);
		}
		return null;
	}
}
