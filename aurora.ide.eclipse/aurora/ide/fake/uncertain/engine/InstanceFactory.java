package aurora.ide.fake.uncertain.engine;

import java.io.IOException;

import uncertain.pkg.PackageManager;
import uncertain.schema.ISchemaManager;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.preferencepages.SxsdDirPreferencePage;

public class InstanceFactory {

	private static PackageManager packageManager;

	public static PackageManager getPackageManager() {
		if (packageManager == null) {
			packageManager = createInternalPackageManager();
			refeshPreferenceSchemaSetting();
		}
		return packageManager;
	}

	private static InternalPackageManager createInternalPackageManager() {
		FakeUncertainEngine fakeUncertainEngine = new FakeUncertainEngine();
		return fakeUncertainEngine.getInternalPackageManager();
	}

	public static ISchemaManager getSchemaManager() {
		return getPackageManager().getSchemaManager();
	}

	public static void refeshPreferenceSchemaSetting() {
		String[] sxsdPaths = SxsdDirPreferencePage.getSxsdPaths();
		try {
			refeshPreferenceSchemaSetting(sxsdPaths);
		} catch (IOException e) {
			e.printStackTrace();
			DialogUtil.logErrorException(e);
		}
	}

	public static void refeshPreferenceSchemaSetting(String[] sxsdPaths)
			throws IOException {
		if (sxsdPaths != null) {
			for (int i = 0; i < sxsdPaths.length; i++) {
				getPackageManager().loadPackgeDirectory(sxsdPaths[i]);
			}
		}
	}
}
