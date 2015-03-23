package aurora.ide.api.composite.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Comment {

	public static final String PAGE_NAME = "pagename";

	private Map<String, Object> setting = new HashMap<String, Object>();

	public Set<String> keys() {
		return getSetting().keySet();
	}

	public Object get(String key) {
		return getSetting().get(key);
	}

	public void put(String key, Object value) {
		getSetting().put(key, value);
	}

	public Map<String, Object> getSetting() {
		return setting;
	}

	public void setSetting(Map<String, Object> setting) {
		this.setting = setting;
	}

}
