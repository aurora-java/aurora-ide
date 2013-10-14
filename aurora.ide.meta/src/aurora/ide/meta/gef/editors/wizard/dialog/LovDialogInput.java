package aurora.ide.meta.gef.editors.wizard.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LovDialogInput {
	private List<String> queryHead = new ArrayList<String>();
	private Map<Integer, List<String>> columns = new HashMap<Integer, List<String>>();

	public void addQueryHead(String head) {
		queryHead.add(head);
	}

	public void addColumn(int col, String data) {
		List<String> list = columns.get(col);
		if (list == null) {
			columns.put(col, list = new ArrayList<String>());
		}
		list.add(data);
	}

	public String getHead(int i) {
		if (i >= queryHead.size())
			return null;
		return queryHead.get(i);
	}

	public int columns() {
		return columns.size();
	}

	public List<String> getColumn(int i) {
		return columns.get(i);
	}
}
