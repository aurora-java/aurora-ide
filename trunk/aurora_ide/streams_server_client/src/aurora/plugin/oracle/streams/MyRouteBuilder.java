package aurora.plugin.oracle.streams;

import org.apache.camel.builder.RouteBuilder;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {
 
	/**
	 * Let's configure the Camel routing rules using Java code...
	 */
	public void configure() {

		from("aq-jms:queue:STRMADMIN.STREAMS_CACHE_QUEUE").to(
				"amq-jms:logic_change_record");
	}

}
