package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.TabBody;

public class MoveRemoteChildCmpCmd extends Command {
	private Container srcContainer;
	private Container destContainer;
	private AuroraComponent acToMove;
	private AuroraComponent acReference = null;
	private int oriIndex = -1;

	public MoveRemoteChildCmpCmd() {
	}

	public void setComponentToMove(AuroraComponent child) {
		acToMove = child;
		srcContainer = child.getParent();
	}

	public void setReferenceComponent(AuroraComponent after) {
		acReference = after;
	}

	public void setTargetContainer(Container tgtContainer) {
		destContainer = tgtContainer;
	}

	@Override
	public boolean canExecute() {
		if (acToMove instanceof TabBody)
			return false;
		return super.canExecute();
	}

	@Override
	public boolean canUndo() {
		return super.canUndo();
	}

	@Override
	public String getLabel() {
		return "Move Component";
	}

	@Override
	public void execute() {
		List<AuroraComponent> srcList = srcContainer.getChildren();
		List<AuroraComponent> destList = destContainer.getChildren();
		oriIndex = srcList.indexOf(acToMove);
		srcContainer.removeChild(oriIndex);
		if (acReference == null) {
			destContainer.addChild(acToMove);
		} else {
			int idx = destList.indexOf(acReference);
			destContainer.addChild(acToMove, idx);
		}
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		destContainer.removeChild(acToMove);
		srcContainer.addChild(acToMove, oriIndex);
	}

}