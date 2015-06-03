package aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer;

import java.util.logging.Level;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;
import aurora.plugin.esb.AuroraEsbContext;

public class ApprovalContractFile {

	private int batchNo = 1;
	private AuroraEsbContext esbContext;

	public ApprovalContractFile(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	private CompositeMap callProc() {
		// sn.getFileName()
		// message_recevie

		CompositeMap header = new CompositeMap("result");

		header.put("fileName".toLowerCase(), "filename");

		header.put("serviceName".toLowerCase(), "servicename");

		header.put("orgCode".toLowerCase(), "orgnumger");
		// header.put("yyymmdd".toLowerCase(), sn.getYymmdd());
		// header.put("batchNo".toLowerCase(), sn.getBatchNo().replace(".txt",
		// ""));

		try {
			CompositeMap executeProc = esbContext.executeProc("gen_file_ap",
					header);
			return executeProc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new CompositeMap();

	}

	public void genFile(Exchange exchange) {

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
		// isLast

		batchNo++;
		callProc();
		Message out = exchange.getOut();
		String fileName = "CFCar_AUTOFI_APPROVAL_CONTRACT_20150202_" + batchNo
				+ ".txt";
		out.setHeader("camelfilename",
				"/CFCar/AUTOFI_APPROVAL_CONTRACT/20150601/" + fileName);

		// gen_file.proc
		// out.setHeader("camelfileabsolute",
		// "false");
		// out.setHeader(
		// "camelfilenameconsumed",
		// "/CFCar/AUTOFI_APPROVAL_CONTRACT/20150601/CFCar_AUTOFI_APPROVAL_CONTRACT_20150202_2.txt");
		// out.setHeader("camelfilepath",
		// "/AUTOFI_APPROVAL_CONTRACT/20150601/");
		// out.setHeader(
		// <parameter header="version:1.0|count:3|isLast:NO"
		// att="napplyNo|allowLoan|failReason|reasonType|applyAmount|contractNo"
		// value1="94012014070100039587S|YES|||1000|AAAAAAA"
		// value2="94012014070100039587S|YES|||1000|AAAAAAA" count="2"/>
		// "camelfileabsolutepath",
		// "/Users/shiliyan/Desktop/esb/upload/CFCar/AUTOFI_APPROVAL_CONTRACT/20150601/CFCar_AUTOFI_APPROVAL_CONTRACT_20150202_2.txt");
		out.setBody("version:1.0|count:3|isLast:NO\napplyNo|allowLoan|failReason|reasonType|applyAmount|contractNo\n94012014070100039587S|YES|||1000|AAAAAAA");

		// out.setBody(null);
		System.out.println("???");

		log("file " + fileName + " generated. ");

		// return exchange;
	}

	private void log(String msg) {
		ILogger logger = esbContext.getmLogger();
		logger.log(Level.SEVERE, "" + msg);
	}
}
