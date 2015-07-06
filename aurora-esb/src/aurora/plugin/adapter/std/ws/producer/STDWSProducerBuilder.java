package aurora.plugin.adapter.std.ws.producer;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.adapter.std.producer.PersistData;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.data.DataSaveBean;
import aurora.plugin.esb.model.BusinessModel;
import aurora.plugin.esb.model.Demo;
import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.ws.WSHelper;

public class STDWSProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private Producer producer;
	private CompositeMap producerMap;

	public STDWSProducerBuilder(AuroraEsbContext esbContext, Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	public STDWSProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	public STDWSProducerBuilder() {
	}

	@Override
	public void configure() throws Exception {
		final From f = Demo.createFrom();
		BusinessModel businessModel = new BusinessModel("test");
//		from("timer://foo?period=30000")
//				.process(new Processor() {
//
//					@Override
//					public void process(Exchange exchange) throws Exception {
//						Map<String, Object> paras = WSHelper
//								.createHeaderOptions(f.getUserName(),
//										f.getPsd());
//						exchange.getOut().setHeaders(paras);
//						exchange.getOut().setBody(f.getParaText());
//					}
//				}).to(f.getEndpoint()).recipientList()
//				.method(new PersistData(businessModel));
		//
		// from("direct:file").bean(new
		// DataSaveBean(),"save2File").to("direct:consumer");
		from(
		// "direct:db"

				"timer://foo?period=30000").bean(
				new DataSaveBean(esbContext, businessModel), "save2DB");
		// .to(
		// "direct:consumer");
		//
		// // BusinessModel.name
		//
		// from("direct:consumer").bean(new
		// ConsumerDispatch(businessModel),"dispatch");
	}
}
