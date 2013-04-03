package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;


public class Navbar extends AuroraComponent {

	static final long serialVersionUID = 1;
//	private String navbarType = Grid.NAVBAR_NONE;

	public Navbar() {
		this.setSize(1, 25);
		this.setComponentType("navbar");
		this.setNavBarType(Grid.NAVBAR_NONE);
	}

	public String getNavBarType() {
//		return navbarType;
		return this.getStringPropertyValue(ComponentInnerProperties.GRID_NAVBAR);
	}

	/**
	 * Grid.NAVBAR_SIMPLE or Grid.NAVBAR_COMPLEX
	 */
	public void setNavBarType(String type) {
//		String oldType = this.navbarType;
//		this.navbarType = type;
//		if (type != Grid.NAVBAR_NONE) {
//			firePropertyChange("NavBarType", oldType, type);
//		}
		this.setPropertyValue(ComponentInnerProperties.GRID_NAVBAR, type);
	}

}
