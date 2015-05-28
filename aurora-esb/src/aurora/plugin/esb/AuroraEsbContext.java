package aurora.plugin.esb;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.impl.DefaultCamelContext;

import uncertain.composite.CompositeMap;
import uncertain.core.IContainer;
import uncertain.event.Configuration;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.Procedure;
import aurora.plugin.esb.adapter.AdapterManager;
import aurora.plugin.esb.config.DataStore;
import aurora.plugin.esb.model.Consumer;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Producer;
import aurora.plugin.esb.model.ProducerConsumer;
import aurora.service.IServiceFactory;
import aurora.service.ServiceThreadLocal;
import aurora.service.http.HttpServiceInstance;

public class AuroraEsbContext {
	private AuroraEsbServer server;
	private List<DirectConfig> task_configs = new ArrayList<DirectConfig>();
	private DefaultCamelContext context;

	private DataStore ds;

	private List<Producer> producers = new ArrayList<Producer>();

	private List<Consumer> consumers = new ArrayList<Consumer>();

	private List<ProducerConsumer> producerConsumer = new ArrayList<ProducerConsumer>();

	private AdapterManager adapterManager = new AdapterManager();

	private String workPath = null;
	private ILogger mLogger;
	private IObjectRegistry registry;

	public AuroraEsbServer getServer() {
		return server;
	}

	public void setServer(AuroraEsbServer server) {
		this.server = server;
	}

	public List<DirectConfig> getDirectConfigs() {
		return task_configs;
	}

	public void addTaskConfig(DirectConfig config) {
		task_configs.add(config);

	}

	public void setCamelContext(DefaultCamelContext context) {
		this.context = context;
	}

	public DefaultCamelContext getCamelContext() {
		return context;
	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		if (workPath.endsWith("/"))
			this.workPath = workPath;
		else
			this.workPath = workPath + "/";
	}

	public List<Producer> getProducers() {
		return producers;
	}

	public void addProducer(Producer producer) {
		this.producers.add(producer);
	}

	public List<Consumer> getConsumers() {
		return consumers;
	}

	public void addConsumer(Consumer consumer) {
		// producer.addConsumer(consumer);
		// consumer.setProducer(producer);
		this.consumers.add(consumer);
	}

	public void removeConsumer(Consumer consumer) {
		// consumer.setProducer(null);
		// producer.removeConsumer(consumer);
		this.consumers.remove(consumer);
	}

	public void addProducer(DirectConfig dc) {
		Producer pro = new Producer();
		pro.setName(dc.getName());
		pro.setType(dc.getType());
		pro.setFrom(dc.getRouter().getFrom());
		this.addProducer(pro);
	}

	public void addConsumer(DirectConfig dc) {
		Consumer co = new Consumer();
		co.setName(dc.getName());
		co.setType(dc.getType());
		co.setTo(dc.getRouter().getTo());
		this.addConsumer(co);
	}

	public List<ProducerConsumer> getProducerConsumer() {
		return producerConsumer;
	}

	public void setProducerConsumer(ProducerConsumer producerConsumer) {
		this.producerConsumer.add(producerConsumer);
	}

	public Producer getProducer(String p_name) {
		List<Producer> producers = this.getProducers();
		for (Producer producer : producers) {
			if (p_name.equals(producer.getName())) {
				return producer;
			}
		}
		return null;
	}

	public Consumer getConsumer(String c_name) {
		List<Consumer> consumers = this.getConsumers();
		for (Consumer consumer : consumers) {
			if (c_name.equals(consumer.getName())) {
				return consumer;
			}
		}
		return null;
	}

	public ProducerConsumer getProducerConsumer(String name) {

		List<ProducerConsumer> producerConsumer = this.getProducerConsumer();
		for (ProducerConsumer pc : producerConsumer) {
			if (name.equals(pc.getProducer().getName())) {
				return pc;
			}
		}
		return null;
	}

	public void bind(Producer producer, Consumer consumer) throws Exception {
		ProducerConsumer pc = this.getProducerConsumer(producer.getName());
		if (pc != null) {
			pc.addConsumer(consumer);
			// this.context.addRoutes(new ConsumerBuilder(this, consumer));
			this.context.addRoutes(this.adapterManager.createRouteBuilder(this,
					consumer));
		}

	}

	public boolean isActive(Producer producer) {
		ProducerConsumer pc = this.getProducerConsumer(producer.getName());
		return pc != null;
	}

	public void bind(Producer producer) throws Exception {
		ProducerConsumer pc = new ProducerConsumer();
		pc.setProducer(producer);
		this.getProducerConsumer().add(pc);
		// this.context.addRoutes(new ProducerBuilder(this, producer));
		this.context.addRoutes(this.adapterManager.createRouteBuilder(this,
				producer));
	}

	public DataStore getDataStore() {
		return ds;
	}

	public void setDataStore(DataStore ds) {
		this.ds = ds;
	}

	public AdapterManager getAdapterManager() {
		return adapterManager;
	}

	public void setAdapterManager(AdapterManager adapterManager) {
		this.adapterManager = adapterManager;
	}

	public void setLoger(ILogger mLogger) {
		this.mLogger = mLogger;
	}

	public ILogger getmLogger() {
		return mLogger;
	}

	public void executeProc(String proc_name, CompositeMap para)
			throws Exception {
		IProcedureManager procedureManager = (IProcedureManager) registry
				.getInstanceOfType(IProcedureManager.class);
		IServiceFactory serviceFactory = (IServiceFactory) registry
				.getInstanceOfType(IServiceFactory.class);
		String autoLoginProc = proc_name;
		Procedure proc = procedureManager.loadProcedure(autoLoginProc);
		CompositeMap auroraContext = new CompositeMap("esb_conext");
		CompositeMap createChild = auroraContext.createChild("parameter");
		// String locale = request.getLocale().toString();
		createChild.addChild(para);
		// createChild.put("locale", locale);
		HttpServiceInstance svc = createHttpService(autoLoginProc,
				procedureManager, auroraContext);

		ServiceThreadLocal.setCurrentThreadContext(auroraContext);
		ServiceInvoker.invokeProcedureWithTransaction(autoLoginProc, proc,
				serviceFactory, svc, auroraContext);
		System.out.println("=================");
		System.out.println(auroraContext.toXML());

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

	public void setRegistry(IObjectRegistry registry) {
		this.registry = registry;
	}

}
