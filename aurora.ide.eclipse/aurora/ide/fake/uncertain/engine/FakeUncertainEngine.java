package aurora.ide.fake.uncertain.engine;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.sql.DataSource;

import uncertain.cache.CacheFactoryConfig;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.core.DirectoryConfig;
import uncertain.core.IContainer;
import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.IContextListener;
import uncertain.event.IEventDispatcher;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.logging.BasicConsoleHandler;
import uncertain.logging.BasicFileHandler;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingTopic;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IClassLocator;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.pkg.IInstanceCreationListener;
import uncertain.pkg.IPackageManager;
import uncertain.pkg.PackageManager;
import uncertain.pkg.PackagePath;
import uncertain.proc.IProcedureManager;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.ProcedureManager;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;
import aurora.datasource.DatabaseConnection;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DialogUtil;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;

public class FakeUncertainEngine {
	private SchemaManager mSchemaManager;
	private CompositeLoader compositeLoader;
	private OCManager oc_manager;
	private ClassRegistry classRegistry;
	private InternalPackageManager internalPackageManager;
	private ObjectRegistryImpl objectRegistry;
	private DirectoryConfig directoryConfig;
	private ParticipantRegistry mParticipantRegistry;
	// private PackageManager mPackageManager;
	private boolean mIsRunning;
	private Set<String> mLoadedFiles = new HashSet<String>();
	private List<ILifeCycle> mLoadedLifeCycleList = new LinkedList<ILifeCycle>();
	@SuppressWarnings("rawtypes")
	private Set mContextListenerSet = new HashSet();
	private Configuration mConfig;
	private File mConfigDir;
	private static final String mDefaultLogLevel = "WARNING";
	private ILogger mLogger;
	private InnerUncertainEngine engine;
	private ProcedureManager mProcedureManager;

	private class InnerUncertainEngine extends UncertainEngine {

		@Override
		protected void bootstrap() {
			// super.bootstrap();
		}

		@Override
		public void initialize(CompositeMap config) {
			// super.initialize(config);
		}

		@Override
		protected ILoggerProvider createDefaultLoggerProvider() {
			return null;
			// return super.createDefaultLoggerProvider();
		}

		@Override
		protected void checkLogger() {
			// super.checkLogger();
		}

		@Override
		public void addLoggingConfig(ILoggerProvider logging_config) {
			super.addLoggingConfig(logging_config);
		}

		@Override
		public ILogger getLogger(String topic) {
			// return super.getLogger(topic);
			return FakeUncertainEngine.this.mLogger;
		}

		@Override
		public void logException(String message, Throwable thr) {
			// super.logException(message, thr);
		}

		@Override
		public void loadConfigFile(String full_path) {
			// super.loadConfigFile(full_path);
		}

		@Override
		public Configuration createConfig() {
			// return super.createConfig();
			return FakeUncertainEngine.this.createConfig();
		}

		@Override
		public Configuration createConfig(CompositeMap cfg) {
			// return super.createConfig(cfg);
			return FakeUncertainEngine.this.createConfig(cfg);
		}

		@Override
		public void initContext(CompositeMap context) {
			// super.initContext(context);
		}

		@Override
		public void destroyContext(CompositeMap context) {
			// super.destroyContext(context);
		}

		@Override
		public void addPackages(PackagePath[] paths) throws IOException {
			FakeUncertainEngine.this.addPackages(paths);
			// super.addPackages(paths);
		}

		@Override
		public Throwable getInitializeException() {
			// return super.getInitializeException();
			return null;
		}

		@Override
		public boolean isRunning() {
			return FakeUncertainEngine.this.isRunning();
			// return super.isRunning();
		}

		@Override
		public void setConfigDirectory(File dir) {
			// super.setConfigDirectory(dir);
		}

		@Override
		public void addClassRegistry(ClassRegistry reg) {
			// super.addClassRegistry(reg);
		}

		@Override
		public void addClassRegistry(ClassRegistry reg, boolean override) {
			// super.addClassRegistry(reg, override);
		}

		@Override
		public ClassRegistry getClassRegistry() {
			// return super.getClassRegistry();
			return FakeUncertainEngine.this.getClassRegistry();
		}

		@Override
		public CompositeLoader getCompositeLoader() {
			// return super.getCompositeLoader();
			return FakeUncertainEngine.this.getCompositeLoader();
		}

		@Override
		public OCManager getOcManager() {
			// return super.getOcManager();
			return FakeUncertainEngine.this.getOc_manager();
		}

		@Override
		public IObjectCreator getObjectCreator() {
			// return super.getObjectCreator();
			return FakeUncertainEngine.this.getObjectRegistry();
		}

		@Override
		public IObjectRegistry getObjectRegistry() {
			// return super.getObjectRegistry();
			return FakeUncertainEngine.this.getObjectRegistry();
		}

		@Override
		public IEventDispatcher getEventDispatcher() {
			// return super.getEventDispatcher();
			return FakeUncertainEngine.this.getEventDispatcher();
		}

		@Override
		public ParticipantRegistry getParticipantRegistry() {
			// return super.getParticipantRegistry();
			return null;
		}

		@Override
		public CompositeMap getGlobalContext() {
			// return super.getGlobalContext();
			return null;
		}

		@Override
		public File getConfigDirectory() {
			// return super.getConfigDirectory();
			return FakeUncertainEngine.this.getConfigDirectory();
		}

		@Override
		public boolean getIsRunning() {
			// return super.getIsRunning();
			return FakeUncertainEngine.this.isRunning();
		}

		@Override
		public void addContextListener(IContextListener listener) {
			// super.addContextListener(listener);
			FakeUncertainEngine.this.addContextListener(listener);
		}

		@Override
		public void startup() {
			// super.startup();
		}

		@Override
		public void startup(boolean scan_config_files) {
			// super.startup(scan_config_files);
		}

		@Override
		public void shutdown() {
			// super.shutdown();
		}

		@Override
		public DirectoryConfig getDirectoryConfig() {
			// return super.getDirectoryConfig();
			return FakeUncertainEngine.this.directoryConfig;
		}

		@Override
		public IProcedureManager getProcedureManager() {
			// return super.getProcedureManager();
			return FakeUncertainEngine.this.getProcedureManager();
		}

		@Override
		public String getName() {
			// return super.getName();
			return "Aurora IDE FakeUncertainEngine";
		}

		@Override
		public String getMBeanName(String category, String sub_name) {
			// return super.getMBeanName(category, sub_name);
			return "";
		}

		@Override
		public void setName(String name) {
			// super.setName(name);
		}

		@Override
		public String getDefaultLogLevel() {
			return super.getDefaultLogLevel();
		}

		@Override
		public void setDefaultLogLevel(String defaultLogLevel) {
			super.setDefaultLogLevel(defaultLogLevel);
		}

		@Override
		public PackageManager getPackageManager() {
			// return super.getPackageManager();
			return FakeUncertainEngine.this.getInternalPackageManager();
		}

		@Override
		public ISchemaManager getSchemaManager() {
			// return super.getSchemaManager();
			return FakeUncertainEngine.this.getmSchemaManager();
		}

		@Override
		public void registerMBean() {
			// super.registerMBean();
		}

		@Override
		public boolean isContinueLoadConfigWithException() {
			return super.isContinueLoadConfigWithException();
		}

		@Override
		public void setContinueLoadConfigWithException(
				boolean continueLoadConfigWithException) {
			super.setContinueLoadConfigWithException(continueLoadConfigWithException);
		}

	}

	public FakeUncertainEngine() {
		init();
		loadPackageManager();
	}

	public FakeUncertainEngine(String base_dir, String config_dir) {
		init();
		this.directoryConfig.setBaseDirectory(base_dir);
		this.directoryConfig.setConfigDirectory(config_dir);
		loadPackageManager();
	}

	private InternalPackageManager loadPackageManager() {

		internalPackageManager = new InternalPackageManager(compositeLoader,
				oc_manager, mSchemaManager);
		try {
			internalPackageManager.loadPackgeDirectory(AuroraResourceUtil
					.getClassPathFile("uncertain_builtin_package")
					.getCanonicalPath());
			internalPackageManager.loadPackgeDirectory(AuroraResourceUtil
					.getClassPathFile("aurora_builtin_package")
					.getCanonicalPath());
			internalPackageManager.loadPackgeDirectory(AuroraResourceUtil
					.getClassPathFile("aurora_plugin_package")
					.getCanonicalPath());

		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.logErrorException(e);
		}
		return internalPackageManager;
	}

	private void init() {
		mSchemaManager = new SchemaManager();
		mSchemaManager.addSchema(SchemaManager.getSchemaForSchema());

		compositeLoader = CompositeLoader.createInstanceForOCM();
		objectRegistry = new ObjectRegistryImpl();
		oc_manager = new OCManager(objectRegistry);
		classRegistry = oc_manager.getClassRegistry();
		directoryConfig = DirectoryConfig.createDirectoryConfig();

		mParticipantRegistry = new ParticipantRegistry();

		setDefaultClassRegistry(classRegistry);
		registerBuiltinInstances();
	}

	public Configuration createConfig(CompositeMap cfg) {
		Configuration conf = new Configuration(mParticipantRegistry, oc_manager);
		conf.loadConfig(cfg);
		return conf;
	}

	protected ILoggerProvider createDefaultLoggerProvider() {
		LoggerProvider clp = new LoggerProvider();
		clp.setDefaultLogLevel(mDefaultLogLevel);
		clp.addTopics(new LoggingTopic[] {
				new LoggingTopic(UncertainEngine.UNCERTAIN_LOGGING_TOPIC,
						Level.INFO),
				new LoggingTopic(OCManager.LOGGING_TOPIC, Level.WARNING) });

		String log_path = directoryConfig.getLogDirectory();
		if (log_path == null)
			clp.addHandle(new BasicConsoleHandler());
		else {
			BasicFileHandler fh = new BasicFileHandler();
			fh.setLogPath(log_path);
			fh.setLogFilePrefix("Aurora IDE FakeUncertainEngine");
			clp.addHandle(fh);
		}
		return clp;
	}

	protected void checkLogger() {
		ILoggerProvider logger_provider = (ILoggerProvider) objectRegistry
				.getInstanceOfType(ILoggerProvider.class);
		if (logger_provider == null) {
			logger_provider = createDefaultLoggerProvider();
			objectRegistry.registerInstance(ILoggerProvider.class,
					logger_provider);
			this.oc_manager.setLoggerProvider(logger_provider);
		}
		mLogger = logger_provider
				.getLogger(UncertainEngine.UNCERTAIN_LOGGING_TOPIC);
	}

	private void loadInstanceFromPackage() {
		internalPackageManager.createInstances(objectRegistry,
				new IInstanceCreationListener() {

					public void onInstanceCreate(Object instance,
							File config_file) {
						// System.out.println(instance);
						// System.out.println(config_file);
						if (!loadInstance(instance)) {
							throw BuiltinExceptionFactory
									.createInstanceStartError(instance,
											config_file.getAbsolutePath(), null);
						}
						mLoadedFiles.add(config_file.getAbsolutePath());

					}
				}, true);

	}

	private boolean loadInstance(Object inst) {
		mConfig.addParticipant(inst);
		if (inst instanceof IContextListener)
			addContextListener((IContextListener) inst);
		if (inst instanceof ILifeCycle) {
			ILifeCycle c = (ILifeCycle) inst;
			if (c instanceof CacheFactoryConfig) {
				return false;
			}
			if (c.startup()) {
				mLoadedLifeCycleList.add(c);
				return true;
			} else
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void addContextListener(IContextListener listener) {
		mContextListenerSet.add(listener);
	}

	public void addPackages(PackagePath[] paths) throws IOException {
		for (int i = 0; i < paths.length; i++)
			internalPackageManager.loadPackage(paths[i]);
	}

	private void registerBuiltinInstances() {
		engine = new InnerUncertainEngine();
		objectRegistry.registerInstanceOnce(IContainer.class, engine);
		objectRegistry.registerInstanceOnce(UncertainEngine.class, engine);
		objectRegistry.registerInstance(CompositeLoader.class, compositeLoader);
		objectRegistry.registerInstance(IClassLocator.class, classRegistry);
		objectRegistry.registerInstance(ClassRegistry.class, classRegistry);
		objectRegistry.registerInstance(OCManager.class, oc_manager);
		objectRegistry.registerInstance(DirectoryConfig.class, directoryConfig);

		objectRegistry.registerInstanceOnce(IObjectRegistry.class,
				objectRegistry);
		objectRegistry.registerInstanceOnce(IObjectCreator.class,
				objectRegistry);
		objectRegistry.registerInstance(IPackageManager.class,
				internalPackageManager);

		objectRegistry.registerInstanceOnce(ISchemaManager.class,
				mSchemaManager);

		objectRegistry.registerInstance(ParticipantRegistry.class,
				mParticipantRegistry);
		// objectRegistry.registerInstance(IPackageManager.class,
		// mPackageManager);
	}

	private void setDefaultClassRegistry(ClassRegistry mClassRegistry) {

		mClassRegistry.registerPackage("uncertain.proc");
		mClassRegistry.registerPackage("uncertain.ocm");
		mClassRegistry.registerPackage("uncertain.logging");
		mClassRegistry.registerPackage("uncertain.core");
		mClassRegistry.registerPackage("uncertain.core.admin");
		mClassRegistry.registerPackage("uncertain.event");
		mClassRegistry.registerPackage("uncertain.pkg");
		mClassRegistry.registerPackage("uncertain.cache");
		mClassRegistry.registerPackage("uncertain.cache.action");
		mClassRegistry.registerClass("class-registry", "uncertain.ocm",
				"ClassRegistry");
		mClassRegistry.registerClass("package-mapping", "uncertain.ocm",
				"PackageMapping");
		mClassRegistry.registerClass("class-mapping", "uncertain.ocm",
				"ClassMapping");
		mClassRegistry.registerClass("feature-attach", "uncertain.ocm",
				"FeatureAttach");
		mClassRegistry.registerClass("package-path", "uncertain.pkg",
				"PackagePath");

		// loadInternalRegistry(LoggingConfig.LOGGING_REGISTRY_PATH);
	}

	public SchemaManager getmSchemaManager() {
		return mSchemaManager;
	}

	public CompositeLoader getCompositeLoader() {
		return compositeLoader;
	}

	public OCManager getOc_manager() {
		return oc_manager;
	}

	public ClassRegistry getClassRegistry() {
		return classRegistry;
	}

	public InternalPackageManager getInternalPackageManager() {
		return internalPackageManager;
	}

	public ObjectRegistryImpl getObjectRegistry() {
		return objectRegistry;
	}

	Map<String, DataSource> datasources;

	private List<DatabaseConnection> loadDataSourceConfig() {
		File configDirectory = new File(directoryConfig.getConfigDirectory());
		File config = new File(configDirectory,
				"/aurora.database/datasource.config");
		if (config.exists() == false) {
			config = new File(configDirectory, "0.datasource.config");
		}
		final List<DatabaseConnection> dss = new ArrayList<DatabaseConnection>();
		if (config.exists() == false) {
			return dss;
		}
		CompositeMap loadFile = CompositeMapUtil.loadFile(config);
		loadFile.iterate(new IterationHandle() {
			public int process(CompositeMap map) {
				if ("database-connection".equalsIgnoreCase(map.getName())) {
					DatabaseConnection dc = new DatabaseConnection();
					dc.setName(CompositeMapUtil.getValueIgnoreCase(map, "name"));
					dc.setDriverClass(CompositeMapUtil.getValueIgnoreCase(map,
							"driverClass"));
					dc.setUrl(CompositeMapUtil.getValueIgnoreCase(map, "url"));
					dc.setUserName(CompositeMapUtil.getValueIgnoreCase(map,
							"userName"));
					dc.setPassword(CompositeMapUtil.getValueIgnoreCase(map,
							"password"));
					dss.add(dc);
				}
				return IterationHandle.IT_CONTINUE;
			}
		}, true);
		return dss;
	}

	public void startup() {
		long tick = System.currentTimeMillis();

		mIsRunning = false;
		mConfig = createConfig();
		checkLogger();

		mConfig.setLogger(mLogger);
		setProcedureManager(new ProcedureManager(engine));

		loadInstanceFromPackage();
		datasources = new HashMap<String, DataSource>();
		List<DatabaseConnection> loadDataSourceConfig = loadDataSourceConfig();
		// createDatasource1(loadDataSourceConfig);
		createDatasource2(loadDataSourceConfig);

		mIsRunning = isSuccess();
		tick = System.currentTimeMillis() - tick;
	}

	public void createDatasource2(List<DatabaseConnection> loadDataSourceConfig) {
		for (DatabaseConnection dbConfig : loadDataSourceConfig) {
			// Map<String, String> dbMap = new HashMap<String, String>();
			DataSource ds = null;
			// dbMap.put("username", dbConfig.getUserName());
			// dbMap.put("password", dbConfig.getPassword());
			// dbMap.put("driverClassName", dbConfig.getDriverClass());
			// dbMap.put("url", dbConfig.getUrl());
			ds = FakeDataSource.createDataSource(dbConfig);
			this.datasources.put(dbConfig.getName(), ds);
		}
	}

	public void createDatasource1(List<DatabaseConnection> loadDataSourceConfig) {
		for (DatabaseConnection dc : loadDataSourceConfig) {
			// Class.forName(className)
			try {
				DriverManagerDataSource ds = (DriverManagerDataSource) DataSources
						.unpooledDataSource();
				// ds = DataSources.unpooledDataSource(dbConfig.getUrl(),
				// dbConfig.getUserName(), dbConfig.getPassword());
				ds.setDriverClass(dc.getDriverClass());
				ds.setDescription(dc.getName());
				ds.setJdbcUrl(dc.getUrl());
				ds.setPassword(dc.getPassword());
				ds.setUser(dc.getUserName());
				this.datasources.put(dc.getName(), ds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isSuccess() {
		// if(true){
		// IObjectRegistry mObjectRegistry = getObjectRegistry();
		// DataSource ds = (DataSource) mObjectRegistry
		// .getInstanceOfType(DataSource.class);
		// return ds != null;
		// }
		return datasources.size() > 0;
	}

	public void initialize(CompositeMap config) {
		// // populate self from config
		// if (config != null) {
		// oc_manager.populateObject(config, this);
		// CompositeMap child = config
		// .getChild(DirectoryConfig.KEY_PATH_CONFIG);
		// if (child != null) {
		// if (directoryConfig == null)
		// directoryConfig = DirectoryConfig
		// .createDirectoryConfig(child);
		// else
		// directoryConfig.getObjectContext().putAll(child);
		// }
		// directoryConfig.checkValidation();
		// }
	}

	public File getConfigDirectory() {
		if (mConfigDir == null) {
			String dir = directoryConfig.getConfigDirectory();
			if (dir != null)
				mConfigDir = new File(dir);
		}
		return mConfigDir;
	}

	public void shutdown() {
		for (ILifeCycle l : mLoadedLifeCycleList) {
			try {
				l.shutdown();
			} catch (Throwable thr) {
				mLogger.log(Level.WARNING, "Error when shuting down instance "
						+ l, thr);
			}
		}
		mIsRunning = false;
	}

	public boolean isRunning() {
		return mIsRunning;
	}

	public Configuration createConfig() {
		Configuration conf = new Configuration(mParticipantRegistry, oc_manager);
		return conf;
	}

	public IEventDispatcher getEventDispatcher() {
		return mConfig;
	}

	public ProcedureManager getProcedureManager() {
		return mProcedureManager;
	}

	public void setProcedureManager(ProcedureManager mProcedureManager) {
		this.mProcedureManager = mProcedureManager;
	}

	public DataSource getDatasource(String string) {
		if (datasources == null)
			return null;
		return this.datasources.get(string);
	}

	public DataSource getDefaultDatasource() {
		if (datasources == null)
			return null;
		return this.datasources.get(null);
	}

}
