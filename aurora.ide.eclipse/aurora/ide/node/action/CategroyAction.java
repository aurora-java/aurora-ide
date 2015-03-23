package aurora.ide.node.action;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.core.ICategoryViewer;
import aurora.ide.helpers.LocaleMessage;



public class CategroyAction extends Action {
	
	private ICategoryViewer viewer;
	public CategroyAction(ICategoryViewer viewer) {
		this.viewer = viewer;
		setHoverImageDescriptor(getDefaultImageDescriptor());
	}
	public void run() {
		viewer.setCategory(true);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("category.icon"));
	}
}
