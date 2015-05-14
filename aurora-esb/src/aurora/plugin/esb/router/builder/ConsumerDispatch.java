package aurora.plugin.esb.router.builder;

import java.util.List;

import org.apache.camel.Exchange;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.model.Producer;

public class ConsumerDispatch {	
	private AuroraEsbContext esbContext;

	public ConsumerDispatch(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	public void dispatch(Exchange exchange) {
		List<Producer> producers = esbContext.getProducers();
//		esbContext.getCamelContext();
		
	}

}
