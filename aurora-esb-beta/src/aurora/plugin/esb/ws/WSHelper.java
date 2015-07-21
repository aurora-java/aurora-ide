package aurora.plugin.esb.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.util.Base64;

import aurora.plugin.esb.model.From;
import aurora.plugin.esb.model.TO;
import aurora.plugin.esb.model.Task;
import aurora.plugin.esb.model.xml.XMLHelper;

public class WSHelper {
	public static Map<String, Object> createHeaderOptions(String userName, String psd) {

		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("soapaction", "urn:anonOutInOp");
		paras.put("Content-Type", "text/xml; charset=UTF-8");

		String encoded = new String(Base64.encode(new String(userName + ":"
				+ psd).getBytes()));
		paras.put("Authorization", "Basic " + encoded);
		return paras;
	}
	public static String loadPara(String workPath,Task task, From from) {
		try {
			String exchangeID = from.getExchangeID();
			String name = from.getName();

			return loadSavedParaData(workPath,task, exchangeID, name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String loadPara(String workPath,Task task, TO to) {

		try {
			String exchangeID = to.getExchangeID();
			String name = to.getName();
			return loadSavedParaData(workPath,task, exchangeID, name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	static public String loadSavedParaData(String workPath,Task task, String exchangeID, String name)
			throws FileNotFoundException, IOException {
		int lastIndexOf = exchangeID.lastIndexOf("-");
		String e = exchangeID.substring(lastIndexOf);
		String fileid = exchangeID.substring(0, lastIndexOf)
				+ (Integer.valueOf(e) - 1);

		File file = new File(workPath  + task.getName() + "/"
				+ name, fileid);
		// from.getExchangeID()
		FileInputStream fis = new FileInputStream(file);
		String inputStream2String = XMLHelper.inputStream2String(fis);
		return inputStream2String;
	}
}
