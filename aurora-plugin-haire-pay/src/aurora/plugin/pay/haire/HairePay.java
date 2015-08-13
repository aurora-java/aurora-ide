package aurora.plugin.pay.haire;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

public class HairePay extends AbstractEntry {

	private String request_url;

	private String signField;

	public String getRequest_url() {
		return request_url;
	}

	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}

	public IObjectRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(IObjectRegistry registry) {
		this.registry = registry;
	}

	private IObjectRegistry registry;

	public HairePay(IObjectRegistry registry) {
		this.registry = registry;
		// uncertainEngine = (UncertainEngine)
		// registry.getInstanceOfType(UncertainEngine.class);
	}

	private String getValue(String s, CompositeMap model, String name) {
		String templateName = s;
		if (templateName != null)
			templateName = uncertain.composite.TextParser.parse(templateName,
					model);
		if (templateName == null)
			throw new IllegalArgumentException(name + " can not be null!");
		return templateName;
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {

		CompositeMap context = runner.getContext();
		ServiceContext service = ServiceContext.createServiceContext(context);
		CompositeMap model = service.getModel();

		Map<String, String> para = new HashMap<String, String>();

		String message = "";
		CompositeMap pay = context.getChild("pay");
		String __input_charset = pay.getString("_input_charset", "UTF-8");

		String[] fileds = signField.split(",");
		for (String f : fileds) {
			String v = pay.getString(f, "");
//			message = appendParam(message, f, v, __input_charset);
			para.put(f, v);
		}
		
		String cardNo = com.client.util.Core.encryptData(pay.getString("card_no", ""),__input_charset);	
//		//证件号加密
		String certificatesNumber = com.client.util.Core.encryptData(pay.getString("certificates_number", ""),__input_charset);
//		
		para.put("certificates_number", certificatesNumber);
		para.put("card_no", cardNo);
		

//		Set keySet = pay.keySet();
//		for (Object key : keySet) {
//			String value = pay.getString(key, "");
//			para.put("" + key, value);
//		}

		String _request_url = getValue(request_url, model, "request_url");

		String signKey = pay.getString("sign_key", "");
		String signType = pay.getString("sign_type", "");
		String inputCharset = pay.getString("_input_charset", "");

		Map<String, String> map = com.client.util.Core.buildRequestPara(para,
				signType, signKey, inputCharset);

		// String _sign = MD5Util.md5Hex(message);
		// para.put("sign", _sign);
		// para = encode(para);

		para = map;

		System.out.println(para);
		// System.out.println(message);
		// System.out.println(_sign);
		System.out.println("===================================");

		List urlPost = HttpUtils.URLPost(_request_url, para, __input_charset);
		// is_success=F&_input_charset=UTF-8&error_code=PARTNER_ID_NOT_EXIST&error_message=合作方Id不存在
		CompositeMap haire_pay_result = model.createChild("haire_pay_result");
		for (Object object : urlPost) {
			String r = "" + object;
			String[] split = r.split("&");
			for (String s : split) {
				String[] split2 = s.split("=");
				if (split2.length == 2) {
					haire_pay_result.put(split2[0], split2[1]);
				}
				if (split2.length == 1) {
					haire_pay_result.put(split2[0], "");
				}
			}
		}

		System.out.println(model.toXML());

		// [is_success=F&_input_charset=UTF-8&error_code=PARTNER_ID_NOT_EXIST&error_message=合作方Id不存在]

	}

	static public String appendParam(String returnStr, String paramId,
			String paramValue, String enc) throws UnsupportedEncodingException {
		paramValue = paramValue == null ? "" : paramValue;
		// String enc = "GBK";
		// paramValue = URLEncoder.encode(paramValue, enc);
		if (!returnStr.equals("")) {
			returnStr = returnStr + "&" + paramId + "=" + paramValue;
		} else {
			returnStr = paramId + "=" + paramValue;
		}
		return returnStr;
	}

	final public static String getMd5(final String plainText) {
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			final byte b[] = md.digest();

			int i;

			final StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Map<String, String> encode(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}
		String charset = sArray.get("_input_charset");
		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value != null && !value.equals("")) {
				try {
					value = URLEncoder.encode(value, charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			result.put(key, value);
		}

		return result;
	}

	public String getSignField() {
		return signField;
	}

	public void setSignField(String signField) {
		this.signField = signField;
	}

	public static void main(String[] args) {
		// 访问地址
		// String gatewayUrl =
		// "http://localhost:9080/mag/gateway/receiveOrder.do";
		String gatewayUrl = "https://zmag.kjtpay.com/mag/gateway/receiveOrder.do";

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_bank_withholding");
		sParaTemp.put("version", "1.0");
		sParaTemp.put("partner_id", "200000030006");
		sParaTemp.put("_input_charset", "UTF-8");
		sParaTemp.put("sign", "");
		sParaTemp.put("sign_type", "MD5");
		sParaTemp.put("return_url", "www.baidu.com");
		sParaTemp.put("memo", "");

		// 银行卡号加密
		// String cardNo =
		// com.client.util.Core.encryptData(request.getParameter("card_no"),inputCharset);
		// //证件号加密
		// String certificatesNumber =
		// com.client.util.Core.encryptData(request.getParameter("certificates_number"),inputCharset);

		sParaTemp.put("outer_trade_no", "200000030006");
		sParaTemp.put("user_name", "曾某某");
		sParaTemp.put("certificates_type", "1");
		sParaTemp.put("certificates_number", "203980187208171829");
		sParaTemp.put("bank_code", "ABC");
		sParaTemp.put("card_no", "4567874365729987");
		sParaTemp.put("payable_amount", "776.1");
		sParaTemp.put("submit_time", "");
		sParaTemp.put("notify_url", "www.baidu.com");

		String signKey = "a";
		String signType = "MD5";
		// 参数加密
		try {
			Map<String, String> map = com.client.util.Core.buildRequestPara(
					sParaTemp, signType, signKey, "UTF-8");
			List urlPost = HttpUtils.URLPost(gatewayUrl, sParaTemp, "UTF-8");
			// is_success=F&_input_charset=UTF-8&error_code=PARTNER_ID_NOT_EXIST&error_message=合作方Id不存在
			CompositeMap haire_pay_result = new CompositeMap("haire_pay_result");
			for (Object object : urlPost) {
				String r = "" + object;
				String[] split = r.split("&");
				for (String s : split) {
					String[] split2 = s.split("=");
					if (split2.length == 2) {
						haire_pay_result.put(split2[0], split2[1]);
					}
					if (split2.length == 1) {
						haire_pay_result.put(split2[0], "");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
