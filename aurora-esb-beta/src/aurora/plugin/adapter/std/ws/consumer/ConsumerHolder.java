package aurora.plugin.adapter.std.ws.consumer;

import java.util.List;

import org.apache.camel.Exchange;

import aurora.plugin.esb.model.BusinessModelConsumer;
import aurora.plugin.esb.model.BusinessModelData;
import aurora.plugin.esb.util.BusinessModelUtil;

public class ConsumerHolder {

	private BusinessModelConsumer businessModelConsumer ;
	public ConsumerHolder(BusinessModelConsumer businessModelConsumer) {
		this.businessModelConsumer= businessModelConsumer;
	}

	
	public void consumer(Exchange ex) {
		List<BusinessModelData> consumer = BusinessModelUtil.consumer(businessModelConsumer);
		
		for (BusinessModelData businessModelData : consumer) {
			System.out.println(businessModelData);
		}
		
	}

}
