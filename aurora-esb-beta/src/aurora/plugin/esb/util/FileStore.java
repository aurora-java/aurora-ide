package aurora.plugin.esb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;

public class FileStore {

	private String workPath;

	public FileStore(String workPath) {
		super();
		this.workPath = workPath;
	}

	public void save(CompositeMap map, String name) {
		File file = new File(workPath, name);
		try {
			createFile(map, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CompositeMap load(String fileName) {
		File file = new File(workPath, fileName);
		CompositeMap fileMap = new CompositeMap("map");
		if (file.exists()) {
			fileMap = loadFile(file);
		}

		return fileMap;
	}

	public static CompositeMap loadFile(File file) {
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

	public static void createFile(File parent, String name, CompositeMap map)
			throws IOException {
		File p_file = new File(parent, name);
		createFile(map, p_file);
	}

	public static void createFile(CompositeMap map, File p_file)
			throws IOException {
		// if(p_file.exists() ==false){
		//
		// }
		p_file.createNewFile();
		if (p_file.exists()) {
			if (p_file.canWrite()) {
				XMLOutputter.saveToFile(p_file, map);
			}
		}
	}

}
