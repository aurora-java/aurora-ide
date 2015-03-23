package aurora.ide.navigator.action;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;

import uncertain.pkg.PackageManager;
import uncertain.schema.SchemaManager;

public class SxsdValidVisablePropertyTester extends PropertyTester {

	private static final String CONFIG_PATH = "config";

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (!(receiver instanceof IResource))
			return false;
		IResource type = (IResource) receiver;
		File selectedFile = type.getLocation().toFile();
		return isValidDir(selectedFile);
	}

	public static boolean isValidDir(File selectedFile) {
		if (!selectedFile.isDirectory())
			return false;
		File[] files = selectedFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory() && PackageManager.isPackageDirectory(file)) {
				File mConfigPathFile = new File(file.getPath(), CONFIG_PATH);
				if (!mConfigPathFile.exists())
					continue;
				String extension = "." + SchemaManager.DEFAULT_EXTENSION;
				File[] sxsdFiles = mConfigPathFile.listFiles();
				if (sxsdFiles == null)
					continue;
				for (int k = 0; k < sxsdFiles.length; k++) {
					String sxsdFile = sxsdFiles[k].getName().toLowerCase();
					if (sxsdFile.endsWith(extension)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
