package aurora.plugin.esb.adapter;

import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Consumer;

public interface ConsumerAdapter {

	public RouteBuilder createConsumerBuilder(AuroraEsbContext esbContext,
			Consumer consumer);
	
	String getType();

}
