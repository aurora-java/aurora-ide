package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.TabBody;

public class MoveChildCmpCmd extends Command {
	private Container container;
	private EditPart epParent;
	private AuroraComponent acToMove;
	private AuroraComponent acRel = null;
	private int oriIndex = -1;

	public MoveChildCmpCmd() {
	}

	public void setEditPartToMove(EditPart child) {
		epParent = child.getParent();
		container = (Container) epParent.getModel();
		acToMove = (AuroraComponent) child.getModel();
	}

	public void setReferenceEditPart(EditPart reference) {
		if (reference != null)
			acRel = (AuroraComponent) reference.getModel();
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