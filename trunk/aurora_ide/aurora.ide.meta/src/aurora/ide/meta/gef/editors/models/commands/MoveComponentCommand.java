package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.TabBody;

public class MoveComponentCommand extends Command {
	private AuroraComponent node;

	private Point oldPos;

	private Point newPos;

	public void setLocation(Point p) {
		this.newPos = p;
	}

	public void setNode(AuroraComponent node) {
		this.node = node;
	}

	public void execute() {
		oldPos = this.node.getLocation();
		node.setLocation(newPos);
	}

	public String getLabel() {
		return "Move Component";
	}

	public void redo() {
		this.node.setLocation(newPos);
	}

	public void undo() {
		this.node.setLocation(oldPos);
	}

	@Override
	public boolean canExecute() {
		return super.canExecute() && (!(node.getClass().equals(TabBody.class)));
	}

	@Override
	public boolean canUndo() {
		return super.canUndo() && (!(node.getClass().equals(TabBody.class)));
	}
}