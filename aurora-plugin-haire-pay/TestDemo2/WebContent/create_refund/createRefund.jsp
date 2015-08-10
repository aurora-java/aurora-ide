<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.io.*,java.sql.*,javax.sql.*,javax.naming.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>快捷通退款网关接口测试页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
       <form id="form1" action="<%=request.getContextPath()%>/create_refund/createRefundPay.jsp" method="post">

接口名称:  <input type="text" name="service" value="create_refund">(例如：create_refund)
<br/>
接口版本:  <input type="text" name="version" value="1.0">(目前版本：1.0)
<br/>
合作者身份ID:  <input type="text" name="partner_id" value="200000030006">
<br/>
 字符集:  <input type="text" name="_input_charset" value="UTF-8">(类型有：UTF-8;GBK;GB2312)<br/>
 签名: <input type="text" name="sign" value="">(签约合作方的钱包唯一用户号 不可空)<br/>
 签名方式: <input type="text" name="sign_type" value="ITRUSSRV">(签名方式只支持RSA、MD5、ITRUSSRV 不可空)<br/>
 返回页面路径: <input type="text" name="return_url" value="http://122.224.203.210:18380/TestDemo/create_refund/createRefundReturnUrlResponse.jsp">(页面跳转同步返回页面路径  可空)<br/>
 备注: <input type="text" name="memo" value=""><br/>
<%		
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String orderTime = format.format(new java.util.Date());
		java.text.DecimalFormat def = new java.text.DecimalFormat("000000");
		int b=(int)(Math.random()*1000000%1000000);//产生0-1000000的整数随机数
		String lstStr = def.format(b);
		String outerTradeNo = orderTime + lstStr;
 %>
商户网站唯一订单号: <input type="text" name="outer_trade_no" value="<%=outerTradeNo%>">(不可空)<br/>
原始的商户网站唯一订单号: <input type="text" name="orig_outer_trade_no" value="2014080614274842972018364817">(不可空)<br/>
退款金额: <input type="text" name="refund_amount" value="0.01">(单位为：RMB Yuan，精确到小数点后两位 不可空)<br/>
金额保留字段(保证金): <input type="text" name="deposit_amount" value="">(单位为：RMB Yuan，精确到小数点后两位 可空)<br/>
金额保留字段(退款担保金额): <input type="text" name="refund_ensure_amount" value="">(单位为：RMB Yuan，精确到小数点后两位 可空)<br/>
交易金额分润账号集: <input type="text" name="royalty_parameters" value="">(可空)<br/>
操作员Id :  <input type="text" name="operator_id" value="">(可空)<br/>
异步回调地址 :  <input type="text" name="callback_url" value="http://122.224.203.210:18380/TestDemo/create_refund/createRefundNotifyUrlResponse.jsp">(不可空)<br/>

<br/>
 <input type="submit" name="submit" value="确定提交退款" />

    </form>

  </body>
</html>
