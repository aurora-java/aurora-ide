package aurora.excel.model.files;

import java.util.ArrayList;
import java.util.List;

public class IDX {

	private List<String> lines = new ArrayList<String>();

	public void addHead(String line) {
		getLines().add(line);
	}

	public List<String> getLines() {
		return lines;
	}

}
