package aurora.plugin.adapter.std.ftp.upload.producer;

import java.util.logging.Level;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;

public class STDFtpUploadProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private CompositeMap producerMap;


	public STDFtpUploadProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	@Override
	public void configure() throws Exception {

		String uploadUrl = "sftp://115.124.16.69:22/" + "upload";
		String orgCode = "CFCar";
		String uploadPara = "?username=cfcar&password=123456&delay=100s"
				+ "&noop=true" + "&recursive=true";

		String local_uploading_path = "file:/Users/shiliyan/Desktop/esb/uploading";
		local_uploading_path += "/" + orgCode;
		String uploading_para = "?delay=10s";
		uploading_para += "" + "&recursive=true" + "&delete=true";

		String local_uploaded_path = "file:/Users/shiliyan/Desktop/esb/upload";
		local_uploaded_path += "/" + orgCode;
		String uploaded_para = "?delay=10s";
		uploaded_para += "" + "&recursive=true";

		String ftp_server_url = "sftp://115.124.16.69:22/" + "upload"
				+ "?username=cfcar&password=123456"
				+ "&noop=true&delay=100s&recursive=true";

		CompositeMap config = producerMap.getChild("sftp");
		uploadUrl = config.getString("uploadUrl".toLowerCase(), "");
		orgCode = config.getString("orgCode".toLowerCase(), "");
		uploadPara = config.getString("uploadPara".toLowerCase(), "");
		uploadPara = config.getChild("uploadPara") == null ? "" : config
				.getChild("uploadPara").getText();

		ftp_server_url = uploadUrl + "/" + orgCode + uploadPara.trim();

		config = producerMap.getChild("local");
		local_uploading_path = config.getString("uploadingPath".toLowerCase(),
				"");
		orgCode = config.getString("orgCode".toLowerCase(), "");
		// uploading_para = config.getString("uploadingPara", "");

		uploading_para = config.getChild("uploadingPara") == null ? "" : config
				.getChild("uploadingPara").getText();

		local_uploaded_path = config
				.getString("uploadedPath".toLowerCase(), "");
		// uploaded_para = config.getString("uploadedPara", "");

		uploaded_para = config.getChild("uploadedPara") == null ? "" : config
				.getChild("uploadedPara").getText();

		String uploading_url = local_uploading_path + "/" + orgCode
				+ uploading_para.trim();

		String uploaded_url = local_uploaded_path + "/" + orgCode
				+ uploaded_para.trim();

		// lets shutdown faster in case of in-flight messages stack up
		getContext().getShutdownStrategy().setTimeout(10);
		// file:target/upload?moveFailed=../errormove=movedone""
		// move=../upload
		// &charset=utf-8
		// from(
		// "file:/Users/shiliyan/Desktop/esb/upload" + "?delay=10s"
		// + "&recursive=true"
		// // + "&noop=true"
		// + "&delete=true")
		// move
		// .to("file:/Users/shiliyan/Desktop/esb/uploading"
		// + "?recursive=true"
		// // + "&delay=10s&noop=true"
		// + "")
		// from(uploading_url).log("Uploading file ${file:name}")
		// .to(ftp_server_url).to(uploaded_url)
		// .log("Uploaded file ${file:name} complete.")
		// .bean(new LogBean(esbContext), "log");

		ftp_server_url = uploadUrl + "/" + orgCode + uploadPara.trim()
				+ "&tempFileName=temp.bak";

		// upload test
		from(uploading_url).log("Uploading file ${file:name}")
				.to(ftp_server_url).to(uploaded_url)
				.log("Uploaded file ${file:name} complete.")
				.bean(new LogBean(esbContext), "log");

		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[UPLoad File] " + "UPLOAD File Task Configed");
		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[UPLoad File] " + "FTP_SERVER_URL  " + ftp_server_url);
		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[UPLoad File] " + "UPLOADED_URL  " + uploaded_url);

		clog.log2Console("[UPLoad File] " + "UPLOAD File Task Configed");
		clog.log2Console("[UPLoad File] " + "FTP_SERVER_URL  " + ftp_server_url);
		clog.log2Console("[UPLoad File] " + "UPLOADED_URL  " + uploaded_url);

	}
}
