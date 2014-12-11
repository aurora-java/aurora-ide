package aurora.excel.model.files;

import java.util.ArrayList;
import java.util.List;

public class XLSLine {

	private List<String> datas = new ArrayList<String>();

	public List<String> getDatas() {
		return datas;
	}

	public void addData(String data) {
		this.datas.add(data);
	}
}
