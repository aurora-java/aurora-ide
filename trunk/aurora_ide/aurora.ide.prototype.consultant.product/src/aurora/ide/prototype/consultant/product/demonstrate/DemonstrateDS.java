package aurora.ide.prototype.consultant.product.demonstrate;

public class DemonstrateDS {
	private String name;
	private String data;


	public DemonstrateDS(String name, String data) {
		super();
		this.name = name;
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
