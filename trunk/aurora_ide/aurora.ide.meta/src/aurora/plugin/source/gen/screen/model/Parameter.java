package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;


public class Parameter extends AuroraComponent {
	/**
	 * 
	 */
//	private String name;
//	private String value;
	private Container container;

	public Parameter() {
		this.setComponentType("inner_paramerter");
	}

	public Parameter(String name, Container container, String value) {
		super();
//		this.name = name;
//		this.value = value;
		this.setName(name);
		this.setValue(value);
		this.container = container;
	}

	public String getName() {
//		return name;
		return this.getStringPropertyValue(ComponentInnerProperties.PARAMETER_NAME);
	}

	public void setName(String name) {
//		this.name = name;
		this.setPropertyValue(ComponentInnerProperties.PARAMETER_NAME, name);
	}

	public String getValue() {
//		return value;
		return this.getStringPropertyValue(ComponentInnerProperties.PARAMETER_VALUE);
	}

	public void setValue(String value) {
//		this.value = value;
		this.setPropertyValue(ComponentInnerProperties.PARAMETER_VALUE, value);
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public Parameter clone() {
//		return new Parameter(name, container, value);
		Parameter parameter = new Parameter();
		parameter.setContainer(this.getContainer());
		parameter.setName(this.getName());
		parameter.setValue(this.getValue());
		return parameter;
	}

	public String toParameterFormat() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName());
		return "";
	}


}
