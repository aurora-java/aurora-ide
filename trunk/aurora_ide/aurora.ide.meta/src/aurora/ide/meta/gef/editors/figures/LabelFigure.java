package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Graphics;
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

		Rectangle labelRectangle = getLabelRectangle();
		paintStyledText(graphics, text, ComponentProperties.prompt,
				getTextRectangle(ComponentProperties.prompt, labelRectangle));

		String sd = model
				.getStringPropertyValue(ComponentInnerProperties.INPUT_SIMPLE_DATA);
		Rectangle inputRectangle = this.getInputRectangle();
		if (sd != null && "".equals(sd) == false) {
			Rectangle r = inputRectangle.getTranslated(1, 1).getResized(-2, -2);

			if (TextStyleUtil.isTextLayoutUseless(this.model,
					ComponentInnerProperties.INPUT_SIMPLE_DATA) == false) {
				paintStyledText(graphics, sd,
						ComponentInnerProperties.INPUT_SIMPLE_DATA, r);
			} else {
				paintSimpleData(graphics, ComponentInnerProperties.INPUT_SIMPLE_DATA, r);
			}
		}
		graphics.drawLine(inputRectangle.x, bounds.getBottom().y - 2,
				getBounds().width + getBounds().x, bounds.getBottom().y - 2);

	}

	public Label getModel() {
		return model;
	}

	public void setModel(Label model) {
		super.setModel(model);
		this.model = model;
	}
}
