package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.models.commands.CreateComponentCommand;
import aurora.ide.meta.gef.editors.models.commands.MoveRemoteChildCmpCmd;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.QueryFormToolBarPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;

public class QueryFormLayoutEditPolicy extends FlowLayoutEditPolicy {

	private EditPart targetEditPart;

	protected Command getCreateCommand(CreateRequest request) {
		if (shouldIgonre())
			return null;
		if (request.getNewObject() instanceof AuroraComponent) {
			EditPart host = getHost();
			Container parentModel = (Container) host.getModel();
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

	@Override
	public EditPart getTargetEditPart(Request request) {
		targetEditPart = super.getTargetEditPart(request);
		if (targetEditPart instanceof QueryFormToolBarPart) {
			targetEditPart = targetEditPart.getParent();
		}
		return targetEditPart;
	}

	protected boolean shouldIgonre() {
		return targetEditPart instanceof QueryFormToolBarPart;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		ResizeComponentEditPolicy p = new ResizeComponentEditPolicy();
		ComponentPart cp = (ComponentPart) child;
		p.setResizeDirections(cp.getResizeDirection());
		return p;
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
		return null;
	}

	protected boolean isLayoutHorizontal() {
		return false;
	}

	@Override
	protected void showLayoutTargetFeedback(Request request) {
		if (shouldIgonre()) {
			return;
		}
		super.showLayoutTargetFeedback(request);
	}

}