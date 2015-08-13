<%@page import="com.client.pojo.VerifyResult"%><%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%try {
		request.setCharacterEncoding("UTF-8");
	} catch (java.io.UnsupportedEncodingException e1) {
		e1.printStackTrace();
	}
	Map<String, String> sParaTemp = new TreeMap<String, String>();
	sParaTemp.put("notify_id", request.getParameter("notify_id"));
	sParaTemp.put("notify_type", request.getParameter("notify_type"));
	sParaTemp.put("notify_time", request.getParameter("notify_time"));
	sParaTemp.put("_input_charset",request.getParameter("_input_charset"));
	sParaTemp.put("sign", request.getParameter("sign"));
	sParaTemp.put("sign_type", request.getParameter("sign_type"));
	sParaTemp.put("version", request.getParameter("version"));
	sParaTemp.put("outer_trade_no",request.getParameter("outer_trade_no"));
	sParaTemp.put("inner_trade_no",request.getParameter("inner_trade_no"));
	sParaTemp.put("trade_status", request.getParameter("trade_status"));
	sParaTemp.put("trade_amount", request.getParameter("trade_amount"));
	sParaTemp.put("gmt_create", request.getParameter("gmt_create"));
	sParaTemp.put("gmt_payment", request.getParameter("gmt_payment"));
	sParaTemp.put("gmt_close", request.getParameter("gmt_close"));
	String signKey = request.getParameter("sign_key");
	String signType = request.getParameter("sign_type");
	String inputCharset = request.getParameter("_input_charset");
	//签名验证
	VerifyResult result = com.client.verify.VerifyClient.verifyBasic(
			inputCharset,  sParaTemp);
	if (result.isSuccess()) {
		//out.println("签名验证成功！");
		String tradeStatus = request.getParameter("trade_status");
		if (tradeStatus.equals("WAIT_BUYER_PAY")) {
			//out.println("交易状态：交易创建，等待买家付款");
		} else if (tradeStatus.equals("TRADE_SUCCESS")) {
			//out.println("交易状态：交易成功");
		} else if (tradeStatus.equals("TRADE_FINISHED")) {
			//out.println("交易状态：交易结束");
		} else if (tradeStatus.equals("TRADE_CLOSED")) {
			//out.println("交易状态：交易关闭");
		}
		
		
			//response.getWriter().write("success");
			//response.getWriter().flush();	
			//response.getWriter().close();
		
		
	} else 
	{
		out.write("签名验证失败！");
	}%>


