package aurora.plugin.esb.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class RouteCheckBuilder extends RouteBuilder {

	private Map<String, RouteStatusChecker> routeCheckers = new HashMap<String, RouteStatusChecker>();

	private long routeCheckPeriod = 30000;

	@Override
	public void configure() throws Exception {
		routeStatusCheckConfig();
	}

	public void routeStatusCheckConfig() {

		if (routeCheckers.size() == 0)
			return;

		from("timer://foo?period=" + routeCheckPeriod).process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				CamelContext context = exchange.getContext();

				Set<String> keySet = routeCheckers.keySet();
				for (String string : keySet) {
					boolean error = routeCheckers.get(string).isError();
					if (error) {
						context.stopRoute(string);
						context.startRoute(string);
					}
				}
			}
		});
	}

	public void addRouteChecker(String routeId, RouteStatusChecker checker) {
		routeCheckers.put(routeId, checker);
	}

	
	public void removeRouteChecker(String routeId, RouteStatusChecker checker) {
		routeCheckers.remove(routeId, checker);
	}

	public long getRouteCheckPeriod() {
		return routeCheckPeriod;
	}

	public void setRouteCheckPeriod(long routeCheckPeriod) {
		this.routeCheckPeriod = routeCheckPeriod;
	}

}
