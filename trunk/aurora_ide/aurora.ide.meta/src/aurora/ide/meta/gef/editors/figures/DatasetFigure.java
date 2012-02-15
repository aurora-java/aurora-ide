package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.Dataset;

/**
 */
public class DatasetFigure extends Figure {

	private static Image bgImg = ImagesUtils.getImage("btn.gif");
	private static String[] buttonTypes = { Button.ADD, Button.SAVE,
			Button.DELETE, Button.CLEAR, Button.EXCEL };
	private static Image stdimg = ImagesUtils
			.getImage("aurora/toolbar_btn.gif");
	private static Image defaultimg = ImagesUtils
			.getImage("aurora/default.gif");

	private Dataset model = null;

	public DatasetFigure(Dataset model) {
		this.setModel(model);

		this.setSize(model.getSize());
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		g.pushState();

		Image bgImage = getBgImage();

		// Dimension iconSize = new Dimension(bgImage.getBounds().width,
		// bgImage.getBounds().height);
		Dimension textExtents = FigureUtilities.getTextExtents(model.getId(),
				getFont());

		Rectangle rect = getBounds();
		String text = model.getId();
		org.eclipse.swt.graphics.Rectangle b2 = bgImage.getBounds();
		Rectangle r1 = new Rectangle(b2.x, b2.y, b2.width, b2.height);

		g.setForegroundColor(ColorConstants.BLACK);

		g.drawImage(getBgImage(), r1.x, r1.y, r1.width, r1.height, rect.x,
				rect.y + 1, r1.width, r1.height);
		g.drawString(text, rect.x + r1.width, rect.y
				+ (rect.height - textExtents.height) / 2);
		g.popState();
	}

	public Dimension getPreferredSize(int wHint, int hHint) {
		Image bgImage = getBgImage();
		Dimension iconSize = new Dimension(bgImage.getBounds().width,
				bgImage.getBounds().height);
		Dimension textExtents = FigureUtilities.getTextExtents(model.getId(),
				getFont());
		Dimension dimension = new Dimension(iconSize.width + textExtents.width,
				Math.max(iconSize.height, textExtents.height));
		this.setSize(dimension);
		return dimension;

	}

	private Rectangle getStdImgRect() {
		return new Rectangle(0, 17, 16, 17);
	}

	private Image getBgImage() {
		return ImagesUtils.getImage("bm.gif");
	}

	public void setModel(Dataset model) {
		this.model = model;
	}

}
