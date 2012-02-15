
package aurora.ide.meta.gef.editors.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;


public class NodeDirectEditPolicy extends DirectEditPolicy{

    protected Command getDirectEditCommand(DirectEditRequest request) {
//        RenameNodeCommand cmd = new RenameNodeCommand();
//        cmd.setNode((Node) getHost().getModel());
//        cmd.setName((String) request.getCellEditor().getValue());
//        return cmd;
        return null;
    }
    protected void showCurrentEditValue(DirectEditRequest request) {
        String value = (String) request.getCellEditor().getValue();
//        ((NodeFigure) getHostFigure()).setName(value);
    }
}
