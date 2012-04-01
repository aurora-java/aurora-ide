package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.figures.LabelFigure;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;
import aurora.ide.meta.gef.editors.policies.ResizeComponentEditPolicy;
import aurora.ide.meta.gef.editors.policies.tplt.TemplateNodeEditPolicy;

public class LabelPart extends ComponentPart {

	@Override
	protected IFigure createFigure() {
		LabelFigure labelFigure = new LabelFigure();
		Label model = (Label) getModel();
		labelFigure.setModel(model);
		return labelFigure;
	}
	
	@Override
	public int getResizeDirection() {
		return EAST_WEST;
	}
	
	public Label getModel() {
		return (Label) super.getModel();
	}
}
