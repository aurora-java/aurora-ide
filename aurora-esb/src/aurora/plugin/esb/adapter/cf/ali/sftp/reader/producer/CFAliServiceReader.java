package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;

public class CFAliServiceReader {

	private AuroraEsbContext esbContext;

	private List<String> readHistory = new ArrayList<String>();

	public CFAliServiceReader(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	// log read file;
	public void read(Exchange exchange) throws IOException {

		// camelfilenameconsumed:CFCar_AUTOFI_PAYEE_INFO_20150202_1.txt
		// camelfileparent:/Users/shiliyan/Desktop/esb/download/CFCar
		// camelfilelength:138
		// camelfilerelativepath:CFCar_AUTOFI_PAYEE_INFO_20150202_1.txt
		// camelfilenameonly:CFCar_AUTOFI_PAYEE_INFO_20150202_1.txt
		// camelfileabsolute:true
		// camelfilelastmodified:1432703619000
		// camelfilename:CFCar_AUTOFI_PAYEE_INFO_20150202_1.txt
		// breadcrumbid:ID-itpc118-corp-cf-finance-com-63988-1432703582588-0-41
		// camelfileabsolutepath:/Users/shiliyan/Desktop/esb/download/CFCar/CFCar_AUTOFI_PAYEE_INFO_20150202_1.txt
		// camelfilepath:/Users/shiliyan/Desktop/esb/download/CFCar/CFCar_AUTOFI_PAYEE_INFO_20150202_1.txt
		// version:1.0|count:1|isLast:NO
		// applyNo|name|address|phone
		// 94012014070100039589S|杭州XX经销商|浙江省杭州市西湖区学院路999号|057188888888

		Message in = exchange.getIn();
		Map<String, Object> headers = in.getHeaders();
		Set<String> keySet = headers.keySet();
		for (String key : keySet) {
			System.out.println(key + ":" + headers.get(key));
		}

		String filenameonly = (String) headers.get("camelfilenameonly");
		if (readHistory.contains(filenameonly))
			return;
		readHistory.add(filenameonly);

		ServiceFile sn = new ServiceFile(filenameonly);
		if (sn.isInvalid()) {
			return;
		}

		String body = in.getBody(String.class);
		List<String> lines = readBody(body);
		if (lines.size() < 2) {
			// hehe
			return;
		} else {
			// version:1.0|count:1|isLast:NO
			String header = lines.get(0);
			sn.setHeader(header);
			sn.setHeaders(readHeader(header));
			// applyNo|name|address|phone
			String cols = lines.get(1);
			sn.setCols(cols);
			// 94012014070100039589S|杭州XX经销商|浙江省杭州市西湖区学院路999号|057188888888
			for (int i = 2; i < lines.size(); i++) {
				sn.addData(lines.get(i));
			}
		}
		callProc(sn);

		// System.out.println(body);

	}

	private void callProc(ServiceFile sn) {
		// sn.getFileName()
		// message_recevie
		CompositeMap header = new CompositeMap("result");
		String h = sn.getHeader();
		String[] split = h.split("\\|");
		for (String hh : split) {
			String[] ss = hh.split(":");
			header.put(ss[0].trim(), ss[1].trim());
		}
		header.put("fileName", sn.getFileName());

		header.put("serviceName", sn.getService());

		header.put("orgCode", sn.getOrgCode());
		header.put("yyymmdd", sn.getYymmdd());
		header.put("batchNo", sn.getBatchNo().replace(".txt", ""));

		String cols = sn.getCols();

		String[] ccoolls = cols.split("\\|");

		//
		CompositeMap datasss = header.createChild("datas");
		List<String> datas = sn.getDatas();
		for (String data : datas) {
			CompositeMap dddd = datasss.createChild("data");
			String[] ddd = data.split("\\|");
			for (int i = 0; i < ddd.length; i++) {
				String col = ccoolls[i];
				String d = ddd[i] == null ? "" : ddd[i];
				dddd.put(col, d);
			}
			dddd.put("_status", "update");
//			_status="update"
		}

		try {
			esbContext.executeProc("message_recevie", header);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Map<String, String> readHeader(String header) {
		Map<String, String> headers = new HashMap<String, String>();
		String[] split = header.split("\\|");
		for (String h : split) {
			String[] ss = h.split(":");
			headers.put(ss[0].trim(), ss[1].trim());
		}

		return headers;
	}

	// public List<String> readCols(String cols) {
	// List<String> lines = new ArrayList<String>();
	// return lines;
	// }
	//
	// public List<String> readData(String data) {
	// List<String> lines = new ArrayList<String>();
	// return lines;
	// }

	public List<String> readBody(String body) {
		List<String> lines = new ArrayList<String>();

		StringReader reader = new StringReader(body);

		BufferedReader br = new BufferedReader(reader);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static void main(String[] args) {
		String s = "version:1.0|count:2|isLast:NO";
		String[] split = s.split("\\|");
		for (String string : split) {
			System.out.println(string);
		}
	}
}
