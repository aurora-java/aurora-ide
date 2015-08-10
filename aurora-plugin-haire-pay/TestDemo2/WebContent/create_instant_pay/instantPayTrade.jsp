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
    
    <title>快捷通即时到账交易网关接口测试页面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
       <form id="form1" action="<%=request.getContextPath()%>/create_instant_pay/instantPayTradePay.jsp" method="post">

接口名称:  <input type="text" name="service" value="create_instant_pay">(例如：create_instant_pay)
<br/>
接口版本:  <input type="text" name="version" value="1.0">(目前版本：1.0)
<br/>
合作者身份ID:  <input type="text" name="partner_id" value="200000035130">
<br/>
 字符集:  <input type="text" name="_input_charset" value="UTF-8">(类型有：UTF-8;GBK;GB2312)<br/>
 签名: <input type="text" name="sign" value="">(签约合作方的钱包唯一用户号 不可空)<br/>
 签名方式: <input type="text" name="sign_type" value="ITRUSSRV">(签名方式只支持ITRUSSRV不可空)<br/>
 返回页面路径: <input type="text" name="return_url" value="http://122.224.203.210:18380/TestDemo/create_instant_pay/instantPayReturnUrlResponse.jsp">(页面跳转同步返回页面路径  可空)<br/>
 备注: <input type="text" name="memo" value=""><br/>
<%		
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
		String orderTime = format.format(new java.util.Date());
		java.text.DecimalFormat def = new java.text.DecimalFormat("000000");
		int b=(int)(Math.random()*1000000%1000000);//产生0-1000000的整数随机数
		String lstStr = def.format(b);
		String requestNo = orderTime + lstStr;
 %>
商户网站请求号: <input type="text" name="request_no" value="<%=requestNo%>">(钱包合作商户请求号)<br/>
<%		
		String lstStr1 = def.format(b);
		String tradeList = orderTime+ lstStr1 +"~a啊a~0.01~1~0.01~~zengrenhao@kjtpay.com.cn~1~instant~sss 5KG~http://www.kjtpay.com/?product-9.html~0~~~20140826164521~http://122.224.203.210:18380/TestDemo/create_instant_pay/instantPayNotifyUrlResponse.jsp";
 %>

交易列表: <input type="text" name="trade_list" style="width:500px" value="<%=tradeList%>"><br/>
操作员ID: <input type="text" name="operator_id" value="10005454">(可空)<br/>
买家ID: <input type="text" name="buyer_id" value="raymond323@126.com">anonymous<br/>
买家ID类型: <input type="text" name="buyer_id_type" value="1">(用户ID平台默认值为1 不可空)<br/>
IP地址: <input type="text" name="buyer_ip" value="202.114.12.45">(可空)<br/>
支付方式: <input type="text" name="pay_method" style="width:300px" value="online_bank^0.01^ICBC,C,GC">(支付方式^金额^备注  可空)<br/>
是否转收银台标识 :  <input type="text" name="go_cashier" value="Y">(Y使用(默认值)N不使用 可空)<br/>

<br/>
 <input type="submit" name="submit" value="确定提交支付" />

    
    </form>

  </body>
</html>
