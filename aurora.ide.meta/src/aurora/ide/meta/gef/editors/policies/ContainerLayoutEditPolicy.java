package aurora.ide.meta.gef.editors.policies;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

import aurora.ide.meta.MetaPlugin;
import aurora.ide.meta.gef.editors.models.commands.CreateComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveChildCmpCmd;
import aurora.ide.meta.gef.editors.models.commands.MoveRemoteChildCmpCmd;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.ide.meta.gef.editors.parts.TabBodyPart;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;
import aurora.ide.meta.gef.editors.parts.TabItemPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;

public class ContainerLayoutEditPolicy extends FlowLayoutEditPolicy {
	private EditPart targetEditPart = null;

	@Override
	protected void decorateChild(EditPart child) {
		super.decorateChild(child);
	}

	@Override
	protected void decorateChildren() {
		super.decorateChildren();
	}

	@Override
	public Command getCommand(Request request) {
		if (MetaPlugin.isDemonstrate)
			return null;
		return super.getCommand(request);
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		targetEditPart = super.getTargetEditPart(request);
		return targetEditPart;
	}

	@Override
	public void showTargetFeedback(Request request) {
		if (MetaPlugin.isDemonstrate)
			return ;
		if (targetEditPart != null
				&& targetEditPart.getClass().equals(TabFolderPart.class)) {
			if (request instanceof DropRequest
					&& (!REQ_RESIZE.equals(request.getType()))) {
				ComponentPart ref = (ComponentPart) getInsertionReference(request);
				if (ref == null || (ref instanceof TabBodyPart)) {
					TabItemPart lastTabItem = null;
					for (Object ep : targetEditPart.getChildren()) {
						if (ep instanceof TabItemPart)
							lastTabItem = (TabItemPart) ep;
					}
					if (lastTabItem != null) {
						Rectangle rect = lastTabItem.getFigure().getBounds()
								.getShrinked(-4, -2);
						Polyline linefb = getLineFeedback();
						linefb.setStart(rect.getTopRight());
						linefb.setEnd(rect.getBottomRight());
						// getFeedbackLayer().add(linefb);
						return;
					}
				}
			}
		}
		super.showTargetFeedback(request);
	}

	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObject() instanceof AuroraComponent) {
			Container parentModel = (Container) getHost().getModel();
			AuroraComponent ac = (AuroraComponent) request.getNewObject();
			if (!parentModel.isResponsibleChild(ac)) {
				return null;
			}
			EditPart reference = getInsertionReference(request);
			CreateComponentCommand cmd = new CreateComponentCommand();
			cmd.setTargetContainer(parentModel);
			cmd.setChild(ac);
			cmd.setReferenceModel((AuroraComponent) (reference == null ? null
					: reference.getModel()));
			return cmd;
		}
		return null;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		if (targetEditPart == null)
			return null;
		MoveRemoteChildCmpCmd cmd = new MoveRemoteChildCmpCmd();
		cmd.setComponentToMove((AuroraComponent) child.getModel());
		if (targetEditPart.getModel() instanceof Container) {
			Container dest = (Container) targetEditPart.getModel();
			AuroraComponent ac = (AuroraComponent) child.getModel();
			if (!dest.isResponsibleChild(ac))
				return null;
			cmd.setTargetContainer(dest);
		}
		cmd.setReferenceComponent(after == null ? null
				: (AuroraComponent) after.getModel());
		return cmd;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		MoveChildCmpCmd cmd = new MoveChildCmpCmd();
		cmd.setComponentToMove((AuroraComponent) child.getModel());
		cmd.setReferenceComponent(after == null ? null
				: (AuroraComponent) after.getModel());
		return cmd;
	}

	protected boolean isLayoutHorizontal() {
		EditPart part = getHost();
		if (part instanceof ContainerPart) {
			return ((ContainerPart) part).isLayoutHorizontal();
			// Class<? extends Object> modelClass = part.getModel().getClass();
			// if (modelClass.equals(HBox.class)
			// || modelClass.equals(FieldSet.class)
			// || modelClass.equals(Form.class)
			// || modelClass.equals(Toolbar.class)
			// || modelClass.equals(TabFolder.class))
			// return true;
		}
		return false;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		ResizeComponentEditPolicy p = new ResizeComponentEditPolicy();
		ComponentPart cp = (ComponentPart) child;
		p.setResizeDirections(cp.getResizeDirection());
		return p;
	}

}