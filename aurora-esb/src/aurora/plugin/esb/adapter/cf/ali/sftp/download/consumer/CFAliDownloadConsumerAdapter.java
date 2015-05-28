package aurora.plugin.esb.adapter.cf.ali.sftp.download.consumer;

import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.ConsumerAdapter;
import aurora.plugin.esb.model.Consumer;

public class CFAliDownloadConsumerAdapter implements ConsumerAdapter {
	
	public static final String cf_ali = "cf.ali.sftp.download"; 

	@Override
	public RouteBuilder createConsumerBuilder(AuroraEsbContext esbContext,
			Consumer consumer) {
		return new CFAliDownloadConsumerBuilder(esbContext, consumer);
	}

	@Override
	public String getType() {
		return cf_ali;
	}
}
