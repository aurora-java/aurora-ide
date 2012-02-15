package aurora.ide.meta.gef.editors.wizard.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

public class Temlpate {
	public static final String QUERY_AREA = "query";
	public static final String OPERATION_AREA = "operation";
	public static final String RESULT_AREA = "result";

	private String name;
	private String description;
	private String thumbnail;
	
//	private Map<String, List<AuroraComponent>> containers = new HashMap<String, List<AuroraComponent>>();
//	private List<String> areas=new ArrayList<String>();
	private List<AuroraComponent> models=new ArrayList<AuroraComponent>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setModels(List<AuroraComponent> models) {
		this.models = models;
	}

	public List<AuroraComponent> getModels(){
		return models;
	}
}
