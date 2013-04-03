package aurora.ide.meta.gef.editors.policies.tplt;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;
import aurora.plugin.source.gen.screen.model.ScreenBody;

public class TemplateDiagramLayoutEditPolicy extends
		ContainerLayoutEditPolicy {
	protected Command getCreateCommand(CreateRequest request) {
		Object parent = getHost().getModel();
		if (parent instanceof ScreenBody) {
			return null;
		}
		return super.getCreateCommand(request);
	}

	protected Command createAddCommand(EditPart child, EditPart after) {
		Object parent = getHost().getModel();
		if (parent instanceof ScreenBody) {
			return null;
		}
		return super.createAddCommand(child, after);
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return super.createMoveChildCommand(child, after);
	}

}
