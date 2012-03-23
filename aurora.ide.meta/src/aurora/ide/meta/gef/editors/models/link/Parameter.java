package aurora.ide.meta.gef.editors.models.link;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

public class Parameter extends AuroraComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6921030642748467776L;
	private String name;
	private String value;
	private Container container;

	public Parameter() {
	}

	public Parameter(String name, Container container, String value) {
		super();
		this.name = name;
		this.value = value;
		this.container = container;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public Parameter clone() {
		return new Parameter(name, container, value);
	}

}
