package aurora.ide.helpers;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;

public class FileExplorer {
	static private String defaultTarget;
	static {

		defaultTarget = "shell_open_command {0}";
		String osName = System.getProperty("os.name");
		if (osName.indexOf("Windows") != -1)
			defaultTarget = "explorer.exe /e,/select, {0}";
		else if (osName.indexOf("Mac") != -1)
			defaultTarget = "open {0}";
//		String cmd = String.format("explorer /e,/select, \"%s\"", file);
	}

	static public void open(String directory) {
//		if (defaultTarget.indexOf("{0}") == -1)
//			defaultTarget = defaultTarget.trim() + " {0}";
//		defaultTarget = MessageFormat.format(defaultTarget, directory);
//		try {
//			Runtime.getRuntime().exec(defaultTarget);
//		} catch (Throwable t) {
//		}
		
		String target = MessageFormat.format(defaultTarget, directory);
		try {
			Runtime.getRuntime().exec(target);
		} catch (Throwable t) {
		}
	}

	static public void open(File selected) {
		try {
			File directory = selected.getParentFile();
			open(directory.toString());
		} catch (Throwable e) {
		}
	}

	static public void open(IFile selected) {
		try {
			File directory = selected.getProjectRelativePath().toFile()
					.getParentFile();
			open(directory.toString());
		} catch (Throwable e) {
		}
	}
}
