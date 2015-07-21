package aurora.plugin.esb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.Service;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class RouteBuilderListener extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		from("timer://foo?period=30000").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				CamelContext context = exchange.getContext();
				List<Route> routes = context.getRoutes();
				for (Route route : routes) {
					ServiceStatus routeStatus = context.getRouteStatus(route
							.getId());
					String description = route.getId();
//					route.addService(new Service(){
//
//						@Override
//						public void start() throws Exception {
//						}
//
//						@Override
//						public void stop() throws Exception {
//							
//						}
//						
//					});
//					route.
//					route.getRouteContext().
//					.getDescription();
					String name = routeStatus.name();
					System.out.println(description);
					System.out.println(name);
				}
				List<RouteDefinition> routeDefinitions = context.getRouteDefinitions();
				for (RouteDefinition routeDefinition : routeDefinitions) {
//					routeDefinition.
				}
			
			}
		});
		// .to(url).process(new Processor() {
		//
		// @Override
		// public void process(Exchange exchange) throws Exception {
		// // exchange.getIn().getHeaders();
		// String body = exchange.getIn().getBody(String.class);
		// System.out.println(body);
		// }
		// });
		// this.defaultErrorHandler();
		// // errorHandler(new DefaultErrorHandler(System.out));

	}

}
