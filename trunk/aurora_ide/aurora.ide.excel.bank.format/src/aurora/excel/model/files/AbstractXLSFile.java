package aurora.excel.model.files;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import aurora.excel.model.format.ExcelReader;

public class AbstractXLSFile {

	private String filePath;
	private InputStream is;

	public List<XLSLine> readXLS(int start) {
		try {
			ExcelReader reader = new ExcelReader();
			reader.setStart(start);
			InputStream is2 = is != null ? is : new FileInputStream(filePath);
			List<XLSLine> map = reader.readExcelContent(is2);
			return map;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}
}
