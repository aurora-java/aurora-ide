package aurora.plugin.esb.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class CamelTest3 {

	public static String getMd5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) throws Exception {

		final String data = "{"
				//+ "'begin_date':'2014-11-11 10:00:00','end_date':'2014-11-28 16:31:00'"
				+ "}";

		DateFormat format1 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String now = format1.format(new Date());

		now = "2015-07-14 10:27:10";
		String upperCase = getMd5("test" + "test" + now).toUpperCase();

		final String para = "method=searchOrder&owner=test&datetime=" + now
				+ "&sign=" + upperCase + "&data=" + data;
		final String url = "http://api.rubicware.com/"
		// + para
		// "http://www.google.com"
		;
		// 签名⽅方式：
		// md5(owner+appkey+datetime).upper()
		// owner和appkey为接⼝口提供⽅方提供。datetime格式为yyyy-mm-dd hh24:mi:ss。⽣生成md5编码后要转
		// 成⼤大写。
		System.out.println(url);
		Main main = new Main();
		// main.getCamelContexts().
		main.addRouteBuilder(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("timer://foo?period=30000").process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
						Map<String, Object> paras = new HashMap<String, Object>();
						paras.put(Exchange.HTTP_METHOD, constant("POST"));
						paras.put(Exchange.HTTP_QUERY, constant(para));
						// Map<String, Object> paras = WSHelper
						// .createHeaderOptions(f.getUserName(),
						// f.getPsd());
						exchange.getOut().setHeaders(paras);
						exchange.getOut().setBody(para);
					}
				}).to(url).process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {
					//	exchange.getIn().getHeaders();
						String body = exchange.getIn().getBody(String.class);
						System.out.println(body);
					}
				});
				this.defaultErrorHandler();
				// errorHandler(new DefaultErrorHandler(System.out));
			}
		});

		main.addRouteBuilder(new RouteBuilderListener());
		
		main.enableHangupSupport();
		main.run();
	}
}
