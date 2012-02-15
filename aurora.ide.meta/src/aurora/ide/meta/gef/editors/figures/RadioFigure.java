package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.Radio;

public class RadioFigure extends InputField {
	private static final Image img_unchecked = ImagesUtils
			.getImage("palette/radio_01.png");
	private static final Image img_checked = ImagesUtils
			.getImage("palette/radio_02.png");
	private Radio model;

	public void setModel(Radio model) {
		this.model = model;
	}

	@Override
	protected void paintFigure(Graphics g) {
		String prompt = model.getPrompt() + " : ";
		Dimension textExtents = FigureUtilities.getTextExtents(prompt,
				getFont());
		Rectangle textRectangle = new Rectangle();
		int pWidth = this.getLabelWidth() - textExtents.width;
		// if (pWidth < 0) {
		// prompt = prompt.substring(0, 3) + "...";
		// textExtents = FigureUtilities.getTextExtents(prompt, getFont());
		// pWidth = this.getLabelWidth() - textExtents.width;
		// }
		Rectangle bounds = getBounds();
		textRectangle.x = pWidth + bounds.x;
		int i = bounds.height - textExtents.height;
		textRectangle.y = i <= 0 ? bounds.y : bounds.y + i / 2;

		textRectangle.setSize(textExtents);

		g.drawText(prompt, textRectangle.getLocation());
		Image img = model.isSelected() ? img_checked : img_unchecked;
		if (img != null) {
			Point imgPos = new Point();
			imgPos.x = textRectangle.getTopRight().x + 1;
			imgPos.y = bounds.y + (bounds.height - img.getBounds().height) / 2;
			g.drawImage(img, imgPos);
		}
		String text = model.getText();
		if (text != null) {
			textExtents = FigureUtilities.getTextExtents(text, getFont());
			g.setForegroundColor(ColorConstants.BLACK);
			g.drawText(text, textRectangle.getTopRight().getTranslated(16, 0));
		}

	}

}
