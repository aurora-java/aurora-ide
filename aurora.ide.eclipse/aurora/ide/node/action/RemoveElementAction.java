package aurora.ide.node.action;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.AbstractCMViewer;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;



public class RemoveElementAction extends ActionListener {
	private AbstractCMViewer viewer;

	public RemoveElementAction(AbstractCMViewer viewer,int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		viewer.removeElement();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("delete.icon"));
	}

	public String getDefaultText() {
		return LocaleMessage.getString("delete");
	}
	public Image getDefaultImage() {
		return ImagesUtils.getImage("delete.gif");
	}
}
