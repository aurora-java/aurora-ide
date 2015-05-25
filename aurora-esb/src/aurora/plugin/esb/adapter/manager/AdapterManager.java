package aurora.plugin.esb.adapter.manager;

import aurora.plugin.adapter.ws.std.producer.ProducerProcesser;
import aurora.plugin.adapter.ws.std.producer.WSProducerProcesser;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ws.std.consumer.ConsumerProcesser;
import aurora.plugin.esb.adapter.ws.std.consumer.WSConsumerProcesser;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.Producer;

public class AdapterManager {

	public static final String ws = "ws";

//	public static ProducerProcesser getProcessor(Producer o,
//			AuroraEsbContext esbContext) {
//		if (ws.equals(((Producer) o).getType())) {
//			return new WSProducerProcesser((Producer) o, esbContext);
//		}
//		return null;
//	}

//	public static ConsumerProcesser getProcessor(Consumer consumer,
//			AuroraEsbContext esbContext) {
//		if (ws.equals(((Consumer) consumer).getType())) {
//			return new WSConsumerProcesser((Consumer) consumer, esbContext);
//		}
//		return null;
//	}
}
