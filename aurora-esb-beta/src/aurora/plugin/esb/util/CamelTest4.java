package aurora.plugin.esb.util;

import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.Service;
import org.apache.camel.ShutdownRoute;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.spi.RoutePolicyFactory;
import org.apache.camel.spi.RuntimeEndpointRegistry;

import aurora.plugin.adapter.std.consumer.STDConsumerBuilder;
import aurora.plugin.adapter.std.producer.STDProducerBuilder;

public class CamelTest4 {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
//		main.addRouteBuilder(new STDProducerBuilder());

		main.addRouteBuilder(new FTPDownloadProducerBuilder());
		main.addRouteBuilder(new RouteBuilderListener());
		main.enableHangupSupport();
		main.run();
	}

}
