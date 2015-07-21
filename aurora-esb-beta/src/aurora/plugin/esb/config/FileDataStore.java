package aurora.plugin.esb.config;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.ProducerTask;
import aurora.plugin.esb.ws.WSHelper;

public class FileDataStore extends DataStore{


	public  String loadData(AuroraEsbContext esbContext,ProducerTask task) {
		return WSHelper.loadPara(esbContext.getWorkPath(), task,
				((ProducerTask) task).getFrom());
	}

}
