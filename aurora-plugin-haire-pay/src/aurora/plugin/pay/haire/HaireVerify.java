package aurora.plugin.pay.haire;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import aurora.service.ServiceContext;

import com.client.pojo.VerifyResult;
import com.client.verify.VerifyClient;

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
			para.put(f, v);
		}
		System.out.println("===========HaireVerify=================");
//		System.out.println(para);

		// Map<String, String> sParaTemp = new TreeMap<String, String>();
		// sParaTemp.put("notify_id", request.getParameter("notify_id"));
		// sParaTemp.put("notify_type", request.getParameter("notify_type"));
		// sParaTemp.put("notify_time", request.getParameter("notify_time"));
		// sParaTemp.put("_input_charset",request.getParameter("_input_charset"));
		// sParaTemp.put("sign", request.getParameter("sign"));
		// sParaTemp.put("sign_type", request.getParameter("sign_type"));
		// sParaTemp.put("version", request.getParameter("version"));
		// sParaTemp.put("outer_trade_no",request.getParameter("outer_trade_no"));
		// sParaTemp.put("inner_trade_no",request.getParameter("inner_trade_no"));
		// sParaTemp.put("trade_status", request.getParameter("trade_status"));
		// sParaTemp.put("trade_amount", request.getParameter("trade_amount"));
		// sParaTemp.put("gmt_create", request.getParameter("gmt_create"));
		// sParaTemp.put("gmt_payment", request.getParameter("gmt_payment"));
		// sParaTemp.put("gmt_close", request.getParameter("gmt_close"));

		// <parameter notify_time="20150813150533" sign_type="ITRUSSRV"
		// notify_type="trade_status_sync" trade_status="TRADE_FINISHED"
		// gmt_payment="20150813150532" version="1.0"
		// sign="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"
		// gmt_create="20150813150533" _input_charset="UTF-8"
		// outer_trade_no="20132105152935" trade_amount="0.01"
		// inner_trade_no="101143944952179474394"
		// notify_id="74340dc3979143779cd0c53733c12c70"/>

//		String signKey = pay.getString("sign_key", "");
//		String signType = pay.getString("sign_type", "");
		String inputCharset = pay.getString("_input_charset", "UTF-8");
		// 签名验证
		try{
			VerifyResult result = VerifyClient.verifyBasic(
					inputCharset, para);
			CompositeMap haire_pay_result = model.createChild("haire_pay_result");
			String tradeStatus = pay.getString("trade_status", "");
			haire_pay_result.put("trade_status", tradeStatus);
			haire_pay_result.put("verify_result", result.isSuccess());
//			System.out.println(model.toXML());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		Map<String, String> sParaTemp = new TreeMap<String, String>();
		sParaTemp.put("notify_id", "74340dc3979143779cd0c53733c12c70");
		sParaTemp.put("notify_type", "trade_status_sync");
		sParaTemp.put("notify_time", "20150813150533");
		sParaTemp.put("_input_charset", "UTF-8");
		sParaTemp
				.put("sign",
						"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");
		sParaTemp.put("sign_type", "ITRUSSRV");
		sParaTemp.put("version", "1.0");
		sParaTemp.put("outer_trade_no", "20132105152935");
		sParaTemp.put("inner_trade_no", "101143944952179474394");
		sParaTemp.put("trade_status", "TRADE_FINISHED");
		sParaTemp.put("trade_amount", "0.01");
		sParaTemp.put("gmt_create", "20150813150533");
		sParaTemp.put("gmt_payment", "20150813150532");
		sParaTemp.put("gmt_close", "");

		// <parameter notify_time="" sign_type="" notify_type="" trade_status=""
		// gmt_payment="" version= sign= gmt_create="" _input_charset=
		// outer_trade_no=""
		// trade_amount= inner_trade_no="" notify_id=""/>

		// String signKey = pay.getString("sign_key", "");
		// String signType = pay.getString("sign_type", "");
		// String inputCharset = pay.getString("_input_charset", "");
		//
		//
		// //签名验证
		try {
			VerifyResult result = com.client.verify.VerifyClient.verifyBasic(
					"UTF-8", sParaTemp);
			System.out.println(result.isSuccess());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSignField() {
		return signField;
	}

	public void setSignField(String signField) {
		this.signField = signField;
	}

}
