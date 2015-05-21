package aurora.plugin.esb.config;

import org.apache.camel.model.RouteDefinition;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerTask;
import aurora.plugin.esb.ws.WSHelper;

public class FileDataStore extends DataStore{

	 public RouteDefinition storeData(RouteDefinition begin,
			AuroraEsbContext esbContext, Producer producer) {
		begin = begin.to("file:" + esbContext.getWorkPath()
				+ producer.getName() + "/" + producer.getFrom().getName());
		return begin;

	}


	public  RouteDefinition storeData(RouteDefinition begin,
			AuroraEsbContext esbContext, Consumer consumer) {
		begin = begin.to("file:" + esbContext.getWorkPath()
				+ consumer.getName() + "/" + consumer.getTo().getName());
		return begin;
	}

	public  String loadData(AuroraEsbContext esbContext,ProducerTask task) {
		return WSHelper.loadPara(esbContext.getWorkPath(), task,
				((ProducerTask) task).getFrom());
	}

}
