package aurora.ide.meta.gef.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import aurora.ide.helpers.ImagesUtils;

public class PrototypeImagesUtils {

	// The plugin registry
//	private final static ImageRegistry PLUGIN_REGISTRY = AuroraPlugin
//			.getDefault().getImageRegistry();
//
//	public static final IPath ICONS_PATH = new Path("$nl$/icons/"); //$NON-NLS-1$
//
//	private static Map<String, String> keyPathMap = new HashMap<String, String>() {
//		{
//			this.put("toolbar_bg", "toolbar_bg.gif");
//			this.put("itembar", "itembar.gif");
//			this.put("btn", "btn.gif");
//			this.put("grid_bg", "grid_bg.gif");
//			this.put("navigation", "navigation.gif");
//			this.put("toolbar_sep", "toolbar_sep.gif");
//			this.put("nav1", "nav1.png");
//			this.put("nav2", "nav2.png");
//			this.put("label", "label.png");
//		}
//	};

	public static Image getImage(String key) {
//		Image image = PLUGIN_REGISTRY.get(key);
//		if (image == null) {
//			IPath append = ICONS_PATH.append(getPath(key));
//			ImageDescriptor imageDescriptor = AuroraPlugin
//					.getImageDescriptor(append.toString());
//			PLUGIN_REGISTRY.put(key, imageDescriptor);
//			image = PLUGIN_REGISTRY.get(key);
//		}
//
//		return image;
		return ImagesUtils.getImage(key);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
//		ImageDescriptor image = PLUGIN_REGISTRY.getDescriptor(key);
//		if (image == null) {
//			IPath append = ICONS_PATH.append(getPath(key));
//			ImageDescriptor imageDescriptor = AuroraPlugin
//					.getImageDescriptor(append.toString());
//			PLUGIN_REGISTRY.put(key, imageDescriptor);
//			image = PLUGIN_REGISTRY.getDescriptor(key);
//		}

//		return image;
		return ImagesUtils.getImageDescriptor(key);
	}

//	private static String getPath(String key) {
//		String path = keyPathMap.get(key);
//		return path == null ? key : path;
//	}
}
