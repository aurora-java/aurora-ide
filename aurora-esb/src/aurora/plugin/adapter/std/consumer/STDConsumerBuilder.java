package aurora.plugin.adapter.std.consumer;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.BusinessModel;
import aurora.plugin.esb.model.Producer;

public class STDConsumerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private Producer producer;
	private CompositeMap producerMap;

	private String name;

	public STDConsumerBuilder(AuroraEsbContext esbContext, Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	public STDConsumerBuilder(AuroraEsbContext esbContext, CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	public STDConsumerBuilder(String name) {
		this.name = name;
	}

	@Override
	public void configure() throws Exception {

		BusinessModel businessModel = new BusinessModel("test");

		from("direct:" + name).bean(new Consumer(),"consumer");
		//
		// from("seda:file").bean(new
		// DataSaveBean(),"save2File").to("seda:consumer");
		// from("seda:db").bean(new
		// DataSaveBean(),"save2DB").to("seda:consumer");
		//
		// // BusinessModel.name
		//
		// from("seda:consumer").bean("");
	}
}
