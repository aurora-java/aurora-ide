package aurora.ide.meta.gef.editors.parts;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.BindFormPartEditPolicy;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;

public class BoxPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		BOX model = (BOX) getModel();
		figure.setBox(model);
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy("Bind_Form", new BindFormPartEditPolicy());
	}

	protected void refreshVisuals() {
		BOX model = (BOX) getModel();
		BoxFigure figure = (BoxFigure) getFigure();
		Border border = figure.getBorder();
		if (border instanceof AbstractLabeledBorder) {
			((AbstractLabeledBorder) border).setLabel(model.getTitle());
		}
		super.refreshVisuals();

	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (IProperties.ROW.equals(prop) || IProperties.COL.equals(prop)) {
			this.getFigure().revalidate();
		}
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}

}
