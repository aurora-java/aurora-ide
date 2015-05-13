package aurora.plugin.esb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uncertain.core.ILifeCycle;
import uncertain.core.UncertainEngine;
import uncertain.ocm.AbstractLocatableObject;
import uncertain.ocm.IObjectRegistry;
import aurora.plugin.esb.model.DirectConfig;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.XMLHelper;

public class AuroraEsbServer extends AbstractLocatableObject implements
		ILifeCycle {

	private static final String packageName = "aurora.plugin.esb";
	private String workPath = null;
	private String routers = "";
	private UncertainEngine uncertainEngine;
	private IObjectRegistry registry;
	private AuroraEsbContext esbContext = new AuroraEsbContext();

	public AuroraEsbServer(IObjectRegistry registry) {
		this.registry = registry;
		uncertainEngine = (UncertainEngine) registry
				.getInstanceOfType(UncertainEngine.class);
		esbContext.setServer(this);
	}

	public String getWorkPath() {
		return workPath;
	}

	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	@Override
	public boolean startup() {
		File configDirectory = uncertainEngine.getConfigDirectory();
		File config = new File(configDirectory, packageName);
		System.out.println(config);

		String routers = this.getRouters();
		String[] split = routers.split(",");
		
		for (String ts : split) {
			File tf = new File(config, ts);
			FileInputStream fis;
			try {
				fis = new FileInputStream(tf);
				String inputStream2String = XMLHelper.inputStream2String(fis);
				Task task = XMLHelper.toTask(inputStream2String);
				DirectConfig dc = new DirectConfig();
				dc.setName(task.getName());
				dc.setRouter(task.getRouter());
				esbContext.addTaskConfig(dc);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ESBConfigBuilder runner = new ESBConfigBuilder(esbContext);
		try {
			runner.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void shutdown() {

	}

	public String getRouters() {
		return routers;
	}

	public void setRouters(String routers) {
		this.routers = routers;
	}

}
