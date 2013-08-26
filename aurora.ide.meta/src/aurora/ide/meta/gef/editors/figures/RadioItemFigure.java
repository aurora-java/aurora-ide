package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.RadioItem;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class RadioItemFigure extends InputField {
	private static final Image img_unchecked = PrototypeImagesUtils
			.getImage("palette/radio_01.png");
	private RadioItem model;

	public void setModel(RadioItem model) {
		super.setModel(model);
		this.model = model;
	}

	public int getLabelWidth() {
		// IFigure parent = getParent();
		// if (parent instanceof BoxFigure) {
		// return ((BoxFigure) parent).getLabelWidth();
		// } else {
		// return ScreenBody.DLabelWidth;
		// }
		return 0;
	}

	@Override
	protected void paintFigure(Graphics g) {
		// String prompt = model.getPrompt() + " : ";
		// Dimension textExtents = FigureUtilities.getTextExtents(prompt,
		// getFont());
		// Rectangle textRectangle = new Rectangle();
		// int pWidth = this.getLabelWidth() - textExtents.width;
		Rectangle bounds = getBounds();
		// textRectangle.x = pWidth + bounds.x;
		// int i = bounds.height - textExtents.height;
		// textRectangle.y = i <= 0 ? bounds.y : bounds.y + i / 2;
		//
		// textRectangle.setSize(textExtents);

		// g.drawText(prompt, textRectangle.getLocation());
		// paintStyledText(g, prompt, ComponentProperties.prompt,
		// textRectangle);
		// Image img = model.isSelected() ? img_checked : img_unchecked;
		Image img = img_unchecked;
		if (img != null) {
			Point imgPos = new Point();
			imgPos.x = bounds.getTopLeft().x + 2;
			imgPos.y = bounds.y + (bounds.height - img.getBounds().height) / 2;
			g.drawImage(img, imgPos);
		}
		String text = model.getText();
		if (text != null) {
			// Dimension textExtents = FigureUtilities.getTextExtents(text,
			// getFont());

			if (TextStyleUtil.isTextLayoutUseless(this.model,
					ComponentProperties.text) == false) {
				paintStyledText(g, text, ComponentProperties.text, bounds
						.getTopLeft().getTranslated(18, 2));
			} else {
				g.setForegroundColor(ColorConstants.BLACK);
				g.drawText(text, bounds.getTopLeft().getTranslated(18, 2));
			}

		}
	}

	protected void paintStyledText(Graphics g, String text, String property_id,
			Point p) {
		g.pushState();
		this.disposeResource(property_id);
		g.setForegroundColor(ColorConstants.BLACK);
		// Dimension dim = FigureUtilities.getTextExtents(text, getFont());
		// FigureUtilities.
		// if (ComponentProperties.prompt.equals(property_id) == false)
		// g.setClip(r.getResized(-16, 0));
		TextLayout tl = new TextLayout(null);
		tl.setText(text);
		tl.setFont(getFont());
		Object obj = model.getPropertyValue(property_id
				+ ComponentInnerProperties.TEXT_STYLE);
		TextStyle ts = null;
		if (obj instanceof StyledStringText) {
			ts = TextStyleUtil.createTextStyle((StyledStringText) obj,
					Display.getDefault(), getFont());
		} else {
			ts = new TextStyle();
		}
		tl.setStyle(ts, 0, text.length() - 1);
		// Point p = new Point(r.x + 2, r.y + (r.height - dim.height) / 2);
		// if (ComponentProperties.prompt.equals(property_id)) {
		// g.drawTextLayout(tl, copy.x, copy.y);
		// } else {
		// g.drawTextLayout(tl, p.x, p.y);
		// }
		g.drawTextLayout(tl, p.x, p.y);
		this.handleResource(property_id, tl);
		g.popState();
	}

}
