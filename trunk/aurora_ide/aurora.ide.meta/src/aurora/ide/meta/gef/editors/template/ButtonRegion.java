package aurora.ide.meta.gef.editors.template;

import java.util.ArrayList;
import java.util.List;

public class ButtonRegion extends Region {

	private List<Button> buttons=new ArrayList<Button>();

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

}
