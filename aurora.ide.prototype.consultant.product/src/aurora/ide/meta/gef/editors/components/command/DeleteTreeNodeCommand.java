package aurora.ide.meta.gef.editors.components.command;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;

import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;

public class DeleteTreeNodeCommand extends Command {

	private Object node;
	private Object parent;
	private GroupRequest req;
	private int size = 1;

	public DeleteTreeNodeCommand(EditPart editPart) {
		init(editPart);
	}

	private void init(EditPart editPart) {
		node = editPart.getModel();
		parent = editPart.getParent().getModel();
	}

	public DeleteTreeNodeCommand(GroupRequest req) {
		this.req = req;
		List editParts = req.getEditParts();
		if (editParts.size() != 1)
			this.size = -1;
		init((EditPart) editParts.get(0));
	}

	@Override
	public boolean canExecute() {
		return (parent instanceof CustomTreeContainerNode && node instanceof CustomTreeNode)
				&& size == 1;
	}

	public void undo() {
		// super.undo();
		// parent.removeNode(treeNode);
	}

	public void execute() {
		CustomTreeContainerNode p = (CustomTreeContainerNode) parent;
		p.removeNode((CustomTreeNode) node);
	}
}
