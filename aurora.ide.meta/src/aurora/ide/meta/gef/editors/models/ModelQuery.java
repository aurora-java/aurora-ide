package aurora.ide.meta.gef.editors.models;

public class ModelQuery extends AuroraComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6830395452687270864L;
	// a.b.c
	private String path;

	public ModelQuery() {
		this.setType("model-query");
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
