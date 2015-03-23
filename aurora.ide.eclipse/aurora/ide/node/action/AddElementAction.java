package aurora.ide.node.action;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.core.IViewer;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.ImagesUtils;
import aurora.ide.helpers.LocaleMessage;


import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;

public class AddElementAction extends ActionListener{
	
	protected IViewer viewer;
	protected CompositeMap currentNode;
	protected QualifiedName childQN;
	private String text = null;
	public AddElementAction(IViewer viewer, CompositeMap currentNode, QualifiedName childQN,int actionStyle) {
		this.viewer = viewer;
		this.currentNode = currentNode;
		this.childQN = childQN;
		setActionStyle(actionStyle);
	}
	public AddElementAction(IViewer viewer, CompositeMap currentNode, QualifiedName childQN,String text,int actionStyle) {
		this.viewer = viewer;
		this.currentNode = currentNode;
		this.childQN = childQN;
		this.text = text;
		setActionStyle(actionStyle);
	}
	public void run() {
		CompositeMapUtil.addElement(currentNode, childQN);
		if (viewer != null) {
			viewer.refresh(true);
		}
	}
	public void handleEvent(Event event) {
		run();
	}

	public ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("element.icon"));
	}
	public String getDefaultText() {
		if(text == null){
			if(currentNode == null||childQN==null)
				return "";
			String prefix = CompositeMapUtil.getContextPrefix(currentNode, childQN);
			childQN.setPrefix(prefix);
			text = childQN.getFullName();
		}
		return text;
	}
	public Image getDefaultImage() {
		return ImagesUtils.getImage("element.gif");
	}

}
