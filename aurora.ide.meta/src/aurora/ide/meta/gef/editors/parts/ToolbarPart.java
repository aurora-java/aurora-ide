package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.ToolbarFigure;
import aurora.ide.meta.gef.editors.models.IProperties;

public class ToolbarPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new ToolbarFigure();
		return figure;
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (IProperties.CHILDREN.equals(prop)) {
			// TODO only test
			// Object newValue = evt.getNewValue();
			// if (newValue instanceof Button) {
			// ((Button) newValue).setButtonType(Button.ADD);
			// }
		}
		super.propertyChange(evt);
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	protected void refreshChildren() {
		super.refreshChildren();
	}
}
