package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.RadioFigure;
import aurora.ide.meta.gef.editors.models.Radio;

public class RadioPart extends InputPart {
	protected IFigure createFigure() {
		RadioFigure cbf = new RadioFigure();
		Radio model = (Radio) getModel();
		cbf.setModel(model);
		return cbf;
	}

	public RadioFigure getFigure() {
		return (RadioFigure) super.getFigure();
	}

	public Radio getModel() {
		return (Radio) super.getModel();
	}

}
