package com.client.verify;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.client.pojo.VerifyResult;
import com.client.util.Core;
import com.client.util.itrus.ITrus;

/**
 * @author 
 * @version 
 * 验签
 */
public class verifyClient {
	
	private static final Logger logger = LoggerFactory.getLogger(verifyClient.class);
	
	static ITrus itrus = new ITrus();
 
    static{
    	itrus.setCvmConfigFile("F:/opt/pay/config/basis/mag/cvm.xml");
    	itrus.setKeyPassword("123456");
    	itrus.setPfxFileName("F:/opt/cafiles/200000030006.pfx");
    	itrus.init();
    }
	/**
     * 验签
     * @param tradeInfo
     * @param charset
     * @param formattedParameters
     * @throws CommonException
     */
    public static VerifyResult verifyBasic(String charset,Map<String, String> formattedParameters) throws Exception {
        //拼接签名字符串
        String signContent = Core.createLinkString(Core.paraFilter(formattedParameters),
            false);

        //传过来的签名
        String signMsg = formattedParameters.get("sign");
        if (logger.isInfoEnabled()) {
            logger.info("verify signature: { content:" + signContent + ", signMsg:"+ signMsg+ "}");
        }

        //传入签名字符串、密钥、字符集、签名方式
        VerifyResult result = verifyParameters(signContent, signMsg, charset);
        if (!result.isSuccess()) {
            //验签未通过
            logger.error(";request dosen't pass verify.");
            throw new Exception("验签未通过");
        }

        String identityNo = formattedParameters.get("identity_no");
        //验证结果为需要进行证书持有者校验 且 identityNo不为空，则校验会员标识是否是证书的持有者
        if (result.isNeedPostCheck() && StringUtils.isNotBlank(identityNo)) {
            Map<String, Object> map = result.getInfo();
            if (map != null) {
                if (!identityNo.equals(map.get(VerifyResult.identityNo))) {
                    logger.error("会员标识与证书持有者不匹配,会员标识："+identityNo+"，证书持有者："+map.get(VerifyResult.identityNo));
                    throw new Exception("会员标识与证书持有者不匹配");
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("invoke verify end:" + result.isSuccess());
        }
        
        return result;
    }
    
    public  static VerifyResult verifyParameters(String content, String signature, String charset) throws Exception {
		if (signature != null) {
			signature = signature.replace(' ', '+');
		}
		
		VerifyResult result = new VerifyResult();

		try {
			
			result = itrus.verify(content, signature, null, charset);
			
		} catch (Exception e) {
			logger.error("verify failure for content:" + content + ",signature:" + signature , e);
			throw new Exception("签名失败");
		}
		return result;
	}

}
