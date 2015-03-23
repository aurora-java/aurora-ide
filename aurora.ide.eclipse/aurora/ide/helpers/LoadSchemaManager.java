package aurora.ide.helpers;

import java.io.IOException;

import uncertain.schema.ISchemaManager;
import aurora.ide.fake.uncertain.engine.InstanceFactory;

public class LoadSchemaManager {

//	private static UncertainEngine uncertainEngine;

	public static boolean refeshSchemaManager(String[] sxsdPaths) {
		try {
			InstanceFactory.refeshPreferenceSchemaSetting(sxsdPaths);
		} catch (IOException e) {
			DialogUtil.showErrorMessageBox(ExceptionUtil
					.getExceptionTraceMessage(e));
			return false;
		}
		return true;
	}

	public static ISchemaManager refeshSchemaManager() {
		InstanceFactory.refeshPreferenceSchemaSetting();
		return InstanceFactory.getSchemaManager();
		// try {
		// String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
		// refeshSchemaManager(sxsdPaths);
		// } catch (Exception e) {
		// DialogUtil.showErrorMessageBox(ExceptionUtil
		// .getExceptionTraceMessage(e));
		// }
		// return getUncertainEngine().getSchemaManager();
	}

	static void showSxsdDirHint() {
		DialogUtil.showWarningMessageBox(LocaleMessage
				.getString("undefined.sxsd.dir"));
	}

	public static ISchemaManager getSchemaManager() {
		return InstanceFactory.getSchemaManager();
		// if (uncertainEngine != null)
		// return uncertainEngine.getSchemaManager();
		// try {
		// UncertainEngine uncertainEngine = getUncertainEngine();
		// PackageManager pkgManager = uncertainEngine.getPackageManager();
		// String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
		// if (sxsdPaths != null) {
		// for (int i = 0; i < sxsdPaths.length; i++) {
		// pkgManager.loadPackgeDirectory(sxsdPaths[i]);
		// }
		// }
		// } catch (Throwable e) {
		// DialogUtil.showErrorMessageBox(ExceptionUtil
		// .getExceptionTraceMessage(e));
		// throw new RuntimeException(e);
		// }
		// return uncertainEngine.getSchemaManager();
	}

//	private static UncertainEngine getUncertainEngine() {
//		return uncertainEngine == null ? uncertainEngine = UncertainEngineUtil
//				.getUncertainEngine() : uncertainEngine;
//	}
}
