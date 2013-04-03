package aurora.plugin.source.gen.screen.model;

import java.util.ArrayList;
import java.util.List;

import aurora.plugin.source.gen.screen.model.properties.IPropertyDescriptor;

public class Toolbar extends RowCol {

	public static final String TOOLBAR = "toolbar";

	public Toolbar() {
		// this.row = 1;
		// this.col = 999;
		this.headHight = 2;
		this.setRow(1);
		this.setCol(999);
		this.setComponentType(TOOLBAR);
		this.setSize(1, 25);
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


}
