package aurora.ide.meta.gef.editors.wizard.dialog;

public class LovDialogInput {

	private int col;
	private int row;

	public LovDialogInput(int col, int row) {
		this.setCol(col);
		this.setRow(row);
		input = new String[col][row];
	}

	private String[][] input = null;

	public void add(int col, int row, String value) {
		input[col][row] = value;
	}

	public String get(int col, int row) {
		return input[col][row];
	}

	public int getCol() {
		return col;
	}

	private void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	private void setRow(int row) {
		this.row = row;
	}

	// private Map<String, String> queryHead = new HashMap<String, String>();
	// private Map<Integer, List<String>> columns = new HashMap<Integer,
	// List<String>>();
	//
	// public void addQueryHead(String name, String value) {
	// queryHead.put(name, value);
	// }
	//
	// public void addColumn(int col, String data) {
	// List<String> list = columns.get(col);
	// if (list == null) {
	// columns.put(col, list = new ArrayList<String>());
	// }
	// list.add(data);
	// }
	//
	// public String getHead(String name) {
	// return queryHead.get(name);
	// }
	//
	// public String getHeadName(int i) {
	// if (i >= queryHead.size())
	// return null;
	// return queryHead.keySet().toArray(new String[queryHead.size()])[i];
	// }
	//
	// public int columns() {
	// return columns.size();
	// }
	//
	// public List<String> getColumn(int i) {
	// return columns.get(i);
	// }
}
