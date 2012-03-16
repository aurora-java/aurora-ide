package aurora.ide.meta.gef.editors.template.n;

import java.util.ArrayList;
import java.util.List;

public class Component {
	private Component parent;
	private List<Component> children;
	private String name;
	private String id;

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
}
