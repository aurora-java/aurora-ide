package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class Dataset extends AuroraComponent {
	// model a.b.c
	// public static final String AUTO_QUERY = "autoQuery";
	// public static final String MODEL = "model";
	private AuroraComponent owner;

//	public Dataset(AuroraComponent owner) {
//		this();
//		this.owner = owner;
//	}

	public Dataset() {
		this.setComponentType("dataSet");
	}

	public String getModel() {
		return "" + this.getPropertyValue(ComponentProperties.model);
		// return model;
	}

	public void setModel(String model) {
		// this.model = model;
		this.setPropertyValue(ComponentProperties.model, model);
	}

	public AuroraComponent getOwner() {
		// return this
		// .getAuroraComponentPropertyValue(ComponentInnerProperties.DATASET_OWNER);
		return owner;
	}

	public void setOwner(AuroraComponent owner) {
		// this.setPropertyValue(ComponentInnerProperties.DATASET_OWNER, owner);
		this.owner = owner;
	}


}
