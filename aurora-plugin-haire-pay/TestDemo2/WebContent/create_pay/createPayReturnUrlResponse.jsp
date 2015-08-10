<%@page import="com.client.pojo.VerifyResult"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	/* try {
		request.setCharacterEncoding("UTF-8");
	} catch (java.io.UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	Map<String, String> sParaTemp = new TreeMap<String, String>();
	sParaTemp.put("is_success", request.getParameter("is_success"));
	sParaTemp.put("partner_id", request.getParameter("partner_id"));
	sParaTemp.put("_input_charset",
			request.getParameter("_input_charset"));
	sParaTemp.put("sign", request.getParameter("sign"));
	sParaTemp.put("sign_type", request.getParameter("sign_type"));
	sParaTemp.put("error_code", request.getParameter("error_code"));
	sParaTemp.put("error_message",
			request.getParameter("error_message"));
	sParaTemp.put("memo", request.getParameter("memo"));
	
	sParaTemp.put("request_no", request.getParameter("request_no"));
	sParaTemp.put("inner_pay_no", request.getParameter("inner_pay_no"));
	sParaTemp.put("inst_pay_info", request.getParameter("inst_pay_info"));

	String signType = request.getParameter("sign_type");
	String inputCharset = request.getParameter("_input_charset");

	//参数加密
	VerifyResult result = com.client.verify.verifyClient.verifyBasic(
			inputCharset, signType, sParaTemp);
	if (result.isSuccess()) {
		out.println("签名验证成功！");
		String isSuccess = request.getParameter("is_success");
		if (isSuccess.equals("T")) {
			out.println("接口调用成功！");
		} else {
			out.println("接口调用失败！");
		}
	} else {
		out.println("签名验证失败！");
	} */
	
	String a="is_success="+request.getParameter("is_success")+"partner_id="+request.getParameter("partner_id")+"_input_charset="+request.getParameter("_input_charset")+"sign="+request.getParameter("sign")+"sign_type="+request.getParameter("sign_type")+"error_code="+request.getParameter("error_code")+"error_message="+request.getParameter("error_message")+"memo="+request.getParameter("memo");
	out.println(a);
	out.println("受理成功");


%>


