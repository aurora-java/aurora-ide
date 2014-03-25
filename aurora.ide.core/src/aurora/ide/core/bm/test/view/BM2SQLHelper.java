package aurora.ide.core.bm.test.view;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.SAXException;

import uncertain.composite.CaseInsensitiveMap;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.Configuration;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import aurora.bm.IModelFactory;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.ide.api.composite.map.CommentCompositeMap;
import aurora.ide.api.composite.map.CommentXMLOutputter;
import aurora.ide.bm.AuroraDataBase;
import aurora.ide.bm.editor.IDEModelFactory;
import aurora.ide.fake.uncertain.engine.FakeUncertainEngine;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraConstant;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.SystemException;
import aurora.ide.search.cache.CacheManager;

public class BM2SQLHelper {

	private static final String[] tabs = { "query", "insert", "update",
			"delete" };
	private IFile bmFile;
	private Connection connection;
	private FakeUncertainEngine uncertainEngine;
	private BusinessModelService modelService;

	public BM2SQLHelper(IFile bm) {
		this.bmFile = bm;
		try {
			initConnection();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void initConnection() throws CoreException, ApplicationException,
			SQLException, IOException, SAXException {
		IProject project = bmFile.getProject();
		AuroraDataBase ad = new AuroraDataBase(project);

		String datasourceName = null;
		CompositeMap cMap = CacheManager.getWholeBMCompositeMap(bmFile);
		datasourceName = CompositeMapUtil.getValueIgnoreCase(cMap,
				"dataSourceName");

		connection = ad.getDBConnection(datasourceName);
		connection.setAutoCommit(false);
		uncertainEngine = DBConnectionUtil.getFakeUncertainEngine(project);
		if (!uncertainEngine.isRunning()) {
			uncertainEngine.startup();
		}
		String content;
		content = CommentXMLOutputter.defaultInstance().toXML(
				AuroraResourceUtil.getCompsiteLoader().loadByFullFilePath(
						bmFile.getLocation().toFile().getAbsolutePath()), true);

		modelService = makeBusinessModelService(uncertainEngine, connection,
				content);
	}

	public String[] getSQLs() throws Exception {
		String[] sqls = new String[] { "", "", "", "" };
		if (modelService != null)
			for (int i = 0; i < tabs.length; i++) {
				sqls[i] = modelService.getSql(tabs[i]).toString();
			}

		return sqls;
	}

	public String getSQL(String type) throws Exception {
		if (modelService == null) {
			return "";
		}
		return modelService.getSql(type).toString();
	}

	// public void refresh(String content) throws ApplicationException {
	// if (uncertainEngine == null || !isModify()) {
	// return;
	// }
	// modelService = makeBusinessModelService(uncertainEngine, connection,
	// content);
	// for (int i = 0; i < tabs.length; i++) {
	// StyledText st = (StyledText) tabFolder.getItem(i).getControl();
	// try {
	// SQLFormat sf = new SQLFormat();
	// st.setText(sf.format(modelService.getSql(tabs[i]).toString()));
	// } catch (Throwable e) {
	// st.setText(ExceptionUtil.getExceptionTraceMessage(e));
	// }
	// }
	//
	// if (tableViewer != null && tableViewer.getControl() != null) {
	// tableViewer.getControl().setVisible(false);
	// sashForm.layout();
	// }
	// setModify(false);
	// }
	private BusinessModelService makeBusinessModelService(
			FakeUncertainEngine uncertainEngine, Connection connection,
			String content) throws ApplicationException {
		IObjectRegistry reg = uncertainEngine.getObjectRegistry();
		DatabaseServiceFactory svcFactory = (DatabaseServiceFactory) reg
				.getInstanceOfType(DatabaseServiceFactory.class);

		BusinessModelServiceContext bc = createContext(uncertainEngine,
				connection);
		CompositeMap context = bc.getObjectContext();
		IDEModelFactory modelFactory = new IDEModelFactory(
				uncertainEngine.getOc_manager(), this.bmFile);
		uncertainEngine.getObjectRegistry().registerInstanceOnce(
				IModelFactory.class, modelFactory);
		svcFactory.setModelFactory(modelFactory);
		svcFactory.updateSqlCreator(modelFactory);
		try {
			CompositeMap bm_model = svcFactory.getModelFactory()
					.getCompositeLoader()
					.loadFromString(content, AuroraConstant.ENCODING);
			CompositeMap cim = lowerCaseDeepClone(bm_model);
			BusinessModelService service = svcFactory.getModelService(cim,
					context);
			return service;
		} catch (Throwable e) {
			throw new SystemException(e);
		}
	}

	private BusinessModelServiceContext createContext(
			FakeUncertainEngine uncertainEngine, Connection connection) {
		Configuration rootConfig = uncertainEngine.createConfig();
		rootConfig.addParticipant(this);
		CompositeMap context = new CommentCompositeMap("root");
		BusinessModelServiceContext bc = (BusinessModelServiceContext) DynamicObject
				.cast(context, BusinessModelServiceContext.class);
		bc.setConfig(rootConfig);
		bc.setConnection(connection);
		LoggerProvider lp = LoggerProvider.createInstance(Level.FINE,
				System.out);
		LoggingContext.setLoggerProvider(context, lp);
		SqlServiceContext sc = SqlServiceContext
				.createSqlServiceContext(context);
		sc.setTrace(true);
		return bc;
	}

	private CompositeMap lowerCaseDeepClone(CompositeMap map) {
		CaseInsensitiveMap cim = new CaseInsensitiveMap(map);
		List<CompositeMap> list = cim.getChilds();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				list.set(i, lowerCaseDeepClone(list.get(i)));
			}
		}
		return cim;
	}

}
