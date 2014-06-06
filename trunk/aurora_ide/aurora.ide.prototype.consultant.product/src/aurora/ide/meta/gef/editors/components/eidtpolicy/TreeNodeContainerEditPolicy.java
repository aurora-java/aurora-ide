package aurora.ide.meta.gef.editors.components.eidtpolicy;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

import aurora.ide.meta.gef.editors.components.command.CreateTreeNodeCommand;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;

public class TreeNodeContainerEditPolicy extends AbstractEditPolicy {

	/**
	 * @see ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		CreateTreeNodeCommand createTreeNodeCommand = new CreateTreeNodeCommand(
				"Create Tree Node", this.getHost().getModel(), request);
		return createTreeNodeCommand;
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		if (REQ_CREATE.equals(request.getType())
				&& request instanceof CreateRequest) {
			CreateRequest req = (CreateRequest) request;
			Object newObjectType = req.getNewObjectType();
			if (newObjectType.equals(CustomTreeContainerNode.class)
					|| newObjectType.equals(CustomTreeNode.class))
				return getHost();
		}
		return super.getTargetEditPart(request);

	}

	public Command getCommand(Request request) {
		if (REQ_CREATE.equals(request.getType()))
			return getCreateCommand((CreateRequest) request);
		if (REQ_DELETE.equals(request.getType()))
			return getDeleteCommand((GroupRequest) request);
		return null;
	}

	private Command getDeleteCommand(GroupRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
