package com.client.verify;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.client.pojo.VerifyResult;
import com.client.util.Config;
import com.client.util.Core;
import com.client.util.MD5;
import com.client.util.itrus.ITrus;

/**
 * @author 
 * @version 
 */
public class verifyClient {
	
	private static final Logger logger = LoggerFactory.getLogger(verifyClient.class);
	
	static ITrus itrus = new ITrus();
 
    static{
    	itrus.setCvmConfigFile(Config.cvmPath);
    	itrus.setKeyPassword(Config.pfxKey);
    	itrus.setPfxFileName(Config.pfxPath);
    	itrus.init();
    }
    
    public static VerifyResult verifyBasic(String charset,String sigpType, Map<String, String> formattedParameters) throws Exception {
    	return verifyBasic(charset, formattedParameters);
    }
	/**
     * @param tradeInfo
     * @param charset
     * @param formattedParameters
     * @throws CommonException
     */
    public static VerifyResult verifyBasic(String charset, Map<String, String> formattedParameters) throws Exception {
                String signContent = Core.createLinkString(Core.paraFilter(formattedParameters), false);

        String signMsg = formattedParameters.get("sign");
        String signType = formattedParameters.get("sign_type");
        if (logger.isInfoEnabled()) {
            logger.info("verify signature: { content:" + signContent + ", signMsg:"+ signMsg+ "}");
        }

               VerifyResult result = verifyParameters(signContent, signMsg,signType, charset);
        if (!result.isSuccess()) {
            logger.error(";request dosen't pass verify.");
            throw new Exception("验证签名失败");
        }

        String identityNo = formattedParameters.get("identity_no");
        if (result.isNeedPostCheck() && StringUtils.isNotBlank(identityNo)) {
            Map<String, Object> map = result.getInfo();
            if (map != null) {
                if (!identityNo.equals(map.get(VerifyResult.identityNo))) {
                    logger.error("签名验证错误"+identityNo+"数据新歌想"+map.get(VerifyResult.identityNo));
                    throw new Exception("验证 identityNo异常");
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("invoke verify end:" + result.isSuccess());
        }
        
        return result;
    }
    
    public  static VerifyResult verifyParameters(String content, String signature,String signType, String charset) throws Exception {
    	if (signature != null) {
			signature = signature.replace(' ', '+');
		}
		
		VerifyResult result = new VerifyResult();

		try {
			
			if(signType.equals("MD5")){
				result = new VerifyResult(MD5.verify(content, signature, Config.MD5_KEY, charset));
			}else
				result = itrus.verify(content, signature, null, charset);
			
		} catch (Exception e) {
			logger.error("verify failure for content:" + content + ",signature:" + signature , e);
			throw new Exception("签名失败");
		}
		return result;
	}

}
