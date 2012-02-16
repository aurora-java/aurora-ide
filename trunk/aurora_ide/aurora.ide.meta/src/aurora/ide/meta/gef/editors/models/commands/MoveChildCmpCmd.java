package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.TabBody;

public class MoveChildCmpCmd extends Command {
	private Container container;
	private AuroraComponent acToMove;
	private AuroraComponent acRel = null;
	private int oriIndex = -1;

	public MoveChildCmpCmd() {
	}

	public void setComponentToMove(AuroraComponent child) {
		container = child.getParent();
		acToMove = child;
	}

	public void setReferenceComponent(AuroraComponent ac) {
		acRel = ac;
	}

	@Override
	public String getLabel() {
		return "Move Component";
	}

	@Override
	public boolean canExecute() {
		return super.canExecute()
				&& (!(acToMove.getClass().equals(TabBody.class)));
	}

	@Override
	public boolean canUndo() {
		return super.canUndo()
				&& (!(acToMove.getClass().equals(TabBody.class)));
	}

	@Override
	public void execute() {
		List<AuroraComponent> children = container.getChildren();
		oriIndex = children.indexOf(acToMove);
		container.removeChild(acToMove);
		if (acRel == null) {
			container.addChild(acToMove);
		} else {
			int idx = children.indexOf(acRel);
			container.addChild(acToMove, idx);
		}
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		List<AuroraComponent> children = container.getChildren();
		children.remove(acToMove);
		container.addChild(acToMove, oriIndex);
	}
}