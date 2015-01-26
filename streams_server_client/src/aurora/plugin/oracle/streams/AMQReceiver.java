package aurora.plugin.oracle.streams;

import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import uncertain.composite.CompositeMap;
import uncertain.core.IContainer;
import uncertain.core.ILifeCycle;
import uncertain.event.Configuration;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.plugin.oracle.streams.service.ServiceInvoker;
import aurora.service.IServiceFactory;
import aurora.service.ServiceThreadLocal;
import aurora.service.http.HttpServiceInstance;

public class AMQReceiver extends AbstractLocatableObject implements ILifeCycle {
	public static final String PLUGIN = AMQReceiver.class.getCanonicalName();

	private String amqUrl;

	private IObjectRegistry registry;
	private ILogger logger;
	private Thread moniteStartThread;

	public AMQReceiver(IObjectRegistry registry) {
		this.registry = registry;
	}

	public boolean startup() {
		initParameters();
		moniteStart();
		System.out.println("AMQ Recevier Started ");
		return true;
	}

	private void initParameters() {

		setLogger(LoggingContext.getLogger(PLUGIN, registry));

		if (amqUrl == null)
			throw BuiltinExceptionFactory
					.createAttributeMissing(this, "amqUrl");

		// datasource = (DataSource)
		// registry.getInstanceOfType(DataSource.class);
		// if (datasource == null)
		// throw BuiltinExceptionFactory.createInstanceNotFoundException(this,
		// DataSource.class, this.getClass().getCanonicalName());
	}

	public void onShutdown() throws Exception {
		if (moniteStartThread != null)
			moniteStartThread.interrupt();
	}

	private void moniteStart() {
		final ILogger logger = LoggingContext.getLogger(PLUGIN, registry);
		moniteStartThread = new Thread() {
			public void run() {

				// ConnectionFactory ：连接工厂，JMS 用它创建连接
				ConnectionFactory connectionFactory;
				// Connection ：JMS 客户端到JMS Provider 的连接
				Connection connection = null;
				// Session： 一个发送或接收消息的线程
				Session session;
				// Destination ：消息的目的地;消息发送给谁.
				Destination destination;
				// 消费者，消息接收者
				MessageConsumer consumer;
				connectionFactory = new ActiveMQConnectionFactory(amqUrl);
				// connectionFactory = new ActiveMQConnectionFactory(
				// ActiveMQConnection.DEFAULT_USER,
				// ActiveMQConnection.DEFAULT_PASSWORD,
				// "tcp://localhost:61616");

				//
				try {
					// 构造从工厂得到连接对象
					connection = connectionFactory.createConnection();
					// 启动
					connection.start();
					// 获取操作连接
					session = connection.createSession(Boolean.FALSE,
							Session.AUTO_ACKNOWLEDGE);
					// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
					destination = session.createQueue("logic_change_record");
					consumer = session.createConsumer(destination);
					while (true) {
						// 设置接收者接收消息的时间，为了便于测试，这里谁定为100s

						TextMessage message = (TextMessage) consumer.receive();
						if (null != message) {
							// logger.log("Message Receive");
							executeLoginProc(message.getText());
						} else {
						}
					}
				} catch (Exception e) {
					logger.log(e.getMessage());
					e.printStackTrace();
				} finally {
					try {
						if (null != connection)
							connection.close();
					} catch (Throwable ignore) {
					}
				}

			}

		};
		moniteStartThread.start();
	}

	public void executeLoginProc(String lcr) throws Exception {
		IProcedureManager procedureManager = (IProcedureManager) registry
				.getInstanceOfType(IProcedureManager.class);
		IServiceFactory serviceFactory = (IServiceFactory) registry
				.getInstanceOfType(IServiceFactory.class);
		String autoLoginProc = "message_recevie";
		Procedure proc = procedureManager.loadProcedure(autoLoginProc);
		CompositeMap auroraContext = new CompositeMap("amq_conext");
		CompositeMap createChild = auroraContext.createChild("parameter");
		// String locale = request.getLocale().toString();
		createChild.put("LCR", lcr);
		// createChild.put("locale", locale);
		HttpServiceInstance svc = createHttpService(autoLoginProc,
				procedureManager, auroraContext);

		ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		ServiceInvoker.invokeProcedureWithTransaction(autoLoginProc, proc,
				serviceFactory, svc, auroraContext);

		// ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		// HttpRequestTransfer.copyRequest(svc);
		// HttpSessionCopy.copySession(auroraContext,
		// request.getSession(false));
	}

	public HttpServiceInstance createHttpService(String service_name,

	IProcedureManager procedureManager, CompositeMap context) {
		HttpServiceInstance svc = new HttpServiceInstance(service_name,
				procedureManager);
		// svc.setRequest(request);
		// svc.setResponse(response);
		svc.setContextMap(context);
		svc.setName(service_name);
		// HttpRequestTransfer.copyRequest(svc);
		// HttpSessionCopy.copySession(svc.getContextMap(),
		// request.getSession(false));
		IContainer container = (IContainer) registry
				.getInstanceOfType(IContainer.class);
		Configuration config = (Configuration) container.getEventDispatcher();
		if (config != null)
			svc.setRootConfig(config);
		return svc;
	}

	public void shutdown() {
		try {
			onShutdown();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "shutdown jms instance failed!", e);
		}
	}

	public String getAmqUrl() {
		return amqUrl;
	}

	public void setAmqUrl(String amqUrl) {
		this.amqUrl = amqUrl;
	}

	public ILogger getLogger() {
		return logger;
	}

	public void setLogger(ILogger logger) {
		this.logger = logger;
	}
}
