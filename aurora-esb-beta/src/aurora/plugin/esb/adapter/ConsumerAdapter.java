package aurora.plugin.esb.adapter;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;

public interface ConsumerAdapter {

	
	public RouteBuilder createConsumerBuilder(AuroraEsbContext esbContext,
			CompositeMap consumer);
	
	String getType();

}
