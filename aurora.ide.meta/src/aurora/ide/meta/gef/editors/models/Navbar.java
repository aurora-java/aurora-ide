package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Navbar extends RowCol {

	public static final String NAVBAR = "navbar";
	static final long serialVersionUID = 1;

	public Navbar() {
		this.row = 1;
		this.col = 999;
		this.headHight = 2;
		this.setSize(new Dimension(1, 25));
		this.setType(NAVBAR);
	}

	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	/**
	 * Grid.NAVBAR_SIMPLE or Grid.NAVBAR_COMPLEX
	 */
	public void setType(String type) {
		super.setType(type);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

	public boolean isResponsibleChild(AuroraComponent component) {
		// not allow any child
		return false;
	}

}
