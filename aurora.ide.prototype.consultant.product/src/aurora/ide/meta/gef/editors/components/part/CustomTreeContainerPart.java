package aurora.ide.meta.gef.editors.components.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.components.eidtpolicy.TreeConstrainedLayoutEditPolicy;
import aurora.ide.meta.gef.editors.components.eidtpolicy.TreeExpandSupportEditPolicy;
import aurora.ide.meta.gef.editors.components.eidtpolicy.TreeNodeContainerEditPolicy;
import aurora.ide.meta.gef.editors.components.eidtpolicy.TreeNodeDeleteableEditPolicy;
import aurora.ide.meta.gef.editors.components.figure.TreeContainerLayoutManager;
import aurora.ide.meta.gef.editors.components.figure.TreeLayoutManager;
import aurora.ide.meta.gef.editors.components.figure.TreeNodeContainerFigure;
import aurora.ide.meta.gef.editors.components.figure.TreeNodeFigure;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CustomTreeContainerPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		TreeNodeContainerFigure image = new TreeNodeContainerFigure(
				PrototypeImagesUtils.getImage("palette/itembar_02.png"));
		image.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
		image.setLayoutManager(new TreeContainerLayoutManager());
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
		super.refreshVisuals();
		((TreeNodeFigure) this.getFigure())
				.refreshVisuals( (AuroraComponent) getModel());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
	}

	public void expand(boolean expand) {
		((TreeNodeContainerFigure) this.getFigure()).expand(expand);
	}

	@Override
	protected void createEditPolicies() {

		installEditPolicy(EditPolicy.CONTAINER_ROLE,
				new TreeNodeContainerEditPolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
				new NonResizableEditPolicy());
		installEditPolicy(TreeNodeDeleteableEditPolicy.DELETE_NODE_POLICY,
				new TreeNodeDeleteableEditPolicy());
		installEditPolicy(TreeExpandSupportEditPolicy.EXPAND_SUPPORT,
				new TreeExpandSupportEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new TreeConstrainedLayoutEditPolicy());

	}

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}
}
