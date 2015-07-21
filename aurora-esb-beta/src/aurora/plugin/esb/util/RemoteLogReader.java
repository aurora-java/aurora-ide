package aurora.plugin.esb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class RemoteLogReader {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.addRouteBuilder(new RouteBuilder() {

			@Override
			public void configure() throws Exception {

				String downloadUrl = "sftp://192.168.1.11:22/"
						+ "%2Fu01/CF_Leasing/aurora_esb/logs/";
				String downloadPara = "?username=root&password=123456&delay=10s"
						+ "&noop=true" + "&idempotent=false" + "&fileName=esb.log";

				String ftp_server_url = downloadUrl + downloadPara.trim();

				// + "?charset=utf-8"

				// lets shutdown faster in case of in-flight messages stack up
				getContext().getShutdownStrategy().setTimeout(10);

				from(ftp_server_url).bean(new RemoteLogReader(), "read");

			}

		});
		main.enableHangupSupport();
		main.run();
	}

	private String lastLog = "";
	private int startLine = 0;

	public List<String> readBody(String body) {
		List<String> lines = new ArrayList<String>();

		StringReader reader = new StringReader(body);

		BufferedReader br = new BufferedReader(reader);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public void read(Exchange exchange) {
		String logs = exchange.getIn().getBody(String.class);

		List<String> readBody = readBody(logs);

		for (int i = startLine; i < readBody.size(); i++) {

			String x = readBody.get(i);
			if (x.contains("AuroraEsbServer")) {
				if (x.contains("YES") || x.contains("NO"))
					System.err.println(x);
				else
					System.out.println(x);
			}
		}

		startLine = readBody.size();

		// logs.replaceFirst(lastLog, "");
		// System.out.println(logs);
		// lastLog=logs;
	}

}
