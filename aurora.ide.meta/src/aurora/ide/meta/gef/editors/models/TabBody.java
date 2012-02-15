package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class TabBody extends Container {

	private static final long serialVersionUID = -9196440587781890208L;
	public static final String VISIBLE = "visible";
	@SuppressWarnings("rawtypes")
	private static Class[] unsupported = { Toolbar.class, Navbar.class,
			GridColumn.class, TabItem.class, TabBody.class };
	private boolean visible = false;

	@SuppressWarnings("rawtypes")
	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class cls = component.getClass();
		for (Class c : unsupported)
			if (c.equals(cls))
				return false;
		return super.isResponsibleChild(component);
	}

	public void setVisible(boolean v) {
		if (visible == v) {
			return;
		}
		boolean oldV = visible;
		visible = v;
		firePropertyChange(VISIBLE, oldV, v);
	}

	public boolean getVisible() {
		return visible;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

}
