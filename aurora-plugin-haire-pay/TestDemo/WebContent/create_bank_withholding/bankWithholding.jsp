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
    
    <title>快捷通银行代扣网关接口测试页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
       <form id="form1" action="<%=request.getContextPath()%>/create_bank_withholding/bankWithholdingPay.jsp" method="post">

接口名称:  <input type="text" name="service" value="create_bank_withholding">(例如：create_bank_withholding)
<br/>
接口版本:  <input type="text" name="version" value="1.0">(目前版本：1.0)
<br/>
合作者身份ID:  <input type="text" name="partner_id" value=200000030006>
<br/>
 字符集:  <input type="text" name="_input_charset" value="UTF-8">(类型有：UTF-8;GBK;GB2312)<br/>
 签名: <input type="text" name="sign" value="">(签约合作方的钱包唯一用户号 不可空)<br/>
 签名方式: <input type="text" name="sign_type" value="ITRUSSRV">(签名方式只支持ITRUSSRV不可空)<br/>
 返回页面路径: <input type="text" name="return_url" value="http://122.224.203.210:18380/TestDemo/create_bank_withholding/instantPayReturnUrlResponse.jsp">(页面跳转同步返回页面路径  可空)<br/>
 备注: <input type="text" name="memo" value=""><br/>
<%		
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String orderTime = format.format(new java.util.Date());
		java.text.DecimalFormat def = new java.text.DecimalFormat("000000");
		int b=(int)(Math.random()*1000000%1000000);//产生0-1000000的整数随机数
		String lstStr = def.format(b);
		String requestNo = orderTime + lstStr;
 %>
商户订单号: <input type="text" name="outer_trade_no" value="<%=requestNo%>">(商户订单号)<br/>
用户姓名: <input type="text" name="user_name" value="">(用户姓名)<br/>
证件类型: <input type="text" name="certificates_type" value="1">(证件类型  1：身份证)<br/>
证件号: <input type="text" name="certificates_number" value=""><br/>
银行卡号: <input type="text" name="card_no" value=""><br/>
银行编码: <input type="text" name="bank_code" value=""><br/>
交易金额: <input type="text" name="payable_amount" value=""><br/>
提交时间: <input type="text" name="submit_time" value="20150101235959">(提交时间)<br/>
异步通知地址:<input type="text" name="notify_url" value="http://122.224.203.210:18380/TestDemo/create_bank_withholding/instantPayReturnUrlResponse.jsp">(异步通知地址)<br/>

<br/>
 <input type="submit" name="submit" value="确定提交支付" />

    
    </form>

  </body>
</html>
