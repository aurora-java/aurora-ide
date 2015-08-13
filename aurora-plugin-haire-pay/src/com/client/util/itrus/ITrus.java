/**
 *
 */
package com.client.util.itrus;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.client.pojo.VerifyResult;
import com.itrus.cert.X509Certificate;
import com.itrus.cryptorole.bc.RecipientBcImpl;
import com.itrus.cryptorole.bc.SenderBcImpl;
import com.itrus.cvm.CVM;
import com.itrus.svm.SignerAndEncryptedDigest;
import com.itrus.util.Base64;


/**
 * <p>天威诚信的加签验签实现</p>
 * @author guyihui
 * @version $Id: ITrus.java, v 0.1 2014-7-15 下午2:52:25 guyihui Exp $
 */
public class ITrus implements Signer{
	private static Logger logger = LoggerFactory.getLogger(ITrus.class);
	//CVM Config
	private String cvmConfigFile = "";
	//证书路径
	private String pfxFileName = "";
	//证书访问密码
	private String keyPassword = "";
	
	private String certFileName = "";

	private SenderBcImpl sender = new SenderBcImpl();

	public void init(){
		try {
			if(StringUtils.isNotBlank(cvmConfigFile)){
				CVM.config(cvmConfigFile);
			}else{
				logger.info("未配置CVM配置文件，CVM将不会初始化");
			}
		
			if(StringUtils.isNotBlank(pfxFileName)){
				sender.initCertWithKey(pfxFileName, keyPassword);
			}else{
				logger.info("未配置私钥证书，加签服务将不会初始化");
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
		} 
	}

    /**
     * 签名字符串
     *
     * @param oriText
     *            需要签名的字符串
     * @param privateKey
     *            私钥，无需填写
     * @param input_charset
     *            编码格式
     * @return 签名结果(BASE64编码)
     */
	@Override
	public String sign(String oriText, String privateKey, String charset) throws Exception {
		return sender.signMessage(oriText);
	}

	public String getCvmConfigFile() {
		return cvmConfigFile;
	}

	public void setCvmConfigFile(String cvmConfigFile) {
		this.cvmConfigFile = cvmConfigFile;
	}

	public String getPfxFileName() {
		return pfxFileName;
	}

	public void setPfxFileName(String pfxFileName) {
		this.pfxFileName = pfxFileName;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getCertFileName() {
		return certFileName;
	}

	public void setCertFileName(String certFileName) {
		this.certFileName = certFileName;
	}

	/**
     * 签名字符串
     *
     * @param text
     *            需要签名的字符串
     * @param publicKey
     *            公钥，此实现中无需使用
     * @param sign
     *            客户签名结果
     * @param input_charset
     *            编码格式
     * @return 验签结果
     */
	@Override
	public VerifyResult verify(String oriText, String sign, String publicKey, String charset) throws Exception {
		RecipientBcImpl recipient = new RecipientBcImpl();
		SignerAndEncryptedDigest ret = null;
		byte[] toSignBuf = oriText.getBytes(charset);
		VerifyResult result = null;
		try {
			ret = recipient.verifyAndParsePkcs7(toSignBuf, Base64.decode(sign));
			X509Certificate cert = X509Certificate.getInstance(ret.getSigner());
			result = new VerifyResult(true);
			result.addInfo("subjectDN", cert.getSubjectDNString());

			//以下代码开始验证证书
			int cvm = CVM.verifyCertificate(cert);
			logger.info("证书验证结果，Return=[" + cvm + "]，");
			if (cvm != CVM.VALID) {
				String cvmMsg = "";
				switch (cvm) {
					case CVM.CVM_INIT_ERROR:
						cvmMsg = "CVM初始化错误，请检查配置文件或给CVM增加支持的CA。";
						break;
					case CVM.CRL_UNAVAILABLE:
						cvmMsg = "CRL不可用，未知状态。";
						break;
					case CVM.EXPIRED:
						cvmMsg = "证书已过期。";
						break;
					case CVM.ILLEGAL_ISSUER:
						cvmMsg = "非法颁发者。";
						break;
					case CVM.REVOKED:
						cvmMsg = "证书已吊销。";
						break;
					case CVM.UNKNOWN_ISSUER:
						cvmMsg = "不支持的颁发者。请检查cvm.xml配置文件";
						break;
					case CVM.REVOKED_AND_EXPIRED:
						cvmMsg = "证书被吊销且已过期。";
						break;
				}
				logger.info("证书非法:" + cvmMsg);
				result.setSuccess(false);
				result.addInfo(VerifyResult.exMsg, cvmMsg);
			}

		/*} catch (SignatureVerifyException ex) {
			logger.error(ex.getMessage());
		} catch (CryptoException e) {
			e.printStackTrace();*/
		/*} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}*/
		} catch(Exception ex){
			logger.error(ex.getMessage());
			result = new VerifyResult(false);
			result.addInfo(VerifyResult.exMsg, ex.getMessage());

		}

		return result;
	}

}
