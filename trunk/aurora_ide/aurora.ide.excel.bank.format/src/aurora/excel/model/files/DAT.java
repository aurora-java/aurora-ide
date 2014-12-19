package aurora.excel.model.files;

import java.util.ArrayList;
import java.util.List;

public class DAT {

	private List<String> lines = new ArrayList<String>();

	public void addHead(String string) {
		getLines().add(string);
	}

	public List<String> getLines() {
		return lines;
	}

}
