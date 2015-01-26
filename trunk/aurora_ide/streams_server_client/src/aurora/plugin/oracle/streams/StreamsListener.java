package aurora.plugin.oracle.streams;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.sql.DataSource;

import oracle.jms.AQjmsFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import uncertain.core.ILifeCycle;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;

/**
 * A Camel Application
 */
public class StreamsListener extends AbstractLocatableObject implements
		ILifeCycle {

	public static final String PLUGIN = StreamsListener.class
			.getCanonicalName();
	private IObjectRegistry registry;

	private String dbAddress;
	// port="1522" serviceName="hec3pt" streamUser="strmadmin"
	// password="strmadmin"
	private String port;
	private String serviceName;
	private String streamUser = "strmadmin";
	private String password = "strmadmin";
	private String amqUrl;

	private ILogger logger;
	private DataSource datasource;

	public StreamsListener(IObjectRegistry registry) {
		this.registry = registry;
	}

	private void initParameters() {

		logger = LoggingContext.getLogger(PLUGIN, registry);

		if (dbAddress == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this,
					"dbAddress");
		if (serviceName == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this,
					"serviceName");
		if (port == null)
			throw BuiltinExceptionFactory.createAttributeMissing(this, "port");
		if (amqUrl == null)
			throw BuiltinExceptionFactory
					.createAttributeMissing(this, "amqUrl");
	}

	@Override
	public boolean startup() {

		initParameters();

		streamsListener();
		System.out.println("Streams Listener Started ");

//		if (true) {
//			new AMQReceiver(registry).startup();
//		}
		return true;
	}
	
	private void streamsListener2() {
		try {
			CamelContext context = new DefaultCamelContext();
			ConnectionFactory aqConnectionFactory;
			aqConnectionFactory = AQjmsFactory.getQueueConnectionFactory("172.20.0.77", "hec3pt", 1522,
					"thin");
			AQjmsQueueConnectionFactoryEX connectionFactory = new AQjmsQueueConnectionFactoryEX(
					aqConnectionFactory, this);
			context.addComponent("aq-jms",
					JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

			ConnectionFactory amqConnectionFactory = new ActiveMQConnectionFactory(
					"failover:(tcp://127.0.0.1:61616)");
			context.addComponent("amq-jms", JmsComponent
					.jmsComponentAutoAcknowledge(amqConnectionFactory));

			context.addRoutes(new MyRouteBuilder());
			context.start();
		} catch (JMSException e) {
			logger.log(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.log(e.getMessage());
			e.printStackTrace();
		}
	}

	private void streamsListener() {
		try {
			CamelContext context = new DefaultCamelContext();
			ConnectionFactory aqConnectionFactory;
			aqConnectionFactory = AQjmsFactory.getQueueConnectionFactory(
					dbAddress, serviceName, Integer.valueOf(port), "thin");
			AQjmsQueueConnectionFactoryEX connectionFactory = new AQjmsQueueConnectionFactoryEX(
					aqConnectionFactory, this);
			context.addComponent("aq-jms",
					JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

			ConnectionFactory amqConnectionFactory = new ActiveMQConnectionFactory(
					amqUrl);
			context.addComponent("amq-jms", JmsComponent
					.jmsComponentAutoAcknowledge(amqConnectionFactory));

			context.addRoutes(new MyRouteBuilder());
			context.start();
		} catch (JMSException e) {
			logger.log(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.log(e.getMessage());
			e.printStackTrace();
		}
	}

	public IObjectRegistry getRegistry() {
		return registry;
	}

	@Override
	public void shutdown() {
	}

	public ILogger getLogger() {
		return logger;
	}

	public void setLogger(ILogger logger) {
		this.logger = logger;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public String getDbAddress() {
		return dbAddress;
	}

	public void setDbAddress(String dbAddress) {
		this.dbAddress = dbAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getStreamUser() {
		return streamUser;
	}

	public void setStreamUser(String streamUser) {
		this.streamUser = streamUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAmqUrl() {
		return amqUrl;
	}

	public void setAmqUrl(String amqUrl) {
		this.amqUrl = amqUrl;
	}

}
