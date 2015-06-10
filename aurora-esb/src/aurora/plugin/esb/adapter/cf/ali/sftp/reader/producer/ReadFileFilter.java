package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;

public class ReadFileFilter implements GenericFileFilter {
	private AuroraEsbContext esbContext;
	private CompositeMap producerMap;

	public ReadFileFilter(AuroraEsbContext esbContext, CompositeMap producerMap) {
		this.esbContext = esbContext;
		this.producerMap = producerMap;
	}

	private boolean isAfter(String date, String when) {

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

	public boolean accept(GenericFile file) {
		// we want all directories
		if (file.isDirectory()) {
			return true;
		}

		String fileNameOnly = file.getFileNameOnly();
		ServiceFile sn = new ServiceFile(fileNameOnly);
		if (sn.isInvalid() == false) {
			String batchNo = sn.getBatchNo();
			String service = sn.getService();
			String yymmdd = sn.getYymmdd();
			// return new File().exists()

//			String bn = lastRead(sn);
//			existList(sn);
//			willReadList(sn);
			return willRead(sn,file) == false;

			// return Integer.parseInt(batchNo) - Integer.parseInt(pn) >
			// 0;

		} else {
			return true;
		}

		// Date now = new Date();
		// DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		// String nows = format1.format(now);
		// String startdates = producerMap.getChild("local").getString(
		// "startDate".toLowerCase(), nows);
		// if (isAfter(nows, startdates)) {
		// String fileNameOnly = file.getFileNameOnly();
		// ServiceFile sn = new ServiceFile(fileNameOnly);
		// if (sn.isInvalid() == false) {
		// String batchNo = sn.getBatchNo();
		// String service = sn.getService();
		// String yymmdd = sn.getYymmdd();
		// if (isAfter(startdates, yymmdd)) {
		// // return new File().exists()
		// return isExists(file) == false;
		//
		// // return Integer.parseInt(batchNo) - Integer.parseInt(pn) >
		// // 0;
		// }
		// } else {
		// return isExists(file) == false;
		// }
		// }

		// we dont accept any files starting with skip in the name
		// System.out.println("fffffffffffffffff");
		// return fileNameOnly.contains("20150609");
		// return false;
		// return !file.getFileName().startsWith("skip");
	}

	private boolean willRead(ServiceFile sn, GenericFile file) {
		String[] existList = existList(sn, file);

		int from = lastRead(sn, file) + 1;
		int to = from;
		for (int i = from;; i++) {
			if (isWillRead(i, existList)) {
				to = i;
				break;
			}
		}
		String batchNo = sn.getBatchNo();

		Integer r = Integer.valueOf(batchNo);
		if (r >= from && r <= to)
			return true;
		return false;
	}

	private boolean isWillRead(int n, String[] existList) {
		for (String ll : existList) {
			Integer fbn = Integer.valueOf(new ServiceFile(ll).getBatchNo());
			if (n == fbn) {
				return true;
			}
		}
		return false;
	}

	private String[] existList(ServiceFile sn, GenericFile file) {
		sn.getService();
		sn.getFileName();
		sn.getBatchNo();
		sn.getYymmdd();

		CompositeMap config = producerMap.getChild("local");
		String readingPath = config.getString("readingPath".toLowerCase(), "");
		String backupPath = config.getString("backupPath".toLowerCase(), "");
		String orgCode = config.getString("orgCode".toLowerCase(), "");

		File sf = new File(readingPath.replace("file:", ""), orgCode);
		File bf = new File(backupPath.replace("file:", ""), orgCode);

		File sff = new File(sf, file.getRelativeFilePath());
		File bff = new File(bf, file.getRelativeFilePath());

		String[] list = sff.getParentFile().list();

		return list;

		// reading_url = readingPath + "/" + orgCode ;
		// backup_url = backupPath + "/" + orgCode
		//
		// CompositeMap config = producerMap.getChild("local");
		//
		// orgCode = config.getString("orgCode".toLowerCase(), "");
		// readingPath = config.getString("readingPath".toLowerCase(), "");
		// readingPara = config.getChild("readingPara") == null ? "" : config
		// .getChild("readingPara").getText();
		// // readingPara = config.getString("readingPara", "");
		// backupPath = config.getString("backupPath".toLowerCase(), "");
		//
		// backupPara = config.getChild("backupPara") == null ? "" : config
		// .getChild("backupPara").getText();
		// // backupPara = config.getString("backupPara", "");
		// String readProc = config.getString("readProc".toLowerCase(), "");
		//
		// String invoiceProc = config.getString("invoiceProc".toLowerCase(),
		// "");
		//
		//
		// reading_url = readingPath + "/" + orgCode + readingPara.trim();
		// backup_url = backupPath + "/" + orgCode + backupPara.trim();

	}

	private int lastRead(ServiceFile sn, GenericFile file) {
		sn.getService();
		sn.getFileName();
		sn.getBatchNo();
		sn.getYymmdd();

		CompositeMap config = producerMap.getChild("local");
		String readingPath = config.getString("readingPath".toLowerCase(), "");
		String backupPath = config.getString("backupPath".toLowerCase(), "");
		String orgCode = config.getString("orgCode".toLowerCase(), "");

		File sf = new File(readingPath.replace("file:", ""), orgCode);
		File bf = new File(backupPath.replace("file:", ""), orgCode);

		File sff = new File(sf, file.getRelativeFilePath());
		File bff = new File(bf, file.getRelativeFilePath());

		String[] list = bff.getParentFile().list();
		int n = 0;
		for (String l : list) {
			Integer fbn = Integer.valueOf(new ServiceFile(l).getBatchNo());
			n = Math.max(n, fbn);
		}
		return n;
	}

	//
	// ServiceFile sn = new ServiceFile(header.toString());
	// if(sn.isInvalid()==false){
	// CompositeMap properties = esbContext.getProperties();
	// String service = sn.getService();
	// String batchNo = sn.getBatchNo();
	// String yymmdd = sn.getYymmdd();
	// CompositeMap serviceNode = properties.getChild(service);
	// if(serviceNode==null)
	// serviceNode = properties.createChild(service);
	// serviceNode.put("batchno", batchNo);
	// serviceNode.put("yymmdd", yymmdd);
	// esbContext.saveProperties();
	// }

	private boolean isExists(GenericFile file) {

		// producerMap

		CompositeMap config = producerMap.getChild("local");
		String local_save_path = config.getString(
				"localSavePath".toLowerCase(), "");
		String orgCode = config.getString("orgCode".toLowerCase(), "");

		String backupPath = config.getString("backupPath".toLowerCase(), "");

		File sf = new File(local_save_path.replace("file:", ""), orgCode);
		File bf = new File(backupPath.replace("file:", ""), orgCode);

		File sff = new File(sf, file.getRelativeFilePath());
		File bff = new File(bf, file.getRelativeFilePath());
		// File sff = new
		// Path(local_save_path).append(orgCode).append(file.getRelativeFilePath()).makeAbsolute().toFile();
		// File bff = new
		// Path(backupPath).append(orgCode).append(file.getRelativeFilePath()).removeFirstSegments(1).toFile();
		// new
		// File("File:/Users/shiliyan/Desktop/esb/download/CFCAR/AUTOFI_CREATE_CONTRACT/20150609/CFCAR_AUTOFI_CREATE_CONTRACT_20150609_32.txt").exists();
		return sff.exists() || bff.exists();

	}

}
