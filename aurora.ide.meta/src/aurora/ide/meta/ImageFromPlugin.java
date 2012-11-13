package aurora.ide.meta;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ImageFromPlugin {
	private static final ImageRegistry Image_Registry = createImageRegistry();

	private final static ImageRegistry createImageRegistry() {

		// If we are in the UI Thread use that
		if (Display.getCurrent() != null) {
			return new ImageRegistry(Display.getCurrent());
		}

		if (PlatformUI.isWorkbenchRunning()) {
			return new ImageRegistry(PlatformUI.getWorkbench().getDisplay());
		}

		// Invalid thread access if it is not the UI Thread
		// and the workbench is not created.
		throw new SWTError(SWT.ERROR_THREAD_INVALID_ACCESS);
	}

	public static Image getImage(String key) {
		Image image = Image_Registry.get(key);
		if (image == null) {
			ImageDescriptor imageDescriptor = MetaPlugin
					.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, key);
			Image_Registry.put(key, imageDescriptor);
			image = Image_Registry.get(key);
		}
		return image;
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor image = Image_Registry.getDescriptor(key);
		if (image == null) {
			ImageDescriptor imageDescriptor = MetaPlugin
					.imageDescriptorFromPlugin(MetaPlugin.PLUGIN_ID, key);
			Image_Registry.put(key, imageDescriptor);
			image = Image_Registry.getDescriptor(key);
		}

		return image;
	}

}
