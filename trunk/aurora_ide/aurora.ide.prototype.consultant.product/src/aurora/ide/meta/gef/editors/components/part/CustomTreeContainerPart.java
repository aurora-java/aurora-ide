package aurora.ide.meta.gef.editors.components.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.components.eidtpolicy.TextStyleSupport;
import aurora.ide.meta.gef.editors.components.eidtpolicy.TreeExpandSupportEditPolicy;
import aurora.ide.meta.gef.editors.components.figure.BackTreeLayout;
import aurora.ide.meta.gef.editors.components.figure.TreeNodeFigure;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;
import aurora.ide.meta.gef.editors.policies.PasteComponentEditPolicy;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CustomTreeContainerPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		TreeNodeFigure image = new TreeNodeFigure(getTreeContainer());
//		image.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
		// image.setLayoutManager(new TreeContainerLayoutManager());
		return image;
	}

	protected List getModelChildren() {
		return this.getTreeContainer().getChildren();
	}

	private CustomTreeContainerNode getTreeContainer() {
		return (CustomTreeContainerNode) getModel();
	}

	@Override
	protected void refreshVisuals() {
		// super.refreshVisuals();
		((TreeNodeFigure) this.getFigure()).refreshVisuals();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (CustomTreeContainerNode.CONTAINER_EXPAND.equals(prop))
			refreshChildren();
	}

	// public void expand(boolean expand) {
	// ((TreeNodeContainerFigure) this.getFigure()).expand(expand);
	// }

	@Override
	protected void createEditPolicies() {

		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new ContainerLayoutEditPolicy());
		installEditPolicy("Paste Components", new PasteComponentEditPolicy());

		// installEditPolicy(EditPolicy.CONTAINER_ROLE,
		// new TreeNodeContainerEditPolicy());
		// installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
		// new NonResizableEditPolicy());
		// installEditPolicy(TreeNodeDeleteableEditPolicy.DELETE_NODE_POLICY,
		// new TreeNodeDeleteableEditPolicy());
		installEditPolicy(TreeExpandSupportEditPolicy.EXPAND_SUPPORT,
				new TreeExpandSupportEditPolicy());
		// installEditPolicy(EditPolicy.LAYOUT_ROLE,
		// new TreeConstrainedLayoutEditPolicy());

	}
	public void performRequest(Request req) {
		new TextStyleSupport(this,ComponentProperties.prompt).performRequest(req);
	}
	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}

	public Rectangle layout() {
		// return super.layout();
		return new BackTreeLayout().layout(this);
	}
	// public int getResizeDirection() {
	// return NSEW;
	// }
}
