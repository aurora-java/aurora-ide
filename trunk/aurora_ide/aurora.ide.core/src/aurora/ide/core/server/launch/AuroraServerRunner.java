package aurora.ide.core.server.launch;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class AuroraServerRunner {
	public static void main(String[] args) {
		// JettyConfigurator.startServer
		Server server = new Server(Integer.parseInt(args[2]));
		WebAppContext context = new WebAppContext();
		context.setResourceBase(args[0]);
		// context.setResourceBase("/Users/shiliyan/Desktop/work/aurora/workspace/new_hap/TestAurora/webRoot");
		// context.setResourceBase("/Users/shiliyan/Desktop/work/aurora/workspace/aurora_runtime/test_imart/im-jssp-sample-0.1.2");
		// "/Users/shiliyan/Desktop/work/aurora/workspace/aurora_runtime/test_imart/WebContent"

		context.setContextPath(args[1]);

		server.addHandler(context);

		// context.addLifeCycleListener(new Listener() {
		//
		// public void lifeCycleStopping(LifeCycle event) {
		// System.out.println("lifeCycleStopping");
		// }
		//
		// public void lifeCycleStopped(LifeCycle event) {
		// System.out.println("lifeCycleStopped");
		// }
		//
		// public void lifeCycleStarting(LifeCycle event) {
		// WebAppContext c = (WebAppContext) event;
		// // c.getServletContext();
		// System.out.println("lifeCycleStarting");
		// }
		//
		// public void lifeCycleStarted(LifeCycle event) {
		// System.out.println("lifeCycleStarted");
		// }
		//
		// public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		// System.out.println("lifeCycleFailure");
		//
		// }
		// });
		// WebAppContext context = new WebAppContext();
		// // context.
		// context.setResourceBase("/Users/shiliyan/Desktop/work/aurora/workspace/new_hap/HAP_DBI/webRoot");
		// context.setDescriptor(context + "/WEB-INF/web.xml");
		// context.setContextPath("/");
		// context.setParentLoaderPriority(true);
		// server.setHandler(context);
		// server.setHandler(new HelloHandler());
		try {
			server.start();

			// Enumeration servlets = context.getServletContext().getServlets();
			// ServletMapping[] servletMappings =
			// context.getServletHandler().getServletMappings();
			// EventListener[] eventListeners = context.getEventListeners();
			// for (EventListener eventListener : eventListeners) {
			// System.out.println(eventListener);
			// }
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String programFile = args.length >= 1 ? args[0] : null;
		// if (programFile == null) {
		// System.err.println("Error: No program specified");
		// return;
		// }
		//
		// String debugFlag = args.length >= 2 ? args[1] : "";
		// boolean debug = "-debug".equals(debugFlag);
		// int commandPort = 0;
		// int eventPort = 0;
		//
		// if (debug) {
		// String commandPortStr = args.length >= 3 ? args[2] : "";
		// try {
		// commandPort = Integer.parseInt(commandPortStr);
		// } catch (NumberFormatException e) {
		// System.err.println("Error: Invalid command port");
		// return;
		// }
		//
		// String eventPortStr = args.length >= 4 ? args[3] : "";
		// try {
		// eventPort = Integer.parseInt(eventPortStr);
		// } catch (NumberFormatException e) {
		// System.err.println("Error: Invalid event port");
		// return;
		// }
		// }
		//
		// PDAVirtualMachine pdaVM = null;
		// try {
		// pdaVM = new PDAVirtualMachine(programFile, debug, commandPort,
		// eventPort);
		// pdaVM.startDebugger();
		// } catch (IOException e) {
		// System.err.println("Error: " + e.toString());
		// return;
		// }
		// pdaVM.run();
	}
}
