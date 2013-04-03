package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

import aurora.plugin.source.gen.screen.model.TabItem;

public class TabItemChangEditPolicy extends GraphicalEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (REQ_OPEN.equals(request.getType())) {
			Command command = new Command() {

				@Override
				public boolean canUndo() {
					return false;
				}

				@Override
				public void execute() {
					((TabItem) getHost().getModel()).setCurrent(true);
				}

			};
			return command;
		}
		return null;
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return super.getTargetEditPart(request);
	}

	@Override
	public boolean understandsRequest(Request req) {
		return true;
	}
}
