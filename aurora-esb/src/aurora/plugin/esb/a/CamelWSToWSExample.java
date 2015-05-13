package aurora.plugin.esb.a;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;

import aurora.plugin.esb.console.Console;
import aurora.plugin.esb.model.Demo;
import aurora.plugin.esb.task.TaskManager;

/**
 * An example class for demonstrating some of the basics behind Camel. This
 * example sends some text messages on to a JMS Queue, consumes them and
 * persists them to disk
 */
public final class CamelWSToWSExample {

	private CamelWSToWSExample() {
	}

	
	 static protected JndiRegistry createRegistry() throws Exception {
	        JndiRegistry jndi = new JndiRegistry();
//	        jndi.bind("console", new Console());
	        return jndi;
	    }
	public static void main(String args[]) throws Exception {
		// START SNIPPET: e1
		DefaultCamelContext context = new DefaultCamelContext();
//		context.setRegistry(createRegistry());
		// END SNIPPET: e1
		// Set up the ActiveMQ JMS Components
		// START SNIPPET: e2
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"failover:(tcp://127.0.0.1:61616)");
		// Note we can explicit name the component
		context.addComponent("test-jms",
				JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("test-jms:hungup").to("mock:end");
//				from("stream:in?promptMessage=Enter something: ").bean(new Console(context));
//				.transform()
//						.simple("${body.toUpperCase()}");
				
			}
		});
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				
				from("stream:in?promptMessage=Enter something: ").bean(new Console(context));
//				.transform()
//						.simple("${body.toUpperCase()}");
				
			}
		});

		testRouter(context);
		// END SNIPPET: e4
		// Now everything is set up - lets start the context

		// context.addRoutes(new RouteBuilder() {
		//
		// @Override
		// public void configure() throws Exception {
		// // from("test-jms:send_data_record").to(
		// // "file:/Users/shiliyan/Desktop/target/sendData2");
		// from("test-jms:get_data_record").to(
		// "file:/Users/shiliyan/Desktop/target/getData2");
		// }
		//
		// });

		context.start();

		// END SNIPPET: e5

		// wait a bit and then stop
		Thread.sleep(1000);
		// context.stop();
	}

	public static void testRouter(CamelContext context) throws Exception {
		TaskManager m = new TaskManager(context);
		m.configDirectRouter(Demo.createDirectConfig());
		// Demo.createTask()
		// Task task = m.createTask(Demo.createDirectConfig());
//		m.directStartTask(Demo.createTask());
		// Task task = Demo.createTask();
		// Router createRouter = task.getRouter();
		// ProducerTemplate template = context.createProducerTemplate();
		// context.addRoutes(new WSRouteBuilder(task));
		// template.sendBody("direct:" + createRouter.getFrom().getName(),
		// createRouter.getFrom().getParaText());

		// template.sendBody("direct:" + createRouter.getTo().getName(),
		// createRouter.getTo().getParaText());
	}
}