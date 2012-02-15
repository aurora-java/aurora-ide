package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class Container extends AuroraComponent implements IDatasetDelegate {

	static final long serialVersionUID = 1;
	public static final String SECTION_TYPE_QUERY = "SECTION_TYPE_QUERY";
	public static final String SECTION_TYPE_BUTTON = "SECTION_TYPE_BUTTON";
	public static final String SECTION_TYPE_RESULT = "SECTION_TYPE_RESULT";
	public static final String[] SECTION_TYPES = { SECTION_TYPE_QUERY,
			SECTION_TYPE_BUTTON, SECTION_TYPE_RESULT };

	private static int count;

	private Dataset dataset;

	private String sectionType = null;

	protected List<AuroraComponent> children = new ArrayList<AuroraComponent>();

	public Container() {
		this.setSize(new Dimension(600, 80));
	}

	public void addChild(AuroraComponent child) {
		addChild(child, children.size());
	}

	public void addChild(AuroraComponent child, int index) {
		if (!isResponsibleChild(child))
			return;
		children.add(index, child);
		child.setParent(this);
		fireStructureChange(CHILDREN, child);
	}

	public List<AuroraComponent> getChildren() {
		return children;
	}

	public String getNewID() {
		return Integer.toString(count++);
	}

	public void removeChild(AuroraComponent child) {
		// child.setParent(null);
		children.remove(child);
		fireStructureChange(CHILDREN, child);
	}

	public void removeChild(int idx) {
		// children.get(idx).setParent(null);
		AuroraComponent ac = children.remove(idx);
		fireStructureChange(CHILDREN, ac);
	}

	public boolean isResponsibleChild(AuroraComponent component) {
		return true;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
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
		return sectionType;
	}

	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}

}
