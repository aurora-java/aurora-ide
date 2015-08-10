package aurora.plugin.pay.haire;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *
 * <p>Title: </p>
 * <p>Description: http utils </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @version 1.0
 */
public class HttpUtils {

 
  private static final String URL_PARAM_CONNECT_FLAG = "&";
  private static final int SIZE 	= 1024 * 1024;
//  private static Log log = LogFactory.getLog(HttpUtils.class);
  
  private HttpUtils() {
  }

  /**
   * GET METHOD
   * @param strUrl String
   * @param map Map
   * @throws IOException
   * @return List
   */
  public static List URLGet(String strUrl, Map map,String enc) throws IOException {
    String strtTotalURL = "";
    List result = new ArrayList();
    if(strtTotalURL.indexOf("?") == -1) {
      strtTotalURL = strUrl + "?" + getUrl(map,enc);
    } else {
      strtTotalURL = strUrl + "&" + getUrl(map,enc);
    }
//    log.debug("strtTotalURL:" + strtTotalURL);
    URL url = new URL(strtTotalURL);
    
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//    con.setUseCaches(false);
//    con.setFollowRedirects(true);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream(),"utf-8"),SIZE);
    while (true) {
      String line = in.readLine();
      if (line == null) {
        break;
      }
      else {
    	  result.add(line);
      }
    }
    in.close();
    return (result);
  }

  /**
   * POST METHOD
   * @param strUrl String
   * @param content Map
   * @throws IOException
   * @return List
   */
	public static List URLPost(String strUrl, Map map,String enc) throws IOException {

    String content = "";
    content = getUrl(map,enc);
    String totalURL = null;
    if(strUrl.indexOf("?") == -1) {
      totalURL = strUrl + "?" + content;
    } else {
      totalURL = strUrl + "&" + content;
    }
    URL url = new URL(strUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setDoInput(true);
    con.setDoOutput(true);
    con.setAllowUserInteraction(false);
    con.setUseCaches(false);
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=GBK");
    BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(con.
        getOutputStream()));
    bout.write(content);
    bout.flush();
    bout.close();
    BufferedReader bin = new BufferedReader(new InputStreamReader(con.
        getInputStream(),"utf-8"),SIZE);
    List result = new ArrayList(); 
    while (true) {
      String line = bin.readLine();
      if (line == null) {
        break;
      }
      else {
    	  result.add(line);
      }
    }
    return (result);
  }

  /**
   * @param map Map
   * @return String
   */
  private static String getUrl(Map map,String enc) {
    if (null == map || map.keySet().size() == 0) {
      return ("");
    }
    StringBuffer url = new StringBuffer();
    Set keys = map.keySet();
    for (Iterator i = keys.iterator(); i.hasNext(); ) {
      String key = String.valueOf(i.next());
      if (map.containsKey(key)) {
    	 Object val = map.get(key);
    	 String str = val!=null?val.toString():"";
    	 try {
//			String enc = "GBK";
			str = URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        url.append(key).append("=").append(str).
            append(URL_PARAM_CONNECT_FLAG);
      }
    }
    String strURL = "";
    strURL = url.toString();
    if (URL_PARAM_CONNECT_FLAG.equals("" + strURL.charAt(strURL.length() - 1))) {
      strURL = strURL.substring(0, strURL.length() - 1);
    }
    return (strURL);
  }

}

