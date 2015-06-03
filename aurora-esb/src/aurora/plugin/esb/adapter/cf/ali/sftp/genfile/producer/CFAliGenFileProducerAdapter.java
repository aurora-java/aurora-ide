package aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ProducerAdapter;
import aurora.plugin.esb.model.Producer;

public class CFAliGenFileProducerAdapter implements ProducerAdapter {
	
	public static final String cf_ali = "cf.ali.sftp.gen.file"; 

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		return new CFAliGenFileProducerBuilder(esbContext, producer);
	}

	@Override
	public String getType() {
		return cf_ali;
	}

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return new CFAliGenFileProducerBuilder(esbContext, producer);
	}
}
