package aurora.plugin.esb.adapter.cf.ali.sftp.download.producer;

import java.util.logging.Level;

import org.apache.camel.Exchange;

import aurora.plugin.esb.AuroraEsbContext;
import aurora.plugin.esb.adapter.cf.ali.sftp.reader.producer.ServiceFile;
import aurora.plugin.esb.console.ConsoleLog;
import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

public class LogBean {

	private AuroraEsbContext esbContext;

	public LogBean(AuroraEsbContext esbContext) {
		this.esbContext = esbContext;
	}

	public void log(Exchange exchange) {
		Object header = exchange.getIn().getHeader("CamelFileNameOnly");

//		ServiceFile sn = new ServiceFile(header.toString());
//		if (sn.isInvalid() == false) {
//			CompositeMap properties = esbContext.getProperties();
//			String service = sn.getService();
//			String batchNo = sn.getBatchNo();
//			String yymmdd = sn.getYymmdd();
//			CompositeMap serviceNode = properties.getChild(service);
//			if (serviceNode == null)
//				serviceNode = properties.createChild(service);
//			serviceNode.put("batchno", batchNo);
//			serviceNode.put("yymmdd", yymmdd);
//			esbContext.saveProperties();
//		}

		ILogger logger = esbContext.getmLogger();
		String msg = "[Downloaded File] " + "Downloaded File " + header
				+ " Complete.";
		logger.log(Level.SEVERE, "" + msg);
		new ConsoleLog().log2Console(msg);
	}
}
