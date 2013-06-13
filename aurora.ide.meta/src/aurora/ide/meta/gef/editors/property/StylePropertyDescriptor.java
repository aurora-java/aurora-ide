package aurora.ide.meta.gef.editors.property;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public class StylePropertyDescriptor extends PropertyDescriptor {

	private int style;
	private String child_property_id;
	public static final int component = 1 << 1;
	public static final int dataset = 1 << 2;
	public static final int datasetfield = 1 << 3;
	public static final int component_child = 1 << 4;

	public StylePropertyDescriptor(Object id, String displayName) {
		this(id, displayName, component);
	}

	public StylePropertyDescriptor(Object id, String displayName, int style) {
		super(id, displayName);
		this.setStyle(style);
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public String getChildPropertyId() {
		return child_property_id;
	}

	public void setChildPropertyId(String childPropertyId) {
		this.child_property_id = childPropertyId;
	}

}
