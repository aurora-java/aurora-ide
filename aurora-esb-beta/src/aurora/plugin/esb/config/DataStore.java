package aurora.plugin.esb.config;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.ProducerTask;

public class DataStore {


	public DataStore() {
		super();
	}


	public String loadData(AuroraEsbContext esbContext, ProducerTask task) {
		return new FileDataStore().loadData(esbContext, task);
	}

}
