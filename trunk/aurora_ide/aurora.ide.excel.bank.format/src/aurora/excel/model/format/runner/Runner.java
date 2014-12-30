package aurora.excel.model.format.runner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import aurora.excel.model.files.DAT;
import aurora.excel.model.files.IDX;
import aurora.excel.model.files.TXT;
import aurora.excel.model.files.XLSFile;

public class Runner {

	private List<XLSFileSetting> xlsList = new ArrayList<XLSFileSetting>();

	private String savePath;

	private OutputFileSetting outputFileSetting;

	public Runner(OutputFileSetting outputFileSetting,
			List<XLSFileSetting> xlsList, String savePath) {
		super();
		this.xlsList = xlsList;
		this.savePath = savePath;
		this.outputFileSetting = outputFileSetting;
	}

	public boolean run() {

		StringBuilder idxSB = new StringBuilder();
		StringBuilder datSB = new StringBuilder();
		int key_word_start = 1;
		for (XLSFileSetting setting : xlsList) {
			XLSFile xlsFile = new XLSFile(setting, key_word_start);
			key_word_start = xlsFile.makeKeyWordCode();
			IDX makeIDXFile = xlsFile.makeIDXFile();
			DAT makeDATFile = xlsFile.makeDATFile();
			List<String> lines = makeIDXFile.getLines();
			for (String string : lines) {
				idxSB.append(string);
				idxSB.append("\r\n"); //$NON-NLS-1$
			}
			List<String> lines2 = makeDATFile.getLines();
			for (String string : lines2) {
				datSB.append(string);
				datSB.append("\r\n"); //$NON-NLS-1$
			}
		}

		try {
			writeFile(fileName("BI") + ".IDX", idxSB.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			writeFile(fileName("BJ") + ".DAT", datSB.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			writeFile(fileName("AD") + ".TXT", TXT.content());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private String fileName(String d) {
		OutputFileSetting o = outputFileSetting;
		String s =
		// o.getCode() +
		d + o.getOrg_code() + o.getArea_code() + o.getDate() + o.getRate()
				+ o.getBatch() + o.getOrder();
		return s;
	}

	public void writeFile(String fileName, String content) throws IOException {
		FileWriter fw = new FileWriter(savePath + "/" + fileName); //$NON-NLS-1$
		fw.write(content, 0, content.length());
		fw.flush();
	}
}
