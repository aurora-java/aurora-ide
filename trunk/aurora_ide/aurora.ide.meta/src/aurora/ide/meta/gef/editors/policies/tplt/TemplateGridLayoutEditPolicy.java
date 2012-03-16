package aurora.ide.meta.gef.editors.policies.tplt;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.policies.GridLayoutEditPolicy;

public class TemplateGridLayoutEditPolicy extends GridLayoutEditPolicy {
	protected Command getCreateCommand(CreateRequest request) {
		return super.getCreateCommand(request);
	}
	protected Command createAddCommand(EditPart child, EditPart after) {
		return super.createAddCommand(child, after);
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return super.createMoveChildCommand(child, after);
	}
}
