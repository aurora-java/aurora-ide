package aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

public class SendBillFile {

	private int batchNo = 1;

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
		batchNo++;
		Message out = exchange.getOut();
		out.setHeader("camelfilename",
				"/CFCar/AUTOFI_SEND_BILL/20150601/CFCar_AUTOFI_SEND_BILL_20150202_"
						+ batchNo + ".txt");
		// out.setHeader("camelfileabsolute",
		// "false");
		// out.setHeader(
		// "camelfilenameconsumed",
		// "/CFCar/AUTOFI_APPROVAL_CONTRACT/20150601/CFCar_AUTOFI_APPROVAL_CONTRACT_20150202_2.txt");
		// out.setHeader("camelfilepath",
		// "/AUTOFI_APPROVAL_CONTRACT/20150601/");
		// out.setHeader(
		// "camelfileabsolutepath",
		// "/Users/shiliyan/Desktop/esb/upload/CFCar/AUTOFI_APPROVAL_CONTRACT/20150601/CFCar_AUTOFI_APPROVAL_CONTRACT_20150202_2.txt");
		out.setBody("version:1.0|count:2|isLast:YES\n"
				+ "applyNo|orgCode|drawndnNo|loanLength|loanCustomer|prinAmt|intAmt|penaltyAmt|disAmt|paidPrinAmt|paidIntAmt|paidPenaltyAmt|clearDate|status|termNo|beginDate|endDate|dueDat|paymentDueDate|billPrinAmt|billIntAmt|billPenaltyAmt|billPaidPrinAmt|billPaidIntAmt|billPaidPenaltyAmt|billClearDate|billStatus|overdueDays"
				+ "\n"
				+ "94012014070100039589S|001|1234567|12|张三|1200000|150000|0|30000|0|0|0||正常|01|2015-01-05|2015-02-04|2015-02-15|2015-02-20|100000|10000|0|0|0|0||正常|0"
				+ "\n"
				+ "94012014070100039589S|001|1234567|12|张三|1200000|150000|0|30000|0|0|0||正常|12|2015-12-05|2016-01-04|2016-01-15|2016-01-20|100000|10000|0|0|0|0||正常|0");

		// out.setBody(null);
		System.out.println("???");

		// return exchange;
	}

}
