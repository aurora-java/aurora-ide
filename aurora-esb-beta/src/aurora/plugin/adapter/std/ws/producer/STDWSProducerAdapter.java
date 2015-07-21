package aurora.plugin.adapter.std.ws.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ProducerAdapter;

public class STDWSProducerAdapter implements ProducerAdapter {
	
	public static final String type = "aurora.std.ws"; 


	@Override
	public String getType() {
		return type;
	}

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return new STDWSProducerBuilder(esbContext, producer);
	}
}
