package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Toolbar extends RowCol {

	static final long serialVersionUID = 1;

	public Toolbar() {
		this.row = 1;
		this.col = 999;
		this.headHight = 2;
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
	 * 仅允许增加 Button
	 * */
	public boolean isResponsibleChild(AuroraComponent child) {
		return child instanceof Button;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

}
