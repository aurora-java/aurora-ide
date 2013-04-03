package aurora.plugin.source.gen.screen.model;

import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

abstract public class RowCol extends Container {
	// protected int r = 3;
	// protected int c = 3;
	protected int headHight = 0;

	public int getRow() {
		// return row;
		Object r = this.getPropertyValue(ComponentProperties.row);
		if (r instanceof Integer) {
			return (Integer) r;
		}
		return 3;
	}

	public void setRow(int r) {
		this.setPropertyValue(ComponentProperties.row, r);
		// if (this.row == row) {
		// return;
		// }
		// int old = this.row;
		// this.row = row;
		// firePropertyChange(ROW, old, row);
	}

	public int getCol() {
		Object r = this.getPropertyValue(ComponentProperties.column);
		if (r instanceof Integer) {
			return (Integer) r;
		}
		return 3;
	}

	public void setCol(int col) {
		this.setPropertyValue(ComponentProperties.column, col);
		// if (this.col == col) {
		// return;
		// }
		// int old = this.col;
		// this.col = col;
		// firePropertyChange(COL, old, col);
	}

	public int getHeadHight() {
		return headHight;
	}
}
