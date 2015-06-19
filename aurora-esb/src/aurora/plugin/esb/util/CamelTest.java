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

public class CamelTest {

	private static int i = 1;

	private class TestRouter extends RouteBuilder {

		@Override
		public void configure() throws Exception {

			i++;
			RouteDefinition bean = from("timer://foo2" + i + "?period=100000")
					.bean(new CamelTest(), "testRun");

		}

		@Override
		protected void configureRoute(RouteDefinition route) {
			super.configureRoute(route);
			// route.routeId("pppppppppp");
			// route.
//			System.out.println(route.getId());
		}

	}

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.addRouteBuilder(new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				// lets shutdown faster in case of in-flight messages stack up
				// getContext().getShutdownStrategy().setTimeout(10);

				from("timer://foo1?period=50000").bean(new CamelTest(), "run");

			}

		});
		main.enableHangupSupport();
		main.run();
	}

	public void testRun(Exchange exchange) {
		System.out.println("666");
	}

	public void listRoutes(CamelContext context) {
		List<RouteDefinition> routeDefinitions = context.getRouteDefinitions();
		for (RouteDefinition routeDefinition : routeDefinitions) {
			System.out.println(routeDefinition);
		}
		List<Route> routes = context.getRoutes();
		for (Route route : routes) {
			System.out.println(route);
		}
	}

	public void stopRoute(CamelContext context, String id) {
		try {
			context.stopRoute(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startRoute(CamelContext context, String routeId) {
		try {
			context.startRoute(routeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void removeRoute(CamelContext context, String routeId){
		try {
			context.stopRoute(routeId);
			context.removeRoute(routeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run(Exchange exchange) {
		CamelContext context = exchange.getContext();
		this.listRoutes(context);
//		context.getRouteStatus("").
		try {
			context.addRoutes(new TestRouter());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("============");
		this.listRoutes(context);

		removeRoute(context, "route2");
		System.out.println("============");
		this.listRoutes(context);
		this.stopRoute(context, "route2");

		System.out.println("============");
		this.listRoutes(context);

		this.startRoute(context, "route2");

		System.out.println("============");
		this.listRoutes(context);

	}

}
