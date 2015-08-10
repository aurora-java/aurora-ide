package com.client.util;

import java.util.ResourceBundle;

public final class Config {
	
	private static ResourceBundle res = ResourceBundle.getBundle("config");
	
	public static final String cvmPath = get("cvmPath");
	
	public static final String pfxPath = get("pfxPath");
	
	public static final String pfxKey = get("pfxKey");
	
	public static final String MD5_KEY = get("MD5_KEY");
	
	public static String get(String key){
		return res.getString(key);
	}
	
	
}
