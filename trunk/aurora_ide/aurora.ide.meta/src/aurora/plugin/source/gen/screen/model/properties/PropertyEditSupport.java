package aurora.plugin.source.gen.screen.model.properties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


abstract public class PropertyEditSupport {
	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}
	
	protected PropertyChangeListener[] getPropertyChangeListeners(){
		return listeners.getPropertyChangeListeners();
	}
			
	abstract public Object getPropertyValue(String propId);

	abstract public void setPropertyValue(String propId, Object val);
}
