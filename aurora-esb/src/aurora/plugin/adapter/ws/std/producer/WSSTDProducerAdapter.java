package aurora.plugin.adapter.ws.std.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ProducerAdapter;
import aurora.plugin.esb.model.Producer;

public class WSSTDProducerAdapter implements ProducerAdapter {
	
	public static final String ws_std = "ws.std"; 

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		return new ProducerBuilder(esbContext, producer);
	}

	@Override
	public String getType() {
		return ws_std;
	}

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return null;
	}

}
