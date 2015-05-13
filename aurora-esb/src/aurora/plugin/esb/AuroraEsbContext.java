package aurora.plugin.esb;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.model.DirectConfig;

public class AuroraEsbContext {
	private AuroraEsbServer server;
	private List<DirectConfig> task_configs = new ArrayList<DirectConfig>();
	private DefaultCamelContext context;

	private String workPath = null;

	public AuroraEsbServer getServer() {
		return server;
	}

	public void setServer(AuroraEsbServer server) {
		this.server = server;
	}

	public List<DirectConfig> getDirectConfigs() {
		return task_configs;
	}

	public void addTaskConfig(DirectConfig config) {
		task_configs.add(config);

	}

	public void setCamelContext(DefaultCamelContext context) {
		this.context = context;
	}

	public DefaultCamelContext getCamelContext() {
		return context;
	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		if (workPath.endsWith("/"))
			this.workPath = workPath;
		else
			this.workPath = workPath + "/";
	}

}
