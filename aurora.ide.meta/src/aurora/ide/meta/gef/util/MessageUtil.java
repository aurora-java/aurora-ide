package aurora.ide.meta.gef.util;

import aurora.ide.meta.gef.i18n.Messages;
import aurora.plugin.source.gen.screen.model.Button;

public class MessageUtil {
	static public String getButtonText(Button button) {
		String buttonType = button.getButtonType();
		if (Button.CLEAR.equals(buttonType))
			return "清除";
		else if (Button.ADD.equals(buttonType))
			return "增加";
		else if (Button.DELETE.equals(buttonType))
			return "删除";
		else if (Button.SAVE.equals(buttonType))
			return "保存";
		else if (Button.EXCEL.equals(buttonType))
			return "导出";
		return "button";
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
