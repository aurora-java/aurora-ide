package aurora.plugin.esb;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.impl.DefaultCamelContext;

import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Task;

public class AuroraEsbContext {
	private AuroraEsbServer server;
	private List<DirectConfig> task_configs = new ArrayList<DirectConfig>();
	private DefaultCamelContext context;

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

}
