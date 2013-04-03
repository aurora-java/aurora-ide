package aurora.plugin.source.gen.screen.model.properties;


public class DefaultPropertyDescriptor implements IPropertyDescriptor {
	private String id;
	private String description;
	private String displayName;
	
	public DefaultPropertyDescriptor(String id,int style) {
		this.id = id;
	}

	@Override
	public Object getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
