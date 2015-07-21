package aurora.plugin.adapter.std.ftp.download.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ProducerAdapter;

public class STDFtpDownloadProducerAdapter implements ProducerAdapter {
	
	public static final String type = "aurora.std.ftp.download"; 


	@Override
	public String getType() {
		return type;
	}

	@Override
	public RouteBuilder createProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		return new STDFtpDownloadProducerBuilder(esbContext, producer);
	}

}
