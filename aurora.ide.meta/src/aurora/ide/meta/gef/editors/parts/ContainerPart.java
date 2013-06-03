package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.policies.ContainerLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.PasteComponentEditPolicy;
import aurora.ide.meta.gef.editors.policies.tplt.BindDropModelEditPolicy;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.Dataset;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

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
//		String mode = this.getEditorMode().getMode();
//		if (EditorMode.Template.equals(mode)) {
//			installEditPolicy(EditPolicy.LAYOUT_ROLE,
//					new TemplateContainerLayoutEditPolicy());
//		}
//		if (EditorMode.None.equals(mode)) {
			installEditPolicy(EditPolicy.LAYOUT_ROLE,
					new ContainerLayoutEditPolicy());
//		}
		installEditPolicy("Drop BM", new BindDropModelEditPolicy());
		installEditPolicy("Paste Components", new PasteComponentEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (ComponentInnerProperties.CHILDREN.equals(prop))
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

	public boolean isLayoutHorizontal() {
		return false;
	}
}
