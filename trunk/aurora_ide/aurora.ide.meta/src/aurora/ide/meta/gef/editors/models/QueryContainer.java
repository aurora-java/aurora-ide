package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.DialogEditableObject;
import aurora.ide.meta.gef.editors.property.PropertySourceUtil;

import org.eclipse.swt.graphics.Image;

public class QueryContainer extends AuroraComponent implements
		DialogEditableObject {
	private static final long serialVersionUID = -309816316947594532L;
	private Container target = null;
	private AuroraComponent owner = null;

	public QueryContainer() {
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

	public QueryContainer clone() {
		QueryContainer qc = new QueryContainer();
		qc.target = target;
		qc.owner = owner;
		return qc;
	}

	public Image getDisplayImage() {
		if (target == null)
			return null;
		return PropertySourceUtil.getImageOf(target);
	}

}
