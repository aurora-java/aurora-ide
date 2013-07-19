package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.Label;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class LabelFigure extends InputField {

	private Label model;

	public LabelFigure() {
	}

	protected void paintFigure(Graphics graphics) {
		String text = model.getPrompt() + " : ";
		Dimension textExtents = FigureUtilities.getTextExtents(text, getFont());
		Rectangle textRectangle = new Rectangle();
		int pWidth = getLabelWidth() - textExtents.width;

		textRectangle.x = pWidth + getBounds().x;
		int i = getBounds().height - textExtents.height;
		textRectangle.y = i <= 0 ? getBounds().y : getBounds().y + i / 2;

		textRectangle.setSize(textExtents);
		// graphics.drawText(text, textRectangle.getLocation());
		paintStyledText(graphics, text, ComponentProperties.prompt,
				textRectangle);
		String sd = model
				.getStringPropertyValue(ComponentInnerProperties.INPUT_SIMPLE_DATA);
		if (sd != null && "".equals(sd) == false) {
			Rectangle inputRectangle = this.getInputRectangle(textRectangle);
			Rectangle r = inputRectangle.getTranslated(1, 1).getResized(-2, -2);

			if (TextStyleUtil.isTextLayoutUseless(this.model,
					ComponentInnerProperties.INPUT_SIMPLE_DATA) == false) {
				paintStyledText(graphics, sd,
						ComponentInnerProperties.INPUT_SIMPLE_DATA, r);
			} else {
				paintSimpleData(graphics, sd, r);
			}
		}
		graphics.drawLine(textRectangle.getLocation().x + textRectangle.width,
				bounds.getBottom().y - 2, getBounds().width + getBounds().x,
				bounds.getBottom().y - 2);

	}

	public Label getModel() {
		return model;
	}

	public void setModel(Label model) {
		super.setModel(model);
		this.model = model;
	}
}
