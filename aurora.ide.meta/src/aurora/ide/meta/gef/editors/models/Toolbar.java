package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Toolbar extends RowCol {

	public static final String TOOLBAR = "toolbar";
	static final long serialVersionUID = 1;

	public Toolbar() {
		this.row = 1;
		this.col = 999;
		this.headHight = 2;
		this.setType(TOOLBAR);
		this.setSize(new Dimension(1, 25));
	}

	public List<Button> getButtons() {
		List<AuroraComponent> list = getChildren();
		List<Button> btns = new ArrayList<Button>(list.size());
		for (int i = 0; i < list.size(); i++)
			btns.add((Button) list.get(i));
		return btns;
	}

	/**
	 * 
	 * */
	public boolean isResponsibleChild(AuroraComponent child) {
		return child instanceof Button;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

}
