package aurora.plugin.esb.util;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

public class FTPDownloadProducerBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		String downloadUrl = "sftp://172.20.0.38:22/%2Fusr" + "/download";
		String orgCode = "test";
		String downloadPara = "?username=root&password=handmas&delay=500s"
				+ "&noop=true" + "&recursive=true";

		String local_save_path = "file:/Users/shiliyan/Desktop/esb/download";
		String save_para = "";

		// CompositeMap config = producerMap.getChild("sftp");
		// downloadUrl = config.getString("downloadUrl".toLowerCase(), "");
		// downloadUrl = "sftp://115.124.16.69:22/download";
		// orgCode = config.getString("orgCode".toLowerCase(), "");
		// // downloadPara = config.getString("downloadPara", "");
		// downloadPara = config.getChild("downloadPara") == null ? "" : config
		// .getChild("downloadPara").getText();

		String ftp_server_url = downloadUrl + "/" + orgCode
				+ downloadPara.trim();

		// config = producerMap.getChild("local");
		// local_save_path = config.getString("localSavePath".toLowerCase(),
		// "");
		// orgCode = config.getString("orgCode".toLowerCase(), "");
		// // save_para = config.getString("savePara", "");
		// save_para = config.getChild("savePara") == null ? "" :
		// config.getChild(
		// "savePara").getText();

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

		RouteDefinition from = from(ftp_server_url);
		from.setCustomId(true);
		from.setId("abc.efg");
		from.to(local_url);

		from("timer://foo?period=30000").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {

				CamelContext context = exchange.getContext();
				// context.getRoute("abc.efg");
				context.stopRoute("abc.efg");
				context.startRoute("abc.efg");
			}
		});

	}
}
