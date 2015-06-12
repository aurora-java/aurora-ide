package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import org.apache.camel.Exchange;

import aurora.plugin.source.gen.Path;

public class InvoiceFile {
	private Exchange exchange;

	private String invoice;
	private String yyyy;
	private String mm;
	private String dd;
	private String applyNo;
	private String fileName;

	private String fileLength;
	// CamelFileLength

	private boolean invalid = false;

	private String abPath;

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getYyyy() {
		return yyyy;
	}

	public void setYyyy(String yyyy) {
		this.yyyy = yyyy;
	}

	public String getMm() {
		return mm;
	}

	public void setMm(String mm) {
		this.mm = mm;
	}

	public String getDd() {
		return dd;
	}

	public void setDd(String dd) {
		this.dd = dd;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAbPath() {
		return abPath;
	}

	public void setAbPath(String abPath) {
		this.abPath = abPath;
	}

	public InvoiceFile(Exchange exchange) {
		this.exchange = exchange;
		parse();
	}

	public void parse() {

		// breadcrumbId:ID-shiliyan-corp-cf-finance-com-57291-1433820434049-0-7
		// CamelFileAbsolute:true
		// CamelFileAbsolutePath:/Users/shiliyan/Desktop/esb/download/CFCAR/invoice/2015/02/02/94012014070100039587S/IMG_1362.JPG
		// CamelFileContentType:null
		// CamelFileLastModified:1433466715000
		// CamelFileLength:2002273
		// CamelFileName:invoice/2015/02/02/94012014070100039587S/IMG_1362.JPG
		// CamelFileNameConsumed:invoice/2015/02/02/94012014070100039587S/IMG_1362.JPG
		// CamelFileNameOnly:IMG_1362.JPG
		// CamelFileParent:/Users/shiliyan/Desktop/esb/download/CFCAR/invoice/2015/02/02/94012014070100039587S
		// CamelFilePath:/Users/shiliyan/Desktop/esb/download/CFCAR/invoice/2015/02/02/94012014070100039587S/IMG_1362.JPG
		// CamelFileRelativePath:invoice/2015/02/02/94012014070100039587S/IMG_1362.JPG

		// yyyyMMdd/applyNo/
//		20150611/20150611300000000082510/2015060570/
		String relativePath = (String) exchange.getIn().getHeader(
				"CamelFileRelativePath");
		Path p = new Path(relativePath);

		String[] segments = p.segments();

		String temp = segments[segments.length - 2];
		applyNo = segments[segments.length - 3];
		fileName = segments[segments.length - 1];

		// String[] segments = segments;
		//
		// if (segments.length != 6) {
		// this.invalid = true;
		// return;
		// }
		// invoice = segments[0];
		// if ("invoice".equalsIgnoreCase(invoice) == false) {
		// this.invalid = true;
		// return;
		// }
		// yyyy = segments[1];
		// mm = segments[2];
		// dd = segments[3];
		// applyNo = segments[4];
		// fileName = segments[5];
		this.abPath = (String) exchange.getIn().getHeader(
				"CamelFileAbsolutePath");
		abPath = abPath.replace("file:", "");

		// this.abbackupPath = new Path(backupPath).append(relativePath)
		// .toString();
		this.fileLength = "" + exchange.getIn().getHeader("CamelFileLength");

	}

	public Exchange getExchange() {
		return exchange;
	}

	public void setExchange(Exchange exchange) {
		this.exchange = exchange;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public String getFileLength() {
		return fileLength;
	}

	public void setFileLength(String fileLength) {
		this.fileLength = fileLength;
	}

}
