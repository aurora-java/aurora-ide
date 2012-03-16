package aurora.ide.meta.gef.editors.template.n;

import org.eclipse.core.resources.IFile;

public class BMReference {

	private String id;
	private String name;
	private IFile model;

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

	public IFile getModel() {
		return model;
	}

	public void setModel(IFile model) {
		this.model = model;
	}

}
