package aurora.ide.node.action;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;



public class RefreshAction extends ActionListener {
	IViewer viewer;

	public RefreshAction(IViewer viewer,int actionStyle) {
		setActionStyle(actionStyle);
		this.viewer = viewer;
	}

	public void run() {
		viewer.refresh(false);
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("refresh.icon"));
	}
	public String getDefaultText(){
		return LocaleMessage.getString("refresh");
	}
	public Image getDefaultImage() {
		return ImagesUtils.getImage("refresh.gif");
	}
	 
}
