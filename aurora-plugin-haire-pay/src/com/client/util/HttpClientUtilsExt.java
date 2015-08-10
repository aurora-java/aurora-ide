package com.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtilsExt {
	
	private HttpClientUtilsExt(){
		
	}
	
	
	/**
	 * 使用post方式提交http请求，请求参数默认使用ISO-8859-1编码,需要调用者自己关闭CloseableHttpResponse
	 * @param url 请求地址
	 * @param params 请求参数
	 * @return  CloseableHttpResponse 
	 * @throws IOException 
	 */
	public static CloseableHttpResponse post(String url,Map<String, String> params) 
			throws IOException	{
		
		return post(url, params, null);
	}
	
	/**
	 * 使用post方式提交http请求，需要调用者自己关闭CloseableHttpResponse
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param charset 请求参数编码
	 * @return  CloseableHttpResponse 
	 * @throws IOException 
	 */
	public static CloseableHttpResponse post(String url,Map<String, String> params,String charset) 
			throws IOException	{
		
		CloseableHttpClient httpclient = createSSLClientDefault();
		HttpPost httpPost = buildHttpPost(url, params, charset);
				
		return httpclient.execute(httpPost);
		
	}
	
	/**
	 * 构建post请求数据
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static  HttpPost buildHttpPost(String url,Map<String, String> params,String charset)
			throws UnsupportedEncodingException{
		HttpPost httpPost = new HttpPost(url);
		if (params==null || params.isEmpty()) {
			return httpPost;
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		String value = "";
		for (String key : params.keySet()) {
			value = params.get(key)==null?"":params.get(key);
			
			nvps.add(new BasicNameValuePair(key, value));
		}

		if (charset != null) {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,charset));
		}else{
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		}
		return httpPost;
	}
	
	/**
	 * 使用post方式提交http请求,注意需要调用者自己关闭流
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param charset 参数值的编码格式
	 * @return  InputStream 返回输入流
	 * @throws IOException 
	 */
	public static InputStream postReturnInputStream(String url,Map<String, String> params,String charset) 
			throws IOException
			 {
		CloseableHttpResponse response = post(url, params, charset);
		return response.getEntity().getContent();
	}
	
	/**
	 * 使用post方式提交http请求,该方法会自己关闭流
	 * @param url 请求地址
	 * @param params 请求参数
	 * @param charset 参数值的编码格式
	 * @return  String 返回报文体
	 * @throws IOException 
	 */
	public static String postReturnBodyAsString(String url,Map<String, String> params,String charset) 
			throws IOException
			 {
		String content = "";
		CloseableHttpResponse response = null;
		try{
			response = post(url, params, charset);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
		}finally{
			HttpClientUtils.closeQuietly(response);
		}
		return content;
	}
	
	/**
	 * 创建信任https协议的请求，若失败则返回默认http协议
	 * @return
	 */
	public static CloseableHttpClient createSSLClientDefault() {

		try {
			TrustStrategy trustStrategy = new TrustStrategy() {

				// 信任所有
				public boolean isTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
					return true;
				}
			};
			
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null,trustStrategy ).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);

			return HttpClients.custom().setSSLSocketFactory(sslsf).build();

		} catch (KeyManagementException e) {

			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		} catch (KeyStoreException e) {

			e.printStackTrace();

		}

		return HttpClients.createDefault();

	}
}
