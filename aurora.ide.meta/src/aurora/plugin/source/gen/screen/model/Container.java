package aurora.plugin.source.gen.screen.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

abstract public class Container extends AuroraComponent implements
		IDatasetDelegate {

	public static final String SECTION_TYPE_QUERY = "SECTION_TYPE_QUERY";
	public static final String SECTION_TYPE_BUTTON = "SECTION_TYPE_BUTTON";
	public static final String SECTION_TYPE_RESULT = "SECTION_TYPE_RESULT";
	public static final String[] SECTION_TYPES = { SECTION_TYPE_QUERY,
			SECTION_TYPE_BUTTON, SECTION_TYPE_RESULT };

	// private static int count;

	private Dataset dataset;

	// private String sectionType = null;

	protected List<AuroraComponent> children = new ArrayList<AuroraComponent>();

	public Container() {
		this.setSize(600, 80);
		this.setPrompt("");
	}

	public void addChild(AuroraComponent child) {
		addChild(child, children.size());
	}

	public void addChild(AuroraComponent child, int index) {
		if (!isResponsibleChild(child))
			return;
		children.add(index, child);
		child.setParent(this);
		fireStructureChange(ComponentInnerProperties.CHILDREN, child);
	}

	public List<AuroraComponent> getChildren() {
		return children;
	}

	// public String getNewID() {
	// return Integer.toString(count++);
	// }

	public void removeChild(AuroraComponent child) {
		// child.setParent(null);
		children.remove(child);
		fireStructureChange(ComponentInnerProperties.CHILDREN, child);
	}

	public void removeChild(int idx) {
		// children.get(idx).setParent(null);
		AuroraComponent ac = children.remove(idx);
		fireStructureChange(ComponentInnerProperties.CHILDREN, ac);
	}

	public boolean isResponsibleChild(AuroraComponent component) {
		return true;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		PropertyChangeListener[] propertyChangeListeners = this
				.getPropertyChangeListeners();
		for (PropertyChangeListener l : propertyChangeListeners) {
			if (this.dataset != null) {
				this.dataset.removePropertyChangeListener(l);
			}
			dataset.addPropertyChangeListener(l);
		}
		this.dataset = dataset;
		this.dataset.setOwner(this);
	}

	public AuroraComponent getFirstChild(Class clazz) {
		List<AuroraComponent> children = this.getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			AuroraComponent auroraComponent = (AuroraComponent) iterator.next();
			if (auroraComponent.getClass().equals(clazz))
				return (AuroraComponent) auroraComponent;
		}
		return null;
	}

	public String getSectionType() {
		// return sectionType;
		return this
				.getStringPropertyValue(ComponentInnerProperties.CONTAINER_SECTION_TYPE);
	}

	public void setSectionType(String sectionType) {
		// this.sectionType = sectionType;
		this.setPropertyValue(ComponentInnerProperties.CONTAINER_SECTION_TYPE,
				sectionType);
	}

	public String toDisplayString() {
		return this.getComponentType() + "[" + getName() + "-"
				+ this.getPrompt() + "]";
	}

	public Object getPropertyValue(String propId) {
		if (ComponentInnerProperties.DATASET_DELEGATE.equals(propId)) {
			return dataset;
		}
		if (ComponentInnerProperties.CHILDREN.equals(propId)) {
			return this.getChildren();
		}
		Object propertyValue = super.getPropertyValue(propId);
		return propertyValue;
	}

	public void setPropertyValue(String propId, Object val) {
		if (ComponentInnerProperties.DATASET_DELEGATE.equals(propId)
				&& val instanceof Dataset) {
			this.setDataset((Dataset) val);
			return;
		}
		if (ComponentInnerProperties.CHILDREN.equals(propId)
				&& val instanceof List) {
			this.children = new ArrayList<AuroraComponent>();
			for (AuroraComponent c : (List<AuroraComponent>) val) {
				this.addChild(c);
			}
			return;
		}
		super.setPropertyValue(propId, val);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		super.addPropertyChangeListener(l);
		if (dataset != null) {
			dataset.addPropertyChangeListener(l);
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		super.removePropertyChangeListener(l);
		if (dataset != null) {
			dataset.removePropertyChangeListener(l);
		}
	}

}
