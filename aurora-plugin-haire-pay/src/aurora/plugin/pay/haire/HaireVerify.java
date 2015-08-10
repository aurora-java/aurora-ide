package aurora.plugin.pay.haire;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

import com.client.pojo.VerifyResult;

public class HaireVerify extends AbstractEntry {


	private String signField;


	public IObjectRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(IObjectRegistry registry) {
		this.registry = registry;
	}

	private IObjectRegistry registry;

	public HaireVerify(IObjectRegistry registry) {
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

//		Set keySet = pay.keySet();
//		for (Object key : keySet) {
//			String value = pay.getString(key, "");
//			para.put("" + key, value);
//		}

		
//		Map<String, String> sParaTemp = new TreeMap<String, String>();
//		sParaTemp.put("notify_id", request.getParameter("notify_id"));
//		sParaTemp.put("notify_type", request.getParameter("notify_type"));
//		sParaTemp.put("notify_time", request.getParameter("notify_time"));
//		sParaTemp.put("_input_charset",request.getParameter("_input_charset"));
//		sParaTemp.put("sign", request.getParameter("sign"));
//		sParaTemp.put("sign_type", request.getParameter("sign_type"));
//		sParaTemp.put("version", request.getParameter("version"));
//		sParaTemp.put("outer_trade_no",request.getParameter("outer_trade_no"));
//		sParaTemp.put("inner_trade_no",request.getParameter("inner_trade_no"));
//		sParaTemp.put("trade_status", request.getParameter("trade_status"));
//		sParaTemp.put("trade_amount", request.getParameter("trade_amount"));
//		sParaTemp.put("gmt_create", request.getParameter("gmt_create"));
//		sParaTemp.put("gmt_payment", request.getParameter("gmt_payment"));
//		sParaTemp.put("gmt_close", request.getParameter("gmt_close"));

		
		String signKey = pay.getString("sign_key", "");
		String signType = pay.getString("sign_type", "");
		String inputCharset = pay.getString("_input_charset", "");
		
		
		//签名验证
		VerifyResult result = com.client.verify.verifyClient.verifyBasic(
				inputCharset,  para);
		CompositeMap haire_pay_result = model.createChild("haire_pay_result");
		String tradeStatus = pay.getString("trade_status", "");
		haire_pay_result.put("trade_status", tradeStatus);
		haire_pay_result.put("verify_result", result.isSuccess());

		System.out.println(model.toXML());

		// [is_success=F&_input_charset=UTF-8&error_code=PARTNER_ID_NOT_EXIST&error_message=合作方Id不存在]

	}


	public String getSignField() {
		return signField;
	}

	public void setSignField(String signField) {
		this.signField = signField;
	}

}
