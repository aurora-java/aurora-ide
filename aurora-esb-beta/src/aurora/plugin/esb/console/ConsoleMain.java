package aurora.plugin.esb.console;

import org.apache.camel.Main;
import org.apache.camel.builder.RouteBuilder;

public class ConsoleMain {
	
	

	public static void main(String[] args) {
		Main main = new Main();
		// configure the location of the Spring XML file
		// enable hangup support allows Camel to detect when the JVM is
		// terminated
		main.enableHangupSupport();
		// run and block until Camel is stopped (or JVM terminated)
		try {
			main.addRouteBuilder(new RouteBuilder() {

				@Override
				public void configure() throws Exception {
					// <from uri="stream:in?promptMessage=Enter something: "/>
					// <!-- transform the input to upper case using the simple
					// language -->
					// <!-- you can also use other languages such as groovy,
					// ognl, mvel, javascript etc. -->
					// <transform>
					// <simple>${body.toUpperCase()}</simple>
					// </transform>
					// <!-- and then print to the console -->
					// <to uri="stream:out"/>
					from("stream:in?promptMessage=Enter something: ")
							.transform().simple("${body.toUpperCase()}");
//							.addInterceptStrategy(new InterceptStrategy() {
//								
//								@Override
//								public Processor wrapProcessorInInterceptors(CamelContext context,
//										ProcessorDefinition<?> definition, Processor target,
//										Processor nextTarget) throws Exception {
//									// TODO Auto-generated method stub
//								
////									definition.to("mock:end");
//									return null;
//								}
//							});
				}
			});
			main.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
