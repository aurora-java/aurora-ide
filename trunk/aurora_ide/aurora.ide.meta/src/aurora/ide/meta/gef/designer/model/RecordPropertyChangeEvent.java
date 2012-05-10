package aurora.ide.meta.gef.designer.model;

import java.beans.PropertyChangeEvent;

public class RecordPropertyChangeEvent extends PropertyChangeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2289480259964264203L;

	public RecordPropertyChangeEvent(Object source, String propertyName,
			Object oldValue, Object newValue) {
		super(source, propertyName, oldValue, newValue);
	}

	public RecordPropertyChangeEvent(PropertyChangeEvent e) {
		super(e.getSource(), e.getPropertyName(), e.getOldValue(), e
				.getNewValue());
	}

	public RecordPropertyChangeEvent(Record r, PropertyChangeEvent e) {
		super(r, e.getPropertyName(), e.getOldValue(), e.getNewValue());
	}

}
