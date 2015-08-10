/**
 *
 */
package com.client.util.itrus;

import com.client.pojo.VerifyResult;

/**
 * <p>签名接口</p>
 * @author guyihui
 * @version $Id: Signer.java, v 0.1 2014-7-15 下午5:51:47 guyihui Exp $
 */
public interface Signer {
	/**
	 * 签名
	 * @param text
	 * @param privateKey
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	String sign(String oriText, String privateKey, String charset) throws Exception;

	/**
	 * 验签接口
	 * @param oriText
	 * @param sign
	 * @param publicKey
	 * @param charset
	 * @return
	 */
	VerifyResult verify(String oriText, String sign, String publicKey, String charset) throws Exception;
}
