package aurora.ide.builder;

import java.util.HashMap;
import java.util.ResourceBundle;

public final class BuildMessages {
	private static ResourceBundle rb = ResourceBundle
			.getBundle("aurora.ide.builder.BuildMessages");
	private static HashMap<String, String> msgCache = new HashMap<String, String>(
			300);

	private BuildMessages() {
	}

	public static String get(String key) {
		String value = msgCache.get(key);
		if (value == null) {
			value = rb.getString(key);
			msgCache.put(key, value);
		}
		return value;
	}

	/**
	 * first get(key),if the value exists,then String.format(value,params)
	 * 
	 * @param key
	 * @param params
	 * @return
	 */
	public static String get(String key, Object... params) {
		String value = get(key);
		if (value != null)
			value = String.format(value, params);
		return value;
	}

}
