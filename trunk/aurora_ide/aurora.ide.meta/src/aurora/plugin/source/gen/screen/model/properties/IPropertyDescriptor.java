package aurora.plugin.source.gen.screen.model.properties;

public interface IPropertyDescriptor {

	int simple = 1 << 1;
	int reference = 1 << 2;
	int containment = 1 << 3;
	int list = 1 << 4;
	int array = 1 << 5;
	int editable = 1 << 6;
	int save = 1 << 7;
	int inner = 1 << 8;
	int _boolean = 1<<9;
	int _int = 1<<10;
	int _float = 1<<11;
//	int none = 1<<62;
	int none = 0;
	
	

	/**
	 * Returns a brief description of this property. This localized string is
	 * shown to the user when this property is selected.
	 * 
	 * @return a brief description, or <code>null</code> if none
	 */
	public String getDescription();

	/**
	 * Returns the display name for this property. This localized string is
	 * shown to the user as the name of this property.
	 * 
	 * @return a displayable name
	 */
	public String getDisplayName();

	/**
	 * Returns the id for this property. This object is used internally to
	 * distinguish one property descriptor from another.
	 * 
	 * @return the property id
	 */
	public Object getId();

}
