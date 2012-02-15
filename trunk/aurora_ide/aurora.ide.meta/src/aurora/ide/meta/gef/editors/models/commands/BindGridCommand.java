package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.Grid;

public class BindGridCommand extends DropBMCommand {
	private Grid grid;

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public void execute() {
		this.fillGrid(grid);
	}
}
