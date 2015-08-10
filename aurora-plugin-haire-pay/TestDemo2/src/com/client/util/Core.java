package com.client.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itrus.cryptorole.CryptoException;
import com.itrus.cryptorole.NotSupportException;
import com.itrus.cryptorole.bc.RecipientBcImpl;
import com.itrus.cryptorole.bc.SenderBcImpl;
import com.itrus.util.Base64;

public class Core {


    public static SenderBcImpl sender;
    public static RecipientBcImpl recipient;
    public static String pfxFileName = Config.pfxPath;
    public static String certFileName = Config.get("certPath");
    public static String keyPassword = Config.pfxKey;
    static{
    	sender = new SenderBcImpl();
    	recipient = new RecipientBcImpl();
    	try {
			sender.initCertWithKey(pfxFileName, keyPassword);
			recipient.initCertWithKey(pfxFileName, keyPassword);
			
			InputStream streamCert = new FileInputStream(certFileName);
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			X509Certificate X509Cert = (X509Certificate) factory.generateCertificate(streamCert);
			sender.addRecipientCert(X509Cert);
		} catch (NotSupportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray
     *            签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
    /**
     * 转码
     * @param sArray
     * @return
     */
      public static Map<String, String> encode(Map<String, String> sArray) {

          Map<String, String> result = new HashMap<String, String>();

          if (sArray == null || sArray.size() <= 0) {
              return result;
          }
          String charset = sArray.get("_input_charset");
          for (String key : sArray.keySet()) {
              String value = sArray.get(key);
              if (value != null && !value.equals("") ) {
              	try {
                      value = URLEncoder.encode(value, charset);
                  
              		  //改成以下两句
//                      byte [] changString=value.getBytes("iso8859-1");
//                      value=new String(changString);
                      
                  } catch (UnsupportedEncodingException e) {
                      e.printStackTrace();
                  }
              }
              
              result.put(key, value);
          }

          return result;
      }
      /**
       * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
       *
       * @param params
       *            需要排序并参与字符拼接的参数组
       * @param encode 是否需要urlEncode
       * @return 拼接后字符串
       */
    public static String createLinkString(Map<String, String> params, boolean encode) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        String charset = params.get("_input_charset");
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (encode) {
                try {
                    value = URLEncoder.encode(value, charset);
                    
                	
                    
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (i == keys.size() - 1) {// 
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

   

    public static String buildRequest(Map<String, String> sPara,String signType,String inputCharset) throws Exception {
    	String prestr = createLinkString(sPara, false); // 
    	System.out.println(prestr);
    	if(signType.equals("MD5")){
    		String mysign = MD5.sign(prestr, Config.MD5_KEY, inputCharset);
            return mysign;
    	}else{
    		String mysign = sender.signMessage(prestr);
            return mysign;
    	}
    }

    /**
     * 生成要请求给钱包的参数数组
     *
     * @param sParaTemp         请求前的参数数组
     * @return                  要请求的参数数组
     */
    public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp,String signType, String key,String inputCharset) throws Exception {
    	// 除去数组中的空值和签名参数
        Map<String, String> sPara = paraFilter(sParaTemp);
        if(StringUtils.isBlank(signType))return sPara;
        // 生成签名结果
        String mysign = buildRequest(sPara,signType,inputCharset);
        // 签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", signType);

        return encode(sPara);
    }
    //天威加密
    public static String encryptData(String oriMessage,String inputCharset){
    	String str = null;
    	byte[] encryMsg = null;
		try {
			encryMsg = sender.encryptMessage(oriMessage.getBytes(inputCharset));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	str = Base64.encode(encryMsg);
    	return str;
    }
    //天威解密
    public static String decryptData(String oriMessage,String inputCharset){
    	String str = null;
    	byte[] decryMsg = null;
		try {
			decryMsg = recipient.decryptMessage(Base64.decode(oriMessage.getBytes(inputCharset)));
			str = new String(decryMsg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
    }

}
