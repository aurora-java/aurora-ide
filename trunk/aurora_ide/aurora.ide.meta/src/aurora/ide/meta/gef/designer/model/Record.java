package aurora.ide.meta.gef.designer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import aurora.ide.meta.gef.designer.editor.BMModelViewer;

public class Record {

	private HashMap<String, Object> map = new HashMap<String, Object>();
	PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public Record() {
	}

	public final Object get(String key) {
		if (key != null)
			key = key.toLowerCase();
		return map.get(key);
	}

	public final void put(String key, Object value) {
		if (key != null)
			key = key.toLowerCase();
		Object old = map.get(key);
		map.put(key, value);
		if ((old == null && value != null) || (old != null && value == null)
				|| (!old.equals(value)))
			firePropertyChange(key, old, value);
	}

	public String getString(String key) {
		Object obj = get(key);
		if (obj == null)
			return null;
		return obj.toString();
	}

	public String getStringNotNull(String key) {
		String value = getString(key);
		return value == null ? "" : value;
	}

	public int getNum() {
		return getInt(BMModelViewer.COLUMN_NUM);
	}

	public void setNum(int num) {
		put(BMModelViewer.COLUMN_NUM, num);
	}

	public String getPrompt() {
		return getString(BMModelViewer.COLUMN_PROMPT);
	}

	public void setPrompt(String prompt) {
		put(BMModelViewer.COLUMN_PROMPT, prompt);
	}

	public Integer getInt(String key) {
		Object obj = get(key);
		if (obj == null)
			return 0;
		else if (obj instanceof String)
			return Integer.parseInt((String) obj);
		return ((Integer) obj);
	}

	public boolean getBoolean(String key) {
		Object obj = get(key);
		if (obj == null)
			return false;
		else if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else if (obj instanceof String)
			return obj.equals("true");
		return false;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	@SuppressWarnings("unchecked")
	public Record clone() {
		Record r = new Record();
		r.map = (HashMap<String, Object>) this.map.clone();
		return r;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(1000);
		for (String key : BMModelViewer.TABLE_COLUMN_PROPERTIES) {
			if (key.length() == 0)
				continue;
			sb.append(key);
			sb.append(" = ");
			sb.append(get(key));
			sb.append(" ;  ");
		}
		return sb.toString();
	}

	public String getName() {
		return getStringNotNull(BMModelViewer.COLUMN_NAME);
	}
}