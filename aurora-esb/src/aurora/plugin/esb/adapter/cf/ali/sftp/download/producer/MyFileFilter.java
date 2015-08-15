package aurora.plugin.esb.adapter.cf.ali.sftp.download.producer;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer.ServiceFile;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.source.gen.Path;

public class MyFileFilter<T> implements GenericFileFilter<T> {
	private AuroraEsbContext esbContext;
	private CompositeMap producerMap;

	private String startdates;

	public MyFileFilter(AuroraEsbContext esbContext, CompositeMap producerMap) {
		this.esbContext = esbContext;
		this.producerMap = producerMap;
		// Date now = new Date();
		// DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		// String nows = format1.format(now);
		// startdates = producerMap.getChild("sftp").getString(
		// "startDate".toLowerCase(), nows);
		this.resetStartDate();
	}

	static private boolean isAfter(String date, String when) {

		if (date.equals(when))
			return true;

		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			Date d1;
			d1 = format.parse(date);
			Date d2 = format.parse(when);
			return d2.after(d1);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean accept(GenericFile<T> file) {
		boolean acc = acc(file);
		System.out.println(new Date() + "  [MyFileFilter Folder&File] "
				+ file.getFileNameOnly() + "   " + acc);
		// new ConsoleLog().log2Console();
		return acc;
	}

	public boolean acc(GenericFile<T> file) {
		// we want all directories
		Date now = new Date();
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		String nows = format1.format(now);
		// String startdates = producerMap.getChild("sftp").getString(
		// "startDate".toLowerCase(), nows);
		if (file.isDirectory()) {
			String d = file.getFileNameOnly();
			// new ConsoleLog().log2Console("[MyFileFilter Folder] "
			// + file.getFileNameOnly());
			if (d.startsWith("20") && d.length() == 8) {
				try {
					int parseInt = Integer.parseInt(d);
					if (isAfter(startdates, d) == false) {
						return false;
					}

				} catch (NumberFormatException e) {
				}
			}
			new ConsoleLog().log2Console("[MyFileFilter Folder] "
					+ file.getAbsoluteFilePath() + " true" + "startdates : "
					+ startdates);
			return true;
		}
		String fileNameOnly = file.getFileNameOnly();
		if (fileNameOnly.endsWith(".tmp"))
			return false;
		// new ConsoleLog()
		// .log2Console("[MyFileFilter] " + file.getFileNameOnly());
		if (isAfter(startdates, nows)) {
			// String fileNameOnly = file.getFileNameOnly();
			ServiceFile sn = new ServiceFile(fileNameOnly);
			if (sn.isInvalid() == false) {
				String batchNo = sn.getBatchNo();
				String service = sn.getService();
				String yymmdd = sn.getYymmdd();
				if (isAfter(startdates, yymmdd)) {
					// return new File().exists()
					return isExists(file) == false;
				}
			} else {
				return isExists(file) == false;
			}
		}

		// we dont accept any files starting with skip in the name
		return false;
	}

	private boolean isExists(GenericFile<T> file) {

		// producerMap

		CompositeMap config = producerMap.getChild("local");
		String local_save_path = config.getString(
				"localSavePath".toLowerCase(), "");
		String orgCode = config.getString("orgCode".toLowerCase(), "");

		String backupPath = config.getString("backupPath".toLowerCase(), "");

		String errorPath = config.getString("errorPath".toLowerCase(), "");

		File sf = new File(local_save_path.replace("file:", ""), orgCode);
		File bf = new File(backupPath.replace("file:", ""), orgCode);
		File errf = new File(errorPath.replace("file:", ""));

		File sff = new File(sf, file.getRelativeFilePath());
		File bff = new File(bf, file.getRelativeFilePath());
		File errff = new File(errorPath.replace("file:", ""),
				file.getFileNameOnly());
		return sff.exists() || bff.exists() || errff.exists();

	}

	private String defaultStartDate() {
		CompositeMap config = producerMap.getChild("local");
		String local_save_path = config.getString(
				"localSavePath".toLowerCase(), "");
		String orgCode = config.getString("orgCode".toLowerCase(), "");

		String local_url = local_save_path + "/" + orgCode;
		File CFCAR = new File(local_url.replace("file:", ""));
		File AUTOFI_CREATE_CONTRACT = new File(CFCAR, "AUTOFI_CREATE_CONTRACT");
		String[] list = AUTOFI_CREATE_CONTRACT.list();
		int max = 0;
		if (list == null) {
			return "" + max;
		}
		for (String d : list) {
			if (d.startsWith("20") && d.length() == 8) {
				try {
					int parseInt = Integer.parseInt(d);
					max = Math.max(max, parseInt);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return "" + max;
		// AUTOFI_CANCLE_CONTRACT
		// AUTOFI_CREATE_CONTRACT
		// AUTOFI_INVOICE
		// AUTOFI_INVOICE_INFO
		// AUTOFI_LOAN_RESULT
		// AUTOFI_PAYEE_INFO
		// AUTOFI_PAYMENT_RESULT
	}

	public static void main(String[] args) {

		String defaultStartDate = "20150812";
		String _startdates = "20150813";
		String r = "0";

		if (isAfter(_startdates, defaultStartDate)) {
			r = defaultStartDate;
		} else {
			r = _startdates;
		}
		System.out.println(r);
	}

	public void resetStartDate() {
		String defaultStartDate = this.defaultStartDate();
		Date now = new Date();
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		String nows = format1.format(now);
		String _startdates = producerMap.getChild("sftp").getString(
				"startDate".toLowerCase(), nows);
		if ("0".equals(defaultStartDate)) {
			startdates = _startdates;
		} else {
			if (isAfter(_startdates, defaultStartDate)) {
				startdates = defaultStartDate;
			} else {
				startdates = _startdates;
			}
		}
		new ConsoleLog().log2Console("[MyFileFilter Folder] resetStartDate "
				+ startdates);
	}

	public String getStartdates() {
		return startdates;
	}

	public void setStartdates(String startdates) {
		this.startdates = startdates;
	}
}
