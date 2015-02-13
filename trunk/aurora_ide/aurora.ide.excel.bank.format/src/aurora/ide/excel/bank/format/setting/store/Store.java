package aurora.ide.excel.bank.format.setting.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.ide.excel.bank.format.Activator;

public class Store {

	public void activatorStart() {
		IPath stateLocation = Activator.getDefault().getStateLocation();
		System.out.println(stateLocation);
	}

	public void activatorStop() {

	}

	static public void saveXLSSetting(String xls_code, CompositeMap map) {
		try {
			createFile(xls_code, map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createFile(String xls_code, CompositeMap map)
			throws IOException {
		IPath stateLocation = Activator.getDefault().getStateLocation();
		File p_file = stateLocation.append(xls_code).toFile();
		p_file.createNewFile();
		if (p_file.exists()) {
			if (p_file.canWrite()) {
				XMLOutputter.saveToFile(p_file, map);
			}
		}
	}

	public static CompositeMap loadSetting(String xls_code) {
		IPath stateLocation = Activator.getDefault().getStateLocation();
		File p = stateLocation.append(xls_code).toFile();
		if (p.exists() == false)
			return null;
		CompositeMap loadFile = loadFile(p);
		return loadFile;
	}

	private static CompositeMap loadFile(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			CompositeLoader parser = new CompositeLoader();
			CompositeMap rootMap = parser.loadFromStream(is);
			return rootMap;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
