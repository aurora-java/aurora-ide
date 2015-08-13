<%@page import="com.client.pojo.VerifyResult"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	try {
		request.setCharacterEncoding("UTF-8");
	} catch (java.io.UnsupportedEncodingException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	Map<String, String> sParaTemp = new TreeMap<String, String>();
	sParaTemp.put("notify_id", request.getParameter("notify_id"));
	sParaTemp.put("notify_type", request.getParameter("notify_type"));
	sParaTemp.put("notify_time", request.getParameter("notify_time"));
	sParaTemp.put("_input_charset",
	request.getParameter("_input_charset"));
	sParaTemp.put("sign", request.getParameter("sign"));
	sParaTemp.put("sign_type", request.getParameter("sign_type"));
	sParaTemp.put("version", request.getParameter("version"));
	
	sParaTemp.put("orig_outer_trade_no",
	request.getParameter("orig_outer_trade_no"));
	sParaTemp.put("outer_trade_no", request.getParameter("outer_trade_no"));
	sParaTemp.put("inner_trade_no", request.getParameter("inner_trade_no"));
	sParaTemp.put("refund_amount", request.getParameter("refund_amount"));
	sParaTemp.put("refund_status", request.getParameter("refund_status"));
	sParaTemp.put("gmt_refund", request.getParameter("gmt_refund"));

	String signType = request.getParameter("sign_type");
	String inputCharset = request.getParameter("_input_charset");

	//参数加密
	VerifyResult result = com.client.verify.VerifyClient.verifyBasic(
	inputCharset, signType, sParaTemp);
	if (result.isSuccess()) {
		//out.println("通知成功！");
		String tradeStatus = request.getParameter("refund_status");
		if (tradeStatus.equals("REFUND_SUCCESS")) {
	out.println("退款成功！");
		} else if (tradeStatus.endsWith("REFUND_FAIL")) {
	out.println("退款失败！");
	} else {
		out.println("通知失败！");
	  }
		
		response.getWriter().write("success");
		response.getWriter().flush();	
		response.getWriter().close();
}
%>


