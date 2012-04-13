package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.Label;

public class LabelFigure extends InputField {

	private Label model;

	public LabelFigure() {
	}

	protected void paintFigure(Graphics graphics) {
		String text = model.getPrompt() + "：";
		Dimension textExtents = FigureUtilities.getTextExtents(text, getFont());
		Rectangle textRectangle = new Rectangle();
		int pWidth = getLabelWidth() - textExtents.width;

		textRectangle.x = pWidth + getBounds().x;
		int i = getBounds().height - textExtents.height;
		textRectangle.y = i <= 0 ? getBounds().y : getBounds().y + i / 2;

		textRectangle.setSize(textExtents);
		graphics.drawText(text, textRectangle.getLocation());
		graphics.drawLine(textRectangle.getLocation().x + textRectangle.width, bounds.getTop().y + textExtents.height + 2, textRectangle.x + getBounds().width, bounds.getTop().y + textExtents.height
				+ 2);

	}

	public Label getModel() {
		return model;
	}

	public void setModel(Label model) {
		this.model = model;
	}
}
