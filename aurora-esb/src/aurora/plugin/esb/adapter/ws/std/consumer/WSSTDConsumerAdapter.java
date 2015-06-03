package aurora.plugin.esb.adapter.ws.std.consumer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ConsumerAdapter;
import aurora.plugin.esb.model.Consumer;

public class WSSTDConsumerAdapter implements ConsumerAdapter {
	
	public static final String ws_std = "ws.std"; 

	@Override
	public RouteBuilder createConsumerBuilder(AuroraEsbContext esbContext,
			Consumer consumer) {
		return new ConsumerBuilder(esbContext, consumer);
	}

	@Override
	public String getType() {
		return ws_std;
	}

	@Override
	public RouteBuilder createConsumerBuilder(AuroraEsbContext esbContext,
			CompositeMap consumer) {
		// TODO Auto-generated method stub
		return null;
	}
}
