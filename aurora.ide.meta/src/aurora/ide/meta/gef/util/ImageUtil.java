package aurora.ide.meta.gef.util;

import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.property.PropertySourceUtil;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Button;
import aurora.plugin.source.gen.screen.model.ButtonClicker;
import aurora.plugin.source.gen.screen.model.IDialogEditableObject;

public class ImageUtil {
	public static Image getImage(IDialogEditableObject value){
//		Object contextInfo = value.getContextInfo();
		if(value instanceof ButtonClicker){
			AuroraComponent targetComponent = ((ButtonClicker)value).getTargetComponent();
			if(targetComponent!=null){
				return PropertySourceUtil.getImageOf(targetComponent);
			}
		}
		return null;
		
//		if (targetComponent != null
//				&& (B_SEARCH.equals(actionID) || B_SAVE.equals(actionID) || B_RESET
//						.equals(actionID)))
//			return PropertySourceUtil.getImageOf(targetComponent);
		
	}
}
