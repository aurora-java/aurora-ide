package aurora.ide.meta.gef.designer.model;

import java.beans.PropertyChangeEvent;

public class RelationPropertyChangeEvent extends PropertyChangeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6135453464218986963L;

	public RelationPropertyChangeEvent(Object source, String propertyName,
			Object oldValue, Object newValue) {
		super(source, propertyName, oldValue, newValue);
	}

	public RelationPropertyChangeEvent(PropertyChangeEvent e) {
		super(e.getSource(), e.getPropertyName(), e.getOldValue(), e
				.getNewValue());
	}

	public RelationPropertyChangeEvent(Record r, PropertyChangeEvent e) {
		super(r, e.getPropertyName(), e.getOldValue(), e.getNewValue());
	}
}
