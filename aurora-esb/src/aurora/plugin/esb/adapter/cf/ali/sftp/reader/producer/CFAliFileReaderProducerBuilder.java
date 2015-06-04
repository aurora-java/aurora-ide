package aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer;

import java.util.logging.Level;

import org.apache.camel.builder.RouteBuilder;

import uncertain.composite.CompositeMap;
import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.cf.ali.sftp.download.producer.LogBean;
import aurora.plugin.esb.console.ConsoleLog;
import aurora.plugin.esb.model.Producer;

public class CFAliFileReaderProducerBuilder extends RouteBuilder {

	private ConsoleLog clog = new ConsoleLog();
	private AuroraEsbContext esbContext;
	private Producer producer;
	private CompositeMap producerMap;

	public CFAliFileReaderProducerBuilder(AuroraEsbContext esbContext,
			Producer producer) {
		this.esbContext = esbContext;
		this.producer = producer;
	}

	public CFAliFileReaderProducerBuilder(AuroraEsbContext esbContext,
			CompositeMap producer) {
		this.esbContext = esbContext;
		this.producerMap = producer;
	}

	@Override
	public void configure() throws Exception {

		getContext().getShutdownStrategy().setTimeout(10);

		String orgCode = "CFCar";

		String readingPath = "file:/Users/shiliyan/Desktop/esb/download";
		String readingPara = "?delete=true&delay=100s&recursive=true&charset=euc_cn";

		String backupPath = "file:/Users/shiliyan/Desktop/esb/download/read";
		String backupPara = "?charset=euc_cn";

		String reading_url = "file:/Users/shiliyan/Desktop/esb/download" + "/"
				+ "CFCar"
				// + "noop=true&idempotent=false&"
				+ "?delete=true&" + "delay=100s&recursive=true&charset=euc_cn";

		String backup_url = "file:/Users/shiliyan/Desktop/esb/download/read"
				+ "/" + "CFCar" + "?"
				// + "noop=true&idempotent=true&delay=5s&"
				+ "charset=euc_cn" + "&recursive=true" + "";

		CompositeMap config = producerMap.getChild("local");

		orgCode = config.getString("orgCode".toLowerCase(), "");
		readingPath = config.getString("readingPath".toLowerCase(), "");
		readingPara = config.getChild("readingPara") == null ? "" : config
				.getChild("readingPara").getText();
		// readingPara = config.getString("readingPara", "");
		backupPath = config.getString("backupPath".toLowerCase(), "");

		backupPara = config.getChild("backupPara") == null ? "" : config
				.getChild("backupPara").getText();
		// backupPara = config.getString("backupPara", "");
		String readProc = config.getString("readProc".toLowerCase(), "");

		reading_url = readingPath + "/" + orgCode + readingPara.trim();
		backup_url = backupPath + "/" + orgCode + backupPara.trim();
		from(reading_url).bean(new CFAliServiceReader(esbContext, readProc),
				"read").to(backup_url);

		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Reading File] " + "Reading File Task Configed");
		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Reading File] " + "READING_URL  " + reading_url);
		esbContext.getmLogger().log(Level.SEVERE,
				"" + "[Reading File] " + "BACKUP_URL  " + backup_url);

		clog.log2Console("[Reading File] " + "Reading File Task Configed");
		clog.log2Console("[Reading File] " + "READING_URL  " + reading_url);
		clog.log2Console("[Reading File] " + "BACKUP_URL  " + backup_url);

		// &idempotent=true
		// .to("file:/Users/shiliyan/Desktop/esb/download" + "/" + "CFCar")
		// .bean(new LogBean("Downloaded file ${file:name} complete.",
		// esbContext), "log")
		// .log("Downloaded file ${file:name} complete.");
		// noop=true&
		// &move=../
	}
}
