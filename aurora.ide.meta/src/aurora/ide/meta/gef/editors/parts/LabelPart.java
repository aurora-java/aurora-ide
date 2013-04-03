package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.LabelFigure;
import aurora.plugin.source.gen.screen.model.Label;

public class LabelPart extends InputPart {

	@Override
	protected IFigure createFigure() {
		LabelFigure labelFigure = new LabelFigure();
		Label model = (Label) getModel();
		labelFigure.setModel(model);
		return labelFigure;
	}
	
//	@Override
//	public int getResizeDirection() {
//		return EAST_WEST;
//	}
	
	public Label getModel() {
		return (Label) super.getModel();
	}
}
