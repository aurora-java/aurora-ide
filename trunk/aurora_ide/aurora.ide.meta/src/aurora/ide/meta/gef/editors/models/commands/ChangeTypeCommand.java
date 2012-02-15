package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.actions.TypeChangeAction;

public class ChangeTypeCommand extends Command {
	private TypeChangeAction action;

	public ChangeTypeCommand(TypeChangeAction action) {
		this.action = action;
	}

	@Override
	public void execute() {
		action.apply();
	}

	@Override
	public String getLabel() {
		return "Change Type";
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
		action.unApply();
	}

}