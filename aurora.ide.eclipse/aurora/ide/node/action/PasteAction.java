package aurora.ide.node.action;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.AbstractCMViewer;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;



public class PasteAction extends ActionListener {
	AbstractCMViewer viewer;

	public PasteAction(AbstractCMViewer viewer,int actionStyle) {
		this.viewer = viewer;
		setActionStyle(actionStyle);
	}

	public void run() {
		viewer.pasteElement();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("paste.icon"));
	}

	public String getDefaultText() {
		return LocaleMessage.getString("paste");
	}
	public Image getDefaultImage() {
		return ImagesUtils.getImage("paste.gif");
	}
}
