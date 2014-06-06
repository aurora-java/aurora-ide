package aurora.ide.meta.gef.editors.components.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;

/**
 * @author shily Created on Feb 16, 2009
 */
public class CreateTreeNodeCommand extends Command {
	private CustomTreeContainerNode parent;
	private AuroraComponent treeNode;
	private CreateRequest request;

	public CreateTreeNodeCommand(String label, Object parent,
			CreateRequest request) {
		super(label);
		this.setParent((CustomTreeContainerNode) parent);
		this.request = request;
	}

	private void setParent(CustomTreeContainerNode parent) {
		this.parent = parent;
	}

	public CustomTreeContainerNode getParent() {
		return parent;
	}

	@Override
	public boolean canExecute() {
		return this.getParent() != null
				&& (request.getNewObject() instanceof CustomTreeNode || request
						.getNewObject() instanceof CustomTreeContainerNode);
	}

	@Override
	public boolean canUndo() {
		return treeNode != null;
	}

	@Override
	public void execute() {
		treeNode = (AuroraComponent) request.getNewObject();
		parent.addNode(treeNode);
		if (treeNode instanceof CustomTreeContainerNode)
			treeNode.setName("folder");
		else
			treeNode.setName("file");
	}

	@Override
	public void redo() {
		super.redo();
	}

	@Override
	public void undo() {
		super.undo();
		parent.removeNode(treeNode);
	}

}
