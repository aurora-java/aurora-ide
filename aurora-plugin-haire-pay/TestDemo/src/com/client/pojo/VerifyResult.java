/**
 *
 */
package com.client.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>验证结果</p>
 * @author guyihui
 * @version $Id: VeriferResult.java, v 0.1 2014-7-16 下午6:19:03 guyihui Exp $
 */
public class VerifyResult {
	public static final String exMsg = "exMsg";

	public static final String msg = "msg";

	public static final String identityNo = "identityNo";

	private boolean success = false;

	private boolean needPostCheck = false;

	private Map<String, Object> info = new HashMap<String, Object>();

	public VerifyResult(){

	}

	public VerifyResult(boolean success){
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Map<String, Object> getInfo() {
		return info;
	}

	public void setInfo(Map<String, Object> info) {
		this.info = info;
	}

	public void addInfo(String key, Object val){
		this.info.put(key, val);
	}

	public boolean isNeedPostCheck() {
		return needPostCheck;
	}

	public void setNeedPostCheck(boolean needPostCheck) {
		this.needPostCheck = needPostCheck;
	}

	@Override
	public String toString() {
		return "VerifyResult [success=" + success + ", info=" + info + "]";
	}

}
