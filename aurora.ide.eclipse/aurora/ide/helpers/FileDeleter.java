package aurora.ide.helpers;

import java.io.File;

public class FileDeleter {

	public static void deleteDirectory(File directory) {
		if (!directory.exists()) {
			return;
		}
		if (directory.isFile()) {
			deleteFile(directory);
			return;
		}
		if (!directory.isDirectory()) {
			return;
		}
		if (directory.listFiles().length == 0) {
			directory.delete();
		} else {
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					deleteFile(file);
				} else if (file.isDirectory()) {
					deleteDirectory(file);
				}
			}
			directory.delete();
		}
	}

	public static void deleteFile(File file) {
		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}
}
