package aurora.ide.search.ui;


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;

public class SearchImages {

	// The plugin registry
	private final static ImageRegistry PLUGIN_REGISTRY = AuroraPlugin
			.getDefault().getImageRegistry();

	public static final IPath ICONS_PATH = new Path("$nl$/icons/search/"); //$NON-NLS-1$

	public static final String LINE_MATCH = "line_match.gif";

	public static Image getImage(String key) {
		Image image = PLUGIN_REGISTRY.get(key);
		if (image == null) {
			IPath append = ICONS_PATH.append(key);
			ImageDescriptor imageDescriptor = AuroraPlugin
					.getImageDescriptor(append.toString());
			PLUGIN_REGISTRY.put(key, imageDescriptor);
			image = PLUGIN_REGISTRY.get(key);
		}

		return image;
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor image = PLUGIN_REGISTRY.getDescriptor(key);
		if (image == null) {
			IPath append = ICONS_PATH.append(key);
			ImageDescriptor imageDescriptor = AuroraPlugin
					.getImageDescriptor(append.toString());
			PLUGIN_REGISTRY.put(key, imageDescriptor);
			image = PLUGIN_REGISTRY.getDescriptor(key);
		}

		return image;
	}

}
