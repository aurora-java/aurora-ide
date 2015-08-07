package aurora.plugin.pay.haire;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

public class HairePay extends AbstractEntry {

	private String service;

	private String version;

	private String partner_id;

	private String _input_charset;

	private String sign;

	private String sign_type;

	private String return_url;

	private String memo;

	private String request_url;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}

	public String get_input_charset() {
		return _input_charset;
	}

	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

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
		String _service = getValue(this.service, model, "service");
		message = appendParam(message, "service", _service);
		para.put("service", _service);

		String _version = getValue(version, model, "version");
		message = appendParam(message, "version", _version);
		para.put("version", _version);

		String _partner_id = getValue(partner_id, model, "partner_id");
		message = appendParam(message, "partner_id", _partner_id);
		para.put("partner_id", _partner_id);

		String __input_charset = getValue(this._input_charset, model,
				"_input_charset");
		message = appendParam(message, "_input_charset", __input_charset);
		para.put("_input_charset", __input_charset);

		String _sign_type = getValue(sign_type, model, "sign_type");
		String _return_url = getValue(return_url, model, "return_url");
		message = appendParam(message, "return_url", _return_url);
		para.put("return_url", _return_url);

		String _memo = getValue(this.memo, model, "memo");
		message = appendParam(message, "memo", _memo);
		para.put("memo", _memo);

		String _request_url = getValue(request_url, model, "request_url");

		CompositeMap pay = context.getChild("pay");
		Set keySet = pay.keySet();
		for (Object key : keySet) {
			String value = pay.getString(key, "");
			message = appendParam(message, "" + key, value);
			para.put("" + key, value);
		}

		String _sign = getMd5(message);
		para.put("sign", _sign);
		para.put("sign_type", _sign_type);

		List urlPost = HttpUtils.URLPost(_request_url, para);
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
			String paramValue) {
		paramValue = paramValue == null ? "" : paramValue;
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

}
