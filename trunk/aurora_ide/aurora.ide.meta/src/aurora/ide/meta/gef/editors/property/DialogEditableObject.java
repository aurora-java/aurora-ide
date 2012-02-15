package aurora.ide.meta.gef.editors.property;

import org.eclipse.swt.graphics.Image;

public interface DialogEditableObject {
	String getDescripition();

	Image getDisplayImage();

	Object getContextInfo();

	DialogEditableObject clone();

}
