package aurora.plugin.esb;

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

import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.console.Console;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.router.builder.MsgBuilder;
import aurora.plugin.esb.task.TaskManager;

public final class ESBConfigBuilder {
	private AuroraEsbContext esbContext;

	public ESBConfigBuilder(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	public void start() throws Exception {
		DefaultCamelContext context = new DefaultCamelContext();
		
		esbContext.setCamelContext(context);
		
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"failover:(tcp://127.0.0.1:61616)");
		// Note we can explicit name the component
		context.addComponent("test-jms",
				JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

		configure(context);

		context.start();
	}

	public void configure(DefaultCamelContext context) throws Exception {
//		context.addRoutes(new RouteBuilder() {
//
//			@Override
//			public void configure() throws Exception {
//				from("test-jms:hungup").to("mock:end");
//			}
//		});
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				from("stream:in?promptMessage=Aurora : ").bean(
						new Console(esbContext),"run");
			}
		});
//		context.addRoutes(new MsgBuilder(esbContext));
//		configRouters(context);
		// testRouter(context);
	}

	public void configRouters(CamelContext context) throws Exception {
		TaskManager m = new TaskManager(esbContext);
		List<DirectConfig> task_configs = esbContext.getDirectConfigs();
		for (DirectConfig c : task_configs) {
			m.configDirectRouter(c);
		}
	}

	// public static void testRouter(CamelContext context) throws Exception {
	// TaskManager m = new TaskManager(context);
	// m.configDirectRouter(Demo.createDirectConfig());
	// }
}