package aurora.ide.swt.util;

import java.util.HashMap;
import java.util.Map;

public class PageModel extends PropertyEditSupport {
	private Map<Object, Object> simpleProperties = new HashMap<Object, Object>();

	public Object getPropertyValue(String propId) {
		return simpleProperties.get(propId);
	}

	public void setPropertyValue(String propId, Object val) {
		Object oldVal = simpleProperties.get(propId);
		simpleProperties.put(propId, val);
		firePropertyChange(propId, oldVal, val);
	}

	public int getIntegerPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof Integer ? (Integer) x : -1;
	}

	public Boolean getBooleanPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof Boolean ? (Boolean) x : false;
	}

	public String getStringPropertyValue(String propId) {
		Object x = this.getPropertyValue(propId);
		return x instanceof String ? x.toString() : "";
	}

	protected boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

}
