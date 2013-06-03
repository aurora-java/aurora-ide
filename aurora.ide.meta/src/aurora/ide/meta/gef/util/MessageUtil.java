package aurora.ide.meta.gef.util;

import aurora.ide.meta.gef.i18n.Messages;
import aurora.plugin.source.gen.screen.model.Button;

public class MessageUtil {
	static public String getButtonText(Button button) {
		String buttonType = button.getButtonType();
		if (Button.CLEAR.equals(buttonType))
			return Messages.MessageUtil_Toolbar_button_clear;
		else if (Button.ADD.equals(buttonType))
			return Messages.MessageUtil_Toolbar_button_add;
		else if (Button.DELETE.equals(buttonType))
			return Messages.MessageUtil_Toolbar_button_del;
		else if (Button.SAVE.equals(buttonType))
			return Messages.MessageUtil_Toolbar_button_save;
		else if (Button.EXCEL.equals(buttonType))
			return Messages.MessageUtil_Toolbar_button_exp;
		return button.getText();
	}

	public static final String[] button_action_texts() {
		return new String[] { Messages.ButtonClicker_Query,
				Messages.ButtonClicker_Reset, Messages.ButtonClicker_Save,
				Messages.ButtonClicker_Open, Messages.ButtonClicker_Close,
				Messages.ButtonClicker_Custom };
	}
	public static final String[] INNER_RENDERER_DESC() { 
		return new String[]{ Messages.Renderer_10,
				Messages.Renderer_11, Messages.Renderer_12 };
	} 
}
