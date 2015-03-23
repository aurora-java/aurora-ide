package aurora.ide.helpers;

import java.io.File;

import javax.sql.DataSource;

import uncertain.core.DirectoryConfig;
import uncertain.core.UncertainEngine;
import aurora.database.service.DatabaseServiceFactory;
import aurora.ide.AuroraPlugin;

public class DBEngineInitiator {
//
//	File mHomeDir;
//	File mConfigPath;
//	UncertainEngine uncertainEngine;
//
//	public DBEngineInitiator(File homeDir, File configPath) {
//		mHomeDir = homeDir;
//		mConfigPath = configPath;
//	}
//
//	public void init() throws Exception {
//		uncertainEngine = UncertainEngineUtil.getUncertainEngine(mConfigPath);
//		DirectoryConfig dirConfig = uncertainEngine.getDirectoryConfig();
//		dirConfig.setBaseDirectory(mHomeDir.getCanonicalPath());
//		uncertainEngine.setContinueLoadConfigWithException(true);
//		try {
//			uncertainEngine.startup();
//		} catch (Throwable e) {
//			throw new ApplicationException("启用UncertainEngine失败!", e);
//		}
//		DataSource ds = (DataSource) (uncertainEngine.getObjectRegistry().getInstanceOfType(DataSource.class));
//		if (ds == null) {
//			throw new ApplicationException(
//					"startup UncertainEngine failed: Can not get DataSource instance. Please check database config files.");
//		}
//		DatabaseServiceFactory dbs = (DatabaseServiceFactory) (uncertainEngine.getObjectRegistry()
//				.getInstanceOfType(DatabaseServiceFactory.class));
//		if (dbs == null) {
//			throw new ApplicationException(
//					"startup UncertainEngine failed: Can not get DatabaseServiceFactory instance. Please check database config files.");
//		}
//	}
//
//	/**
//	 * @return the mHomeDir
//	 */
//	public File getHomeDir() {
//		return mHomeDir;
//	}
//
//	/**
//	 * @return the uncertainEngine
//	 */
//	public UncertainEngine getUncertainEngine() {
//		return uncertainEngine;
//	}

}
