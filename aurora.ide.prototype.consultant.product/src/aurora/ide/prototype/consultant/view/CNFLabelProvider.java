package aurora.ide.prototype.consultant.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import aurora.ide.libs.AuroraImagesUtils;

public class CNFLabelProvider extends LabelProvider implements ILabelProvider,
		IDescriptionProvider {
	public String getText(Object element) {
		if (element instanceof Node) {
			return ((Node) element).getPath().removeFileExtension()
					.lastSegment();
		}
		return null;
	}

	public String getDescription(Object element) {
		String text = getText(element);
		return "This is a description of " + text;
	}

	public Image getImage(Object element) {
		if (element instanceof Node) {
			if (((Node) element).getFile().isDirectory()) {
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_FOLDER);
			} else {
				return  AuroraImagesUtils.getImage("/meta.png");
			}
		}
		return null;
	}
}
