package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Navbar extends RowCol {

	static final long serialVersionUID = 1;

	public Navbar() {
		this.row = 1;
		this.col = 999;
		this.headHight = 2;
		this.setSize(new Dimension(1, 25));
	}

	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	// navBarType = "complex" 或者 "simple";
	public void setType(String type) {
		super.setType(type);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

	/**
	 * 
	 * 不允许增加child，外观使用图片显示
	 * 
	 * */
	public boolean isResponsibleChild(AuroraComponent component) {
		return false;
	}

}
