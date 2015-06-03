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

public class GenFile {

	private AuroraEsbContext esbContext;
	private String serviceName;
	private String procName;

	public GenFile(AuroraEsbContext esbContext, String serviceName,String procName) {
		this.esbContext = esbContext;
		this.serviceName = serviceName;
		this.procName = procName;
	}

	private CompositeMap callProc() {
		// sn.getFileName()
		// message_recevie

		CompositeMap header = new CompositeMap("result");

		// header.put("fileName".toLowerCase(), "filename");
//		"AUTOFI_SEND_BILL"
		header.put("serviceName".toLowerCase(), serviceName);

		header.put("orgCode".toLowerCase(), "CFCar");

		Date date = new Date();
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		header.put("yyyymmdd".toLowerCase(), format1.format(date));
		// header.put("batchNo".toLowerCase(), sn.getBatchNo().replace(".txt",
		// ""));

		// islast=yes
		// only once

		try {
//			"gen_file_bill"
			CompositeMap executeProc = esbContext.executeProc(procName,
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

		CompositeMap callProc = callProc();

		System.out.println(callProc.toXML());

		CompositeMap parameter = callProc.getChild("parameter");
		int count = parameter.getInt("count", 0);
		if (count <= 0) {
			Message out = exchange.getOut();
			out.setBody(null);
			// out.setFault(true);
			return;
		}
		CompositeMap header = parameter.getChild("header");
		String orgCode = header.getString("orgCode", "CFCar");
//		"AUTOFI_SEND_BILL"
		String serviceName = header
				.getString("serviceName", this.serviceName);
		DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String yyyymmdd = header.getString("yyymmdd", format1.format(date));
		String batchNo = header.getString("batchNo", "1");
		String isLast = header.getString("isLast", "NO");
		String version = header.getString("version", "1.0");
		String counts = header.getString("count", "0");

		String fileName = orgCode + "_" + serviceName + "_" + yyyymmdd + "_"
				+ batchNo + ".txt";

		String camelfilename = orgCode + "/" + serviceName + "/" + yyyymmdd
				+ "/" + fileName;

		// String fileName = "CFCar_AUTOFI_APPROVAL_CONTRACT_20150202_" +
		// batchNo
		// + ".txt";
		Message out = exchange.getOut();
		out.setHeader("camelfilename", camelfilename);
		String headers = "version:" + version + "|count:" + counts + "|isLast:"
				+ isLast;
		String datas = makeDatas(parameter);
		out.setBody(headers + "\n" + datas);
		// <parameter count="12.0">
		// <result orgcode="CFCar" yyyymmdd="20150602"
		// servicename="AUTOFI_SEND_BILL"/>
		// <header batchNo="1" isLast="YES" count="12" version="1.0"
		// orgCode="CFCar" yyyymmdd="20150602" serviceName="AUTOFI_SEND_BILL"/>
		// <datas>
		// <data endDate="2015-02-04" dueDat="2015-02-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-01-05" paymentDueDate="2015-02-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="01" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-03-04" dueDat="2015-03-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-02-05" paymentDueDate="2015-03-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="02" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-04-04" dueDat="2015-04-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-03-05" paymentDueDate="2015-04-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="03" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-05-04" dueDat="2015-05-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-04-05" paymentDueDate="2015-05-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="04" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-06-04" dueDat="2015-06-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-05-05" paymentDueDate="2015-06-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="05" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-07-04" dueDat="2015-07-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-06-05" paymentDueDate="2015-07-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="06" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-08-04" dueDat="2015-08-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-07-05" paymentDueDate="2015-08-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="07" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-09-04" dueDat="2015-09-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-08-05" paymentDueDate="2015-09-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="08" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-10-04" dueDat="2015-10-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-09-05" paymentDueDate="2015-10-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="09" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-11-04" dueDat="2015-11-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-10-05" paymentDueDate="2015-11-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="10" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2015-12-04" dueDat="2015-12-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-11-05" paymentDueDate="2015-12-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="11" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// <data endDate="2016-01-04" dueDat="2016-01-15" billPenaltyAmt="0"
		// billPaidPenaltyAmt="0" orgCode="001" billStatus="正常" overdueDays="0"
		// billPaidPrinAmt="0" prinAmt="1200000" paidPenaltyAmt="0"
		// drawndnNo="1234567" clearDate="" billIntAmt="10000"
		// billPrinAmt="100000" intAmt="150000" paidIntAmt="0" paidPrinAmt="0"
		// loanLength="12" beginDate="2015-12-05" paymentDueDate="2016-01-20"
		// billClearDate="" applyNo="94012014070100039589S" penaltyAmt="0"
		// disAmt="30000" termNo="12" loanCustomer="张三" status="正常"
		// billPaidIntAmt="0"/>
		// </datas>
		// </parameter>
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
		// out.setBody("version:1.0|count:3|isLast:NO\napplyNo|allowLoan|failReason|reasonType|applyAmount|contractNo\n94012014070100039587S|YES|||1000|AAAAAAA");

		// out.setBody(null);

		log("file " + fileName + " generated. ");

		// return exchange;
	}

	private String makeDatas(CompositeMap parameter) {

		CompositeMap datas = parameter.getChild("datas");
		Set keySet = getKeySet(parameter);

		StringBuilder result = new StringBuilder();
		for (Object key : keySet) {
			result.append("|");
			result.append(key);
		}
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
			line.deleteCharAt(0);
			body.append("\n");
			body.append(line);
		}
		result.append(body);

		return result.toString();
	}

	private Set getKeySet(CompositeMap parameter) {
		return parameter.getChild("datas").getChild("data").keySet();
	}

	private void log(String msg) {
		ILogger logger = esbContext.getmLogger();
		logger.log(Level.SEVERE, "" + msg);
	}
}
