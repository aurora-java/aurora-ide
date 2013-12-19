package aurora.ide.prototype.consultant.view.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;

import aurora.ide.prototype.consultant.view.wizard.CreateModuleWizard;
import aurora.ide.prototype.consultant.view.wizard.CreateProjectWizard;

public class ResourceUtil {

	public static boolean isProject(File pj) {
		File p = new File(pj, CreateProjectWizard.QUICK_UI_PROJECT);
		return p.exists();
	}

	public static boolean isModule(File file) {
		File p = new File(file, CreateModuleWizard.QUICK_UI_MODULE);
		return p.exists();
	}

	public static File getProject(File file) {
		if (isProject(file)) {
			return file;
		} else if (file.getParentFile().exists()) {
			return getProject(file.getParentFile());
		} else {
			return file;
		}
	}

	public static IPath getFullProjectRelativePath(File project, File f) {
		IPath pjPath = new Path(project.getPath());
		IPath pPath = new Path(f.getPath());
		IPath r = new Path(project.getName() + File.separator
				+ pPath.makeRelativeTo(pjPath));
		return r;
	}

	public static File createFolder(File f) {
		if (f.mkdir()) {
			return f;
		} else {
			return null;
		}
	}

	public static void createFile(File parent, String name, CompositeMap map)
			throws IOException {
		File p_file = new File(parent, name);
		p_file.createNewFile();
		if (p_file.exists()) {
			if (p_file.canWrite()) {
				XMLOutputter.saveToFile(p_file, map);
			}
		}
	}

}
