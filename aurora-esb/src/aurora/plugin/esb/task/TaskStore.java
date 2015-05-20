package aurora.plugin.esb.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.xml.XMLHelper;


public class TaskStore {

//	static private String path = "/Users/shiliyan/Desktop/esb/" + "task";


	private String taskStorePath;
	
	
	
	
	public TaskStore(String workPath) {
		super();
		this.taskStorePath = workPath +"task";
	}

	public void save(Task task) {
		String name = task.getName();
		File file = new File(taskStorePath, name);
		CompositeMap fileMap = new CompositeMap("tasks");
		if (file.exists()) {
			fileMap = loadFile(file);
		}
		CompositeMap compositeMap = XMLHelper.toCompositeMap(task);
		fileMap.addChild(compositeMap);
		try {
			createFile(fileMap, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Task> load(String taskName) {
		String name = taskName;
		File file = new File(taskStorePath, name);
		CompositeMap fileMap = new CompositeMap("tasks");
		if (file.exists()) {
			fileMap = loadFile(file);
		}
		List childsNotNull = fileMap.getChildsNotNull();
		List<Task> tasks = new ArrayList<Task>();
		for (Object object : childsNotNull) {
			Task t = XMLHelper.toTask((CompositeMap) object);
			tasks.add(t);
		}
		return tasks;
	}

	public void update(Task task) {
		String name = task.getName();
		File file = new File(taskStorePath, name);
		CompositeMap fileMap = new CompositeMap("tasks");
		if (file.exists()) {
			fileMap = loadFile(file);
		}
		CompositeMap newer = new CompositeMap("tasks");
		List childsNotNull = fileMap.getChildsNotNull();
		for (Object object : childsNotNull) {
			Task t = XMLHelper.toTask((CompositeMap) object);
			if (t.getId().equals(task.getId()) == false)
				newer.addChild((CompositeMap) object);
		}
		newer.addChild(XMLHelper.toCompositeMap(task));
		try {
			createFile(newer, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void history() {

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
//		if(p_file.exists() ==false){
//			
//		}
		p_file.createNewFile();
		if (p_file.exists()) {
			if (p_file.canWrite()) {
				XMLOutputter.saveToFile(p_file, map);
			}
		}
	}

	public Task getTask(String task_id, String task_name) {
		String name = task_name;
		File file = new File(taskStorePath, name);
		CompositeMap fileMap = new CompositeMap("tasks");
		if (file.exists()) {
			fileMap = loadFile(file);
		}
		List childsNotNull = fileMap.getChildsNotNull();
		List<Task> tasks = new ArrayList<Task>();
		for (Object object : childsNotNull) {
			Task t = XMLHelper.toTask((CompositeMap) object);
			if(t.getId().equals(task_id))
				return t;
		}
		return null;
	}

}
