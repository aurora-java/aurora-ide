package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.ToolbarFigure;
import aurora.ide.meta.gef.editors.layout.ToolbarBackLayout2;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;

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
		if (ComponentInnerProperties.CHILDREN.equals(prop)) {
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
	public boolean isLayoutHorizontal() {
		return true;
	}
	public Rectangle layout() {
		ToolbarBackLayout2 layout = new ToolbarBackLayout2();
		return layout.layout(this);
	}
}
