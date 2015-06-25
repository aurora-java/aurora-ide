package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.DBLog;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.util.FileStore;

public class CFAliInvoiceReader {

	private AuroraEsbContext esbContext;

	private ConsoleLog clog = new ConsoleLog();

	private static final String fileName = "invoice_list";

	private String invoice_proc = "message_recevie";

	private CompositeMap readHistory;

	private FileStore fs;

	public CFAliInvoiceReader(AuroraEsbContext esbContext, String invoiceProc) {
		this.esbContext = esbContext;
		this.invoice_proc = invoiceProc;

		fs = new FileStore(esbContext.getWorkPath());
		readHistory = fs.load(fileName);

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
		// exchange.getIn().s
		Message in = exchange.getIn();
		Map<String, Object> headers = in.getHeaders();
		// Set<String> keySet = headers.keySet();
		// for (String key : keySet) {
		// System.out.println(key + ":" + headers.get(key));
		// }
		// exchange.getIn().setFault(true);C
		String filenameonly = (String) headers.get("camelfilenameonly");
		String camelfileabsolutepath = (String) headers
				.get("camelfileabsolutepath");

		InvoiceFile inf = new InvoiceFile(exchange);
		if (inf.isInvalid() == false) {
			sendInvoiceFile(inf);
			return;
		}

		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Reading File] " + filenameonly + " is invalid.");
		clog.log2Console("[Reading File] " + filenameonly + " is invalid.");
		return;
		// System.out.println(body);

	}

	private void sendInvoiceFile(InvoiceFile inf) {

		CompositeMap header = new CompositeMap("result");

		header.put("fileName".toLowerCase(), inf.getFileName());

		header.put("absPath".toLowerCase(), inf.getAbPath());

		header.put("applyNo".toLowerCase(), inf.getApplyNo());

		header.put("fileLength".toLowerCase(), inf.getFileLength());

		try {

			if (this.isRead(inf.getAbPath())) {

				esbContext.getmLogger().log(
						Level.SEVERE,
						"" + "[Reading File] " + inf.getFileName()
								+ " Do Not Need Read Again.");
				clog.log2Console("[Reading File] " + inf.getFileName()
						+ " Do Not Need Read Again.");
				return;
			}

			CompositeMap executeProc = esbContext.executeProc(
					this.invoice_proc, header);
			// return executeProc;
		} catch (Exception e) {
			String msg = e.getMessage();
			String mms = "ApplyNo: " + inf.getApplyNo() + " " + "File: "
					+ inf.getFileName() + " Loaded  Failed." + " errorMSG: "
					+ msg;
			log("[Reading File] " + mms);
			clog.log2Console("[Reading File] " + mms);
			e.printStackTrace();
			new DBLog(esbContext).log(mms);
			return;
		}

		addHistory(inf.getFileName(), inf.getAbPath());

		log("[Reading File] " + "ApplyNo: " + inf.getApplyNo() + " " + "File: "
				+ inf.getFileName() + " Loaded  Success.");
		clog.log2Console("[Reading File] " + "ApplyNo: " + inf.getApplyNo()
				+ " " + "File: " + inf.getFileName() + " Loaded  Success.");

	}

	private void log(String msg) {
		ILogger logger = esbContext.getmLogger();
		logger.log(Level.SEVERE, "" + msg);
	}

	private void addHistory(String filenameonly, String camelfileabsolutepath) {
		CompositeMap createChild = readHistory.createChild("file");
		createChild.put("file", filenameonly);
		createChild.put("abPath", camelfileabsolutepath);
		createChild.put("readDate", new Date());
		fs.save(readHistory, fileName);
	}

	private boolean isRead(String camelfileabsolutepath) {
		CompositeMap childByAttrib = readHistory.getChildByAttrib("file",
				"abPath", camelfileabsolutepath);
		return childByAttrib != null;
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

	public String getInvoice_proc() {
		return invoice_proc;
	}

	public void setInvoice_proc(String invoice_proc) {
		this.invoice_proc = invoice_proc;
	}
}
