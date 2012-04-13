package aurora.ide.meta.gef.editors.template;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.models.link.Parameter;

public class Component {
	private Component parent;
	private List<Component> children;
	private String name;
	private String id;
	private String componentType;
	private String url;
	private List<Parameter> paras = new ArrayList<Parameter>();

	public Component() {
		super();
	}

	public void addChild(Component tm) {
		if (children == null)
			children = new ArrayList<Component>();
		children.add(tm);
		tm.setParent(this);
	}

	public Component getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setParent(Component parent) {
		this.parent = parent;
	}

	public List<Component> getChildren() {
		return children;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public List<Parameter> getParas() {
		return paras;
	}

	public void setParas(List<Parameter> paras) {
		this.paras = paras;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
