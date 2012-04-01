package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.Label;

public class LabelFigure extends Figure {

	private Label model;

	public LabelFigure() {
	}

	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		String text = model.getPrompt();
		if (text == null) {
			text = model.getPrompt();
		}
		Rectangle bounds = getBounds().getCopy();
		Point center = bounds.getCenter();
		Dimension textExtents = FigureUtilities.getTextExtents(text, getFont());
		graphics.drawText(text, center.translate(-textExtents.width / 2, -textExtents.height / 2));
	}

	public Label getModel() {
		return model;
	}

	public void setModel(Label model) {
		this.model = model;
	}
}
