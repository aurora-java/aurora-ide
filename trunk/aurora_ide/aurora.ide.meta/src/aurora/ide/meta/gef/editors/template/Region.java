package aurora.ide.meta.gef.editors.template;

public class Region {

	private String id;
	private String name;
	private Model model;
	private String container;

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

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Region) {
			return ((Region) obj).getId().equals(id);
		}
		return false;
	}
}
