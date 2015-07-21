package aurora.plugin.esb.adapter;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;

public class AbstractProducerAdapter implements ProducerAdapter {


	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}
	
	

}
