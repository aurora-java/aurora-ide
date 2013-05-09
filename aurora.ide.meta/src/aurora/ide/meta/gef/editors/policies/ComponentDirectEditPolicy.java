package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import aurora.ide.meta.gef.editors.models.commands.ComponentPropertyEditCommand;
import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class ComponentDirectEditPolicy extends DirectEditPolicy {

	protected Command getDirectEditCommand(DirectEditRequest request) {
		ComponentPropertyEditCommand cmd = new ComponentPropertyEditCommand(
				(AuroraComponent) getHost().getModel(),
				(String) request.getDirectEditFeature(), request
						.getCellEditor().getValue());
		return cmd;
	}

	protected void showCurrentEditValue(DirectEditRequest request) {
		// String value = (String) request.getCellEditor().getValue();
		// ((InputField) getHostFigure()).setName(value);
	}

}
