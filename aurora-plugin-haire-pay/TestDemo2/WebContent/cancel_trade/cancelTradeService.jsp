<%@page import="com.client.util.Core"%>
<%@ page language="java" import="java.util.*,java.net.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.io.IOException"%>

<%
	    //访问地址
		//String gatewayUrl = "http://localhost:9080/mag/gateway/receiveOrder.do";
		String gatewayUrl = "http://mag.kjtpay.com/mag/gateway/receiveOrder.do";
        //String gatewayUrl = "http://zmag.kjtpay.com/mag/gateway/receiveOrder.do";
        
        try {
			request.setCharacterEncoding("UTF-8");
		} catch (java.io.UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
        
		Map<String, String> sParaTemp = new TreeMap<String, String>();
		sParaTemp.put("service", request.getParameter("service"));
		sParaTemp.put("version", request.getParameter("version"));
		sParaTemp.put("partner_id", request.getParameter("partner_id"));
		sParaTemp.put("_input_charset", request.getParameter("_input_charset"));
		sParaTemp.put("sign", request.getParameter("sign"));
		sParaTemp.put("sign_type", request.getParameter("sign_type"));
		sParaTemp.put("return_url", request.getParameter("return_url"));
		sParaTemp.put("memo", request.getParameter("memo"));
		
		sParaTemp.put("request_no", request.getParameter("request_no"));
		sParaTemp.put("outer_trade_no", request.getParameter("outer_trade_no"));
		sParaTemp.put("reason", request.getParameter("reason"));
		
		String signKey = request.getParameter("sign_key");
        String signType = request.getParameter("sign_type");
        String inputCharset = request.getParameter("_input_charset");
		//生成签名
		Map<String, String> map = com.client.util.Core.buildRequestPara(sParaTemp,signType,signKey,inputCharset);
		
		response.setContentType("text/html;charset=UTF-8");   
		response.setCharacterEncoding("UTF-8");

		//拼装返回的信息 form表单 
		StringBuilder html = new StringBuilder();
		html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
		html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(gatewayUrl).append("\" method=\"post\">\n");

		for (String key : map.keySet()) {
	        html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + map.get(key) + "\">\n");
		}
		html.append("</form>\n");
		System.out.println(html.toString());
		try {
			response.getWriter().write(html.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
%>