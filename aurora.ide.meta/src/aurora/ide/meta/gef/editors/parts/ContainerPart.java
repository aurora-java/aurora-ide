package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.DiagramLayoutEditPolicy;

public abstract class ContainerPart extends ComponentPart {

	protected List<?> getModelChildren() {
		List<AuroraComponent> children = getContainer().getChildren();
		List<AuroraComponent> result = new ArrayList<AuroraComponent>();
		for (AuroraComponent a : children) {
			if (!(a instanceof Dataset)) {
				result.add(a);
			}
		}
		return result;
	}

	private Container getContainer() {
		return (Container) getModel();
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (IProperties.CHILDREN.equals(prop))
			refreshChildren();
	}
}
