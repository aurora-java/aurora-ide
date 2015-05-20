package aurora.plugin.esb.config;

import aurora.plugin.esb.task.TaskStore;

public class FileBasedConfig extends ESBConfig{

	@Override
	public TaskStore getTaskStore() {
		return null;
	}

	@Override
	public DataStore getDataStore() {
		return null;
	}

	
}
