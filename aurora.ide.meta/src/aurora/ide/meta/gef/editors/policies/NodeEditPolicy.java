
package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import aurora.ide.meta.gef.editors.models.commands.DeleteComponentCommand;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;


public class NodeEditPolicy extends ComponentEditPolicy {

	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeleteComponentCommand deleteCommand = new DeleteComponentCommand();
		deleteCommand
				.setContainer((Container) getHost().getParent().getModel());
		deleteCommand.setChild((AuroraComponent) getHost().getModel());

		return deleteCommand;
	}
}
