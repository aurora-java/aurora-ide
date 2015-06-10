package aurora.plugin.adapter.std.ftp.download.producer;

import java.util.logging.Level;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Producer;

public class STDFtpDownloadProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private Producer producer;
	private CompositeMap producerMap;

	public STDFtpDownloadProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	public STDFtpDownloadProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	@Override
	public void configure() throws Exception {

		String downloadUrl = "sftp://115.124.16.69:22/" + "download";
		String orgCode = "CFCar";
		String downloadPara = "?username=cfcar&password=123456&delay=100s"
				+ "&noop=true" + "&recursive=true";

		String local_save_path = "file:/Users/shiliyan/Desktop/esb/download";
		String save_para = "";

		CompositeMap config = producerMap.getChild("sftp");
		downloadUrl = config.getString("downloadUrl".toLowerCase(), "");
		orgCode = config.getString("orgCode".toLowerCase(), "");
		// downloadPara = config.getString("downloadPara", "");
		downloadPara = config.getChild("downloadPara") == null ? "" : config
				.getChild("downloadPara").getText();

		String ftp_server_url = downloadUrl + "/" + orgCode
				+ downloadPara.trim();

		config = producerMap.getChild("local");
		local_save_path = config.getString("localSavePath".toLowerCase(), "");
		orgCode = config.getString("orgCode".toLowerCase(), "");
		// save_para = config.getString("savePara", "");
		save_para = config.getChild("savePara") == null ? "" : config.getChild(
				"savePara").getText();

		// + "?charset=utf-8"
		String local_url = local_save_path + "/" + orgCode + save_para.trim();

		// + "&charset=utf-8"
		// #idempotent=true
		//
		// # for the server we want to delay 5 seconds between polling the
		// server
		// # and move downloaded files to a done sub directory

		// lets shutdown faster in case of in-flight messages stack up
		getContext().getShutdownStrategy().setTimeout(10);

		from(ftp_server_url).to(local_url).bean(new LogBean(esbContext), "log")
				.log("Downloaded file ${file:name} complete.");

		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Downloaded File] " + "DOWNLOAD Task Configed");
		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Downloaded File] " + "DOWNLOAD URL " + ftp_server_url);
		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Downloaded File] " + "SAVE URL " + local_url);
		clog.log2Console("[Downloaded File] " + "DOWNLOAD Task Configed");
		clog.log2Console("[Downloaded File] " + "DOWNLOAD URL "
				+ ftp_server_url);
		clog.log2Console("[Downloaded File] " + "SAVE URL " + local_url);

	}
}
