package aurora.ide.meta.gef.editors.components.eidtpolicy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import aurora.ide.meta.gef.editors.components.command.DeleteTreeNodeCommand;

public class TreeNodeDeleteableEditPolicy extends GraphicalEditPolicy {

	public static final String DELETE_NODE_POLICY = "DELETE_NODE_POLICY";

	@Override
	public Command getCommand(Request request) {
		if (REQ_DELETE.equals(request.getType())) {
			AbstractGraphicalEditPart parent = (AbstractGraphicalEditPart) getHost()
					.getParent();
			EditPolicy editPolicy = parent.getEditPolicy(LAYOUT_ROLE);
			if (editPolicy != null) {
				Request req = new Request(
						TreeExpandSupportEditPolicy.SIZE_CHANGED);
				Map hashMap = new HashMap();
				hashMap
						.put(TreeExpandSupportEditPolicy.SIZE_CHANGED,
								getHost());
				req.setExtendedData(hashMap);
				CompoundCommand c = new CompoundCommand();
				c.add(getDeleteCommand(request));
				c.add(editPolicy.getCommand(req));
				return c;
			}
		}
		return super.getCommand(request);
	}

	private Command getDeleteCommand(Request request) {
		GroupRequest req = (GroupRequest) request;
//		System.out.println("**********************");
//		System.out.println(req.getEditParts());
//		System.out.println(getHost());
//		System.out.println("**********************");
		return new DeleteTreeNodeCommand(req);
		// return new DeleteNodeCommand(this.getHost());
	}

}
