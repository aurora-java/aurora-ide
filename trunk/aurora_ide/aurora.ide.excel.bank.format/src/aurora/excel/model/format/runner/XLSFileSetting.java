package aurora.excel.model.format.runner;

import uncertain.composite.CompositeMap;

public class XLSFileSetting {
	private String filePath;
	private CompositeMap xls_setting;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public CompositeMap getXls_setting() {
		return xls_setting;
	}

	public void setXls_setting(CompositeMap xls_setting) {
		this.xls_setting = xls_setting;
	}
}
