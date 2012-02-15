package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.BOX;

public class BindFormCommand extends DropBMCommand {
	private BOX box;



	public BOX getBox() {
		return box;
	}



	public void setBox(BOX box) {
		this.box = box;
	}



	public void execute() {
		this.fillForm(box);
	}
}
