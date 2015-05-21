package aurora.plugin.esb.config;

import org.apache.camel.model.RouteDefinition;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerTask;

public class DataStore {


	public DataStore() {
		super();
	}

	public RouteDefinition storeData(RouteDefinition begin,
			AuroraEsbContext esbContext, Producer producer) {
		return new FileDataStore().storeData(begin, esbContext, producer);

	}

	public RouteDefinition storeData(RouteDefinition begin,
			AuroraEsbContext esbContext, Consumer consumer) {
		return new FileDataStore().storeData(begin, esbContext, consumer);
	}

	public String loadData(AuroraEsbContext esbContext, ProducerTask task) {
		return new FileDataStore().loadData(esbContext, task);
	}

}
