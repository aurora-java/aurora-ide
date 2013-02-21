package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Navbar extends AuroraComponent {

	static final long serialVersionUID = 1;
	private String navbarType = "complex";

	public Navbar() {
		this.setSize(new Dimension(1, 25));
		this.setType("navbar");
	}

	public String getNavBarType() {
		return navbarType;
	}

	/**
	 * Grid.NAVBAR_SIMPLE or Grid.NAVBAR_COMPLEX
	 */
	public void setNavBarType(String type) {
		String oldType = this.navbarType;
		this.navbarType = type;
		if (type != Grid.NAVBAR_NONE) {
			firePropertyChange("NavBarType", oldType, type);
		}
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}
}
