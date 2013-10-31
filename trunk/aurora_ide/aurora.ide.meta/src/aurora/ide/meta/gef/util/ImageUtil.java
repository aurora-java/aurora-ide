package aurora.ide.meta.gef.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import aurora.ide.libs.AuroraImagesUtils;
import aurora.ide.meta.gef.editors.property.PropertySourceUtil;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.ButtonClicker;
import aurora.plugin.source.gen.screen.model.DialogEditableObject;
import aurora.plugin.source.gen.screen.model.IDialogEditableObject;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

public class ImageUtil {
	public static Image getImage(IDialogEditableObject value){
//		Object contextInfo = value.getContextInfo();
		if(value instanceof ButtonClicker){
			AuroraComponent targetComponent = ((ButtonClicker)value).getTargetComponent();
			if(targetComponent!=null){
				return PropertySourceUtil.getImageOf(targetComponent);
			}
		}
		if (value instanceof DialogEditableObject) {
			if (ComponentInnerProperties.ICON_BYTES_DATA
					.equals(((DialogEditableObject) value).getPropertyId())) {
				Object data = ((DialogEditableObject) value).getData();
				if (data != null && "".equals(data) == false) {
					byte[] iconByteData = AuroraImagesUtils.toBytes(data
							.toString());
					if (iconByteData != null) {
						ImageData idd = AuroraImagesUtils
								.toImageData(iconByteData);
						ImageDescriptor id = ImageDescriptor
								.createFromImageData(idd);
						Image image = id.createImage();
						return image;
					}
				}
			}
		}
		return null;
		
//		if (targetComponent != null
//				&& (B_SEARCH.equals(actionID) || B_SAVE.equals(actionID) || B_RESET
//						.equals(actionID)))
//			return PropertySourceUtil.getImageOf(targetComponent);
		
	}
}
