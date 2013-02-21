package aurora.ide.meta.gef.editors.models;

import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.property.DialogEditableObject;
import aurora.ide.meta.gef.editors.property.PropertySourceUtil;

public class ContainerHolder extends AuroraComponent implements
		DialogEditableObject {
	private static final long serialVersionUID = -309816316947594532L;
	private Container target = null;
	private AuroraComponent owner = null;
	private String containerType = BOX.SECTION_TYPE_QUERY;

	public ContainerHolder() {
		super();
	}

	public String getDescripition() {
		if (target == null)
			return "";
		return target.getPrompt();
	}

	public Container getTarget() {
		return target;
	}

	public void setTarget(Container target) {
		this.target = target;
	}

	public AuroraComponent getOwner() {
		return owner;
	}

	public void setOwner(AuroraComponent owner) {
		this.owner = owner;
	}

	public Object getContextInfo() {
		return owner;
	}

	public String getQueryDateset() {
		if (target == null || target.getDataset() == null)
			return "";
		return target.getDataset().getId();
	}

	public ContainerHolder clone() {
		ContainerHolder qc = new ContainerHolder();
		qc.target = target;
		qc.owner = owner;
		qc.containerType = containerType;
		return qc;
	}

	public Image getDisplayImage() {
		if (target == null)
			return null;
		return PropertySourceUtil.getImageOf(target);
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

}
