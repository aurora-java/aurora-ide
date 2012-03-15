package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.tplt.BindDropModelEditPolicy;
import aurora.ide.meta.gef.editors.policies.tplt.TemplateContainerLayoutEditPolicy;

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
		String mode = this.getEditorMode().getMode();
		if (EditorMode.Template.equals(mode)) {
			installEditPolicy(EditPolicy.LAYOUT_ROLE,
					new TemplateContainerLayoutEditPolicy());
			installEditPolicy("Drop BM", new BindDropModelEditPolicy());
		}
		if (EditorMode.None.equals(mode)) {
			installEditPolicy(EditPolicy.LAYOUT_ROLE,
					new ContainerLayoutEditPolicy());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (IProperties.CHILDREN.equals(prop))
			refreshChildren();
	}

	public void applyToModel() {
		List children = this.getChildren();
		for (Object child : children) {
			if (child instanceof ContainerPart) {
				((ContainerPart) child).applyToModel();
			}
		}
	}
}
