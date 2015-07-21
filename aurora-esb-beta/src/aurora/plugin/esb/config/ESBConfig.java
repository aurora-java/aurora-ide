package aurora.plugin.esb.config;

import aurora.plugin.esb.task.TaskStore;

public abstract class ESBConfig {
	abstract public TaskStore getTaskStore();

	abstract public DataStore getDataStore();
	
//	abstract public DataStore getAMQMsg();
	
}
