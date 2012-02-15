package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.CheckBoxFigure;
import aurora.ide.meta.gef.editors.models.CheckBox;

public class CheckBoxPart extends InputPart {
	protected IFigure createFigure() {
		CheckBoxFigure cbf = new CheckBoxFigure();
		CheckBox model = (CheckBox) getModel();
		cbf.setModel(model);
		return cbf;
	}

	public CheckBoxFigure getFigure() {
		return (CheckBoxFigure) super.getFigure();
	}

	public CheckBox getModel() {
		return (CheckBox) super.getModel();
	}

}
