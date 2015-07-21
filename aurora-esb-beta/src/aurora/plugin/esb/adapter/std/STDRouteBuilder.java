package aurora.plugin.esb.adapter.std;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;

public class STDRouteBuilder extends RouteBuilder {
	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private CompositeMap settingMap;
	
	private String id;
	private String name;
	
	

	public STDRouteBuilder(AuroraEsbContext esbContext, CompositeMap settingMap) {
		super();
		this.esbContext = esbContext;
		this.settingMap = settingMap;
		settingMap.getString("name", "");
		
	}
	
	

	public void configure() throws Exception {

	}

	public CompositeMap getSettingMap() {
		return settingMap;
	}

	public void setSettingMap(CompositeMap settingMap) {
		this.settingMap = settingMap;
	}

	public AuroraEsbContext getEsbContext() {
		return esbContext;
	}

	public void setEsbContext(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

}
