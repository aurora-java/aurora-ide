package aurora.plugin.esb.adapter;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Producer;

public interface ProducerAdapter {

	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			Producer producer);

	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer);

	String getType();
}
