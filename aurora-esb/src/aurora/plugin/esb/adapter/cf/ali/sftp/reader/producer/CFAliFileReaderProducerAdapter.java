package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ProducerAdapter;
import aurora.plugin.esb.model.Producer;

public class CFAliFileReaderProducerAdapter implements ProducerAdapter {
	
	public static final String cf_ali = "cf.ali.sftp.read"; 

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		return new CFAliFileReaderProducerBuilder(esbContext, producer);
	}

	@Override
	public String getType() {
		return cf_ali;
	}

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return new CFAliFileReaderProducerBuilder(esbContext, producer);
	}
}
