package aurora.ide.meta.gef.editors.policies.tplt;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;

import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class TemplateNodeEditPolicy extends NodeEditPolicy {
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		if (parent instanceof ViewDiagram) {
			return null;
		}
		if (!isAllowDel()) {
			return null;
		}
		return super.createDeleteCommand(deleteRequest);
	}

	private boolean isAllowDel() {
		Object model = getHost().getModel();
		return !TemplateModeUtil.isBindTemplate(model);
	}

	
}
