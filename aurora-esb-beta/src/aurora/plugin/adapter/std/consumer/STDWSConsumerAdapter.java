package aurora.plugin.adapter.std.consumer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ConsumerAdapter;

public class STDWSConsumerAdapter implements ConsumerAdapter {
	
	public static final String ws_std = "aurora.std.ws"; 

 
	@Override
	public String getType() {
		return ws_std;
	}

	@Override
	public RouteBuilder createConsumerBuilder(AuroraEsbContext esbContext,
			CompositeMap consumer) {
		return new STDConsumerBuilder(esbContext, consumer);
	}
}
