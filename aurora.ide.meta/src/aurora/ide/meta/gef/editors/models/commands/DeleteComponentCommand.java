package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;

import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.TabBody;

public class DeleteComponentCommand extends Command {
	protected Container container;
	protected AuroraComponent child;
	private int oriIndex = -1;

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public boolean canExecute() {
		if (child instanceof TabBody)
			return false;
		// if (child instanceof Container) {
		// if (((Container) child).getSectionType() != null)
		// return false;
		// }
		return super.canExecute();
	}

	@Override
	public boolean canUndo() {
		return super.canUndo();
	}

	public AuroraComponent getChild() {
		return child;
	}

	public void setChild(AuroraComponent child) {
		this.child = child;
	}

	// ------------------------------------------------------------------------
	// Overridden from Command

	public void execute() {
		oriIndex = container.getChildren().indexOf(child);
		container.removeChild(oriIndex);
	}

	public String getLabel() {
		return "Delete Component";
	}

	public void redo() {
		execute();
	}

	public void undo() {
		container.addChild(child, oriIndex);
	}
}