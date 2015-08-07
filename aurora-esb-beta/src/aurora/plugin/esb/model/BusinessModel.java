package aurora.plugin.esb.model;

import uncertain.composite.CompositeMap;

public class BusinessModel {

	private String name;
	private String type;
	
	private CompositeMap dataMap;
	//name,category,id

	public BusinessModel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
