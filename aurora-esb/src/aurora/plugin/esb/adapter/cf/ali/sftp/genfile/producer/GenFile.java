package aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.DBLog;
import aurora.plugin.esb.console.ConsoleLog;

public class GenFile {

	private AuroraEsbContext esbContext;
	private String serviceName;
	private String procName;
	private String orgCode;

	public GenFile(AuroraEsbContext esbContext, String serviceName,
			String procName, String orgCode) {
		this.esbContext = esbContext;
		this.serviceName = serviceName;
		this.procName = procName;
		this.orgCode = orgCode;
	}

	private CompositeMap callProc() {
		// sn.getFileName()
		// message_recevie

		CompositeMap header = new CompositeMap("result");

		// header.put("fileName".toLowerCase(), "filename");
		// "AUTOFI_SEND_BILL"
		header.put("serviceName".toLowerCase(), serviceName);

		header.put("orgCode".toLowerCase(), orgCode);

		Date date = new Date();
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		header.put("yyyymmdd".toLowerCase(), format1.format(date));
		// header.put("batchNo".toLowerCase(), sn.getBatchNo().replace(".txt",
		// ""));

		// islast=yes
		// only once

		try {
			// "gen_file_bill"
			CompositeMap executeProc = esbContext.executeProc(procName, header);
			return executeProc;
		} catch (Exception e) {
			e.printStackTrace();

			String msg = e.getMessage();
			String mms = "" + serviceName + " Gen File  Failed."
					+ " errorMSG: " + msg;
			log("[Reading File] " + mms);
			new ConsoleLog().log2Console("[Reading File] " + mms);
			e.printStackTrace();
			new DBLog(esbContext).log(mms);

		}
		return new CompositeMap();

	}

	public void genFile(Exchange exchange) {

		CompositeMap callProc = callProc();

		// callProc.toXML();
		// System.out.println(callProc.toXML());

		esbContext.getmLogger().log(
				Level.SEVERE,
				"" + "[Gen File] " + "[" + serviceName + "] "
						+ "Genfile Loaded From DB.");
		new ConsoleLog().log2Console("[Gen File] " + "[" + serviceName + "] "
				+ "Genfile Loaded From DB.");
		if (callProc == null) {

			esbContext.getmLogger().log(
					Level.SEVERE,
					"" + "[Gen File] " + "[" + serviceName + "] "
							+ "GenFile No Data Found From DB.");
			new ConsoleLog().log2Console("[Gen File] " + "[" + serviceName
					+ "] " + "GenFile No Data Found From DB.");
			return;

		}

		CompositeMap parameter = callProc.getChild("parameter");
		if (parameter == null) {
			esbContext.getmLogger().log(
					Level.SEVERE,
					"" + "[Gen File] " + "[" + serviceName + "] "
							+ "GenFile No Data Found From DB.");
			new ConsoleLog().log2Console("[Gen File] " + "[" + serviceName
					+ "] " + "GenFile No Data Found From DB.");
			return;
		}
		int count = parameter.getInt("count", 0);
		CompositeMap header = parameter.getChild("header");
		if (count <= 0 && header != null) {
			String isLast = header.getString("isLast", "NO");
			if ("Y".equalsIgnoreCase(isLast) || "YES".equalsIgnoreCase(isLast)) {

				String orgCode = header.getString("orgCode", "CFCAR");
				// "AUTOFI_SEND_BILL"
				String serviceName = header.getString("serviceName",
						this.serviceName);
				DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				String yyyymmdd = header.getString("yyymmdd",
						format1.format(date));
				String batchNo = header.getString("batchNo", "1");
				String version = header.getString("version", "1.0");
				String counts = header.getString("count", "0");

				String fileName = orgCode + "_" + serviceName + "_" + yyyymmdd
						+ "_" + batchNo + ".txt";

				String camelfilename = orgCode + "/" + serviceName + "/"
						+ yyyymmdd + "/" + fileName;

				Message out = exchange.getOut();
				out.setHeader("camelfilename", camelfilename);
				String headers = "version:" + version + "|count:" + counts
						+ "|isLast:" + isLast;

				String datas = makeDatas(parameter);
				String colsText = header.getString("cols_text", "");
				if ("".equals(datas))
					out.setBody(headers + "\n" + colsText);
				else
					out.setBody(headers + "\n" + datas);
				// out.setBody(headers);
				log("[Gen File] " + "File " + fileName + " Generated. ");
				new ConsoleLog().log2Console("[Gen File] " + "File " + fileName
						+ " Generated. ");

				return;
			}
		}
		if (count <= 0) {
			Message out = exchange.getOut();
			out.setFault(true);
			out.getBody();
			out.getHeaders();
			esbContext.getmLogger().log(
					Level.SEVERE,
					"" + "[Gen File] " + "[" + serviceName + "] "
							+ "GenFile No Data Found From DB.");
			new ConsoleLog().log2Console("[Gen File] " + "[" + serviceName
					+ "] " + "GenFile No Data Found From DB.");
			return;
		}
		if (header != null) {
			String orgCode = header.getString("orgCode", "CFCAR");
			// "AUTOFI_SEND_BILL"
			String serviceName = header.getString("serviceName",
					this.serviceName);
			DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date();
			String yyyymmdd = header.getString("yyymmdd", format1.format(date));
			String batchNo = header.getString("batchNo", "1");
			String isLast = header.getString("isLast", "NO");
			String version = header.getString("version", "1.0");
			String counts = header.getString("count", "0");

			String fileName = orgCode + "_" + serviceName + "_" + yyyymmdd
					+ "_" + batchNo + ".txt";

			String camelfilename = orgCode + "/" + serviceName + "/" + yyyymmdd
					+ "/" + fileName;

			Message out = exchange.getOut();
			out.setHeader("camelfilename", camelfilename);
			String headers = "version:" + version + "|count:" + counts
					+ "|isLast:" + isLast;
			String datas = makeDatas(parameter);
			out.setBody(headers + "\n" + datas);

			log("[Gen File] " + "File " + fileName + " Generated. ");
			new ConsoleLog().log2Console("[Gen File] " + "File " + fileName
					+ " Generated. ");
		}

		// return exchange;
	}

	private String makeDatas(CompositeMap parameter) {

		CompositeMap datas = parameter.getChild("datas");
		if (datas == null)
			return "";
		Set keySet = getKeySet(parameter);
		if (keySet == null)
			return "";

		StringBuilder result = new StringBuilder();
		for (Object key : keySet) {
			result.append("|");
			result.append(key);
		}
		if (keySet.size() > 0)
			result.deleteCharAt(0);

		List childsNotNull = datas.getChildsNotNull();

		StringBuilder body = new StringBuilder();
		for (Object object : childsNotNull) {
			CompositeMap data = (CompositeMap) object;
			StringBuilder line = new StringBuilder();
			for (Object key : keySet) {
				line.append("|");
				String s = data.getString(key, "");
				line.append(s);
			}
			if (line.length() > 0)
				line.deleteCharAt(0);
			body.append("\n");
			body.append(line);
		}
		result.append(body);

		return result.toString();
	}

	private Set getKeySet(CompositeMap parameter) {
		CompositeMap child = parameter.getChild("datas").getChild("data");
		if (child == null)
			return null;
		return child.keySet();
	}

	private void log(String msg) {
		ILogger logger = esbContext.getmLogger();
		logger.log(Level.SEVERE, "" + msg);
	}
}
