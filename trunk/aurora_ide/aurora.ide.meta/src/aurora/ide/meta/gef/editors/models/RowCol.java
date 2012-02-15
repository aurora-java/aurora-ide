package aurora.ide.meta.gef.editors.models;

public class RowCol extends Container {
	protected int row = 3;
	protected int col = 3;
	protected int headHight = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8776030333465182289L;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		if (this.row == row) {
			return;
		}
		int old = this.row;
		this.row = row;
		firePropertyChange(ROW, old, row);
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		if (this.col == col) {
			return;
		}
		int old = this.col;
		this.col = col;
		firePropertyChange(COL, old, col);
	}


	public int getHeadHight() {
		return headHight;
	}
}
