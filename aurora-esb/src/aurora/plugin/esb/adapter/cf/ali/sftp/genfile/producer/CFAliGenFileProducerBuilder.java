package aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Producer;

public class CFAliGenFileProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private Producer producer;
	private CompositeMap producerMap;

	public CFAliGenFileProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	public CFAliGenFileProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	@Override
	public void configure() throws Exception {

		List childsNotNull = producerMap.getChildsNotNull();
		for (Object object : childsNotNull) {
			CompositeMap map = (CompositeMap) object;

			String timer = map.getString("timer", "timer://foo?period=3000000");
			String serviceName = map.getString("serviceName",
					"AUTOFI_APPROVAL_CONTRACT");
			String proc = map.getString("proc", "gen_file_ap");
			String saveUrl = map.getString("saveUrl", "");
			String orgCode = map.getString("orgCode", "");

			from(timer).bean(new GenFile(esbContext, serviceName, proc,orgCode),
					"genFile").to(saveUrl);
		}

		// from("timer://foo?period=10000")
		// .bean(new GenFile(esbContext, "AUTOFI_APPROVAL_CONTRACT",
		// "gen_file_ap"), "genFile")
		// .to("file:/Users/shiliyan/Desktop/esb/upload"
		// + "?recursive=true&noop=true");
		//
		// from("quartz2://timerName?cron=0 0 18 * * ?")
		// .bean(new GenFile(esbContext, "AUTOFI_SEND_BILL",
		// "gen_file_bill"), "genFile")
		// .to("file:/Users/shiliyan/Desktop/esb/upload"
		// + "?recursive=true&noop=true");

		// from("timer://foo?period=20000")
		// .bean(new GenFile(esbContext,"AUTOFI_SEND_BILL","gen_file_bill"),
		// "genFile")
		// .to("file:/Users/shiliyan/Desktop/esb/upload?recursive=true&noop=true");

		// from("timer://foo?period=1000").bean(new
		// SendBillFile(),"genFile").to("file:/Users/shiliyan/Desktop/esb/upload?recursive=true&noop=true");
		// TriggerUtils.makeDailyTrigger(hour, minute)

		// String ftp_server_url = "sftp://115.124.16.69:22/"
		// + "upload"
		// +
		// "?username=cfcar&password=123456&noop=true&delay=100s&recursive=true";

		// configure properties component approval_contract

		// lets shutdown faster in case of in-flight messages stack up
		// getContext().getShutdownStrategy().setTimeout(10);
		// file:target/upload?moveFailed=../errormove=movedone""
		// move=../upload
		// &charset=utf-8
		// from(
		// "file:/Users/shiliyan/Desktop/esb/upload?recursive=true&delay=10s&noop=true")
		// // move
		// .log("Uploading file ${file:name}").to(ftp_server_url)
		// .log("Uploaded file ${file:name} complete.");

	}
}
