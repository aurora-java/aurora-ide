package patch.org.eclipse.jdt.internal.ui.viewsupport;

import org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ImageDescriptorRegistryAdapter extends ImageDescriptorRegistry {

	private ext.org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry reg;

	public ImageDescriptorRegistryAdapter(
			ext.org.eclipse.jdt.internal.ui.viewsupport.ImageDescriptorRegistry reg) {
		this.reg = reg;
	}

	@Override
	public Image get(ImageDescriptor descriptor) {
		return reg.get(descriptor);
	}

	@Override
	public void dispose() {
		reg.dispose();
	}

}
