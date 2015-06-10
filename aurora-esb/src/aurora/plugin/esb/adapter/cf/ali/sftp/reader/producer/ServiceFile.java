package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceFile {

	private String fileName;
	private String orgCode;
	private String service;
	private String yymmdd;
	private String batchNo;
	private String header;
	private Map<String, String> headers = new HashMap<String, String>();
	private String cols;
	private List<String> datas = new ArrayList<String>();

	private boolean invalid;


	public ServiceFile(String fileName) {
		super();
		this.fileName = fileName;
		parse(fileName);
	}

	private void parse(String fileName) {
		invalid = false;

		if (fileName.endsWith(".txt")) {
			// 机构编码_AUTOFI_INVOICE_INFO _20150202_1.txt
			String[] split = fileName.replace(".txt", "").split("_");
			if (split.length < 4) {
				setInvalid(true);
				return;
			}
			orgCode = split[0];
			batchNo = split[split.length - 1];
			yymmdd = split[split.length - 2];
			service = "";
			for (int i = 1; i < split.length - 2; i++) {
				service = service + "_" + split[i];
			}
			service = service.replaceFirst("_", "");
		} else {

			setInvalid(true);
			return;
		}

	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getYymmdd() {
		return yymmdd;
	}

	public void setYymmdd(String yymmdd) {
		this.yymmdd = yymmdd;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public List<String> getDatas() {
		return datas;
	}

	public void addData(String data) {
		this.datas.add(data);
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

}
