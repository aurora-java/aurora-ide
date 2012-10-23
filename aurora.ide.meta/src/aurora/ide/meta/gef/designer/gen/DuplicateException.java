package aurora.ide.meta.gef.designer.gen;

public class DuplicateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5171150142706152232L;
	private String node;
	private String name;
	private String value;

	public DuplicateException(String node, String name, String value) {
		super(node + "(" + name + "=" + value + ") already exists.");
		this.node = node;
		this.name = name;
		this.value = value;
	}

	public String getNode() {
		return node;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}
