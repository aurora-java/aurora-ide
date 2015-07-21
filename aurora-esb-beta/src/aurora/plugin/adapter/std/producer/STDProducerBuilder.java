package aurora.plugin.adapter.std.producer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.data.DataSaveBean;
import aurora.plugin.esb.model.BusinessModel;

public class STDProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private CompositeMap producerMap;


	public STDProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	public STDProducerBuilder() {
	}

	@Override
	public void configure() throws Exception {
		BusinessModel businessModel = new BusinessModel("test");
		from("file:/Users/shiliyan/Desktop/esb/test/files").recipientList().method(new PersistData(businessModel)); 
		
		from("direct:file").bean(new DataSaveBean(esbContext, businessModel),"save2File").to("direct:consumer");
		from("direct:db").bean(new DataSaveBean(esbContext, businessModel),"save2DB").to("direct:consumer");
		
//		BusinessModel.name
		
		from("direct:consumer").bean(new ConsumerDispatch(businessModel),"dispatch");
	}
}
