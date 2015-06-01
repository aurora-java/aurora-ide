package aurora.plugin.esb.adapter.cf.ali.sftp.genfile.producer;

import org.apache.camel.builder.RouteBuilder;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Producer;

public class CFAliGenFileProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	// private RouteBuilder rb;
	private AuroraEsbContext esbContext;
	// private Router r;
	// private DirectConfig config;
	private Producer producer;

	public CFAliGenFileProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	@Override
	public void configure() throws Exception {

		from("timer://foo?period=1000").bean(new ApprovalContractFile(),"genFile").to("file:/Users/shiliyan/Desktop/esb/upload?recursive=true&noop=true");
		
		from("timer://foo?period=1000").bean(new SendBillFile(),"genFile").to("file:/Users/shiliyan/Desktop/esb/upload?recursive=true&noop=true");
		
		
//		String ftp_server_url = "sftp://115.124.16.69:22/"
//				+ "upload"
//				+ "?username=cfcar&password=123456&noop=true&delay=100s&recursive=true";

		// configure properties component approval_contract

		// lets shutdown faster in case of in-flight messages stack up
//		getContext().getShutdownStrategy().setTimeout(10);
		// file:target/upload?moveFailed=../errormove=movedone""
		// move=../upload
		// &charset=utf-8
//		from(
//				"file:/Users/shiliyan/Desktop/esb/upload?recursive=true&delay=10s&noop=true")
//				// move
//				.log("Uploading file ${file:name}").to(ftp_server_url)
//				.log("Uploaded file ${file:name} complete.");

		// use system out so it stand out
		// System.out.println("*********************************************************************************");
		// System.out.println("Camel will route files from target/upload directory to the FTP server: "
		// + getContext().resolvePropertyPlaceholders("{{ftp.server}}"));
		// System.out.println("You can configure the location of the ftp server in the src/main/resources/ftp.properties file.");
		// System.out.println("If the file upload fails, then the file is moved to the target/error directory.");
		// System.out.println("Use ctrl + c to stop this application.");
		// System.out.println("*********************************************************************************");

	}
}
