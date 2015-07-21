package aurora.plugin.esb.adapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public abstract class AbstractRouteBuilder extends RouteBuilder {

	private Map<String, RouteStatusChecker> routeCheckers = new HashMap<String, RouteStatusChecker>();

	private String routeCheckPeriod = "30000";

	@Override
	public void configure() throws Exception {
		config();
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

	abstract public void config();

	public String getRouteCheckPeriod() {
		return routeCheckPeriod;
	}

	public void setRouteCheckPeriod(String routeCheckPeriod) {
		this.routeCheckPeriod = routeCheckPeriod;
	}

}
