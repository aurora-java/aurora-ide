package aurora.plugin.adapter.std.ftp.upload.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ProducerAdapter;
import aurora.plugin.esb.model.Producer;

public class STDFtpUploadProducerAdapter implements ProducerAdapter {
	
	public static final String type = "aurora.std.ftp.upload"; 

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		return new STDFtpUploadProducerBuilder(esbContext, producer);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return new STDFtpUploadProducerBuilder(esbContext, producer);
	}
}
