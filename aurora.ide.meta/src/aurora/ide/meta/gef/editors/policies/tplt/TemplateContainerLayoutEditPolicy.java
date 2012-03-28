package aurora.ide.meta.gef.editors.policies.tplt;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;
import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;

public class TemplateContainerLayoutEditPolicy extends
		ContainerLayoutEditPolicy {
	protected Command getCreateCommand(CreateRequest request) {
		return super.getCreateCommand(request);
	}

	protected Command createAddCommand(EditPart child, EditPart after) {
		EditPart childParent = child.getParent();
		if (childParent instanceof ViewDiagramPart) {
			return null;
		}
		if (TemplateModeUtil.isBindTemplate(child.getModel())) {
			return null;
		}
		return super.createAddCommand(child, after);
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return super.createMoveChildCommand(child, after);
	}

}
