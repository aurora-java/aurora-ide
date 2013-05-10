package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.plugin.source.gen.screen.model.Grid;
import aurora.plugin.source.gen.screen.model.Navbar;

/**
 */
public class NavbarFigure extends Figure {

	private String[] texts = { Messages.NavbarFigure_0,
			Messages.NavbarFigure_1, Messages.NavbarFigure_2,
			Messages.NavbarFigure_3, Messages.NavbarFigure_4 };
	private static String simpleText = Messages.NavbarFigure_5;
	private Navbar model;
	private static Image bgImg = PrototypeImagesUtils.getImage("toolbar_bg.gif"); //$NON-NLS-1$
	private static Image navImg = PrototypeImagesUtils.getImage("navigation.gif"); //$NON-NLS-1$
	private static Image sepImg = PrototypeImagesUtils.getImage("toolbar_sep.gif"); //$NON-NLS-1$
	private static Image combImg = PrototypeImagesUtils
			.getImage("palette/itembar_01.png"); //$NON-NLS-1$

	public NavbarFigure() {
		setBorder(null);
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);

	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics g) {
		String type = model.getNavBarType();
		if (type.equals(Grid.NAVBAR_NONE))
			return;
		super.paintFigure(g);
		if (type.equals(Grid.NAVBAR_COMPLEX)) {
			paintComplex(g);
		} else {
			paintSimple(g);
		}
	}

	private void paintSimple(Graphics g) {
		Rectangle bounds = getBounds().getCopy();
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.drawLine(bounds.getTopLeft(), bounds.getTopRight());
		Dimension dim = FigureUtilities.getTextExtents(simpleText, getFont());
		g.setForegroundColor(ColorConstants.BLACK);
		g.drawString(simpleText, bounds.x + bounds.width - dim.width - 3,
				bounds.y + (bounds.height - dim.height) / 2);
	}

	private void paintComplex(Graphics g) {
		Rectangle bounds = getBounds().getCopy();
		g.drawImage(bgImg, new Rectangle(bgImg.getBounds()), bounds);
		// |<
		Rectangle r1 = new Rectangle(0, 0, 16, 16);
		Rectangle r2 = new Rectangle(bounds.x + 4, bounds.y + 4, 16, 16);
		g.drawImage(navImg, r1, r2);
		// <
		r1.y = 32;
		r2.x += bounds.height;
		g.drawImage(navImg, r1, r2);
		// sep
		Rectangle r3 = new Rectangle(r2.x + bounds.height, bounds.y, 2,
				bounds.height);
		g.drawImage(sepImg, new Rectangle(sepImg.getBounds()), r3);
		g.setForegroundColor(ColorConstants.BLACK);
		Dimension dim = FigureUtilities.getTextExtents(texts[0], getFont());
		int nextX = r3.x + r3.width + 2;
		g.drawString(texts[0], nextX, bounds.y + (bounds.height - dim.height)
				/ 2);
		// rect
		nextX += dim.width + 2;
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.setBackgroundColor(ColorConstants.WHITE);
		Rectangle r4 = new Rectangle(nextX, bounds.y + 3, 30, bounds.height - 6);
		g.fillRectangle(r4);
		g.drawRectangle(r4);
		nextX += r4.width + 5;
		g.setForegroundColor(ColorConstants.BLACK);
		dim = FigureUtilities.getTextExtents(texts[1], getFont());
		g.drawString(texts[1], nextX, bounds.y + (bounds.height - dim.height)
				/ 2);
		// sep
		nextX += dim.width + 2;
		r3 = new Rectangle(nextX, bounds.y, 2, bounds.height);
		g.drawImage(sepImg, new Rectangle(sepImg.getBounds()), r3);
		// >
		r1.y = 48;
		r2.x = nextX + 4;
		g.drawImage(navImg, r1, r2);
		// >|
		r1.y = 16;
		r2.x += bounds.height;
		g.drawImage(navImg, r1, r2);
		// refresh
		r1.y = 64;
		r2.x += bounds.height;
		g.drawImage(navImg, r1, r2);
		// sep
		nextX = r2.x + bounds.height + 2;
		r3 = new Rectangle(nextX, bounds.y, 2, bounds.height);
		g.drawImage(sepImg, new Rectangle(sepImg.getBounds()), r3);
		nextX += 5;
		g.setForegroundColor(ColorConstants.BLACK);
		dim = FigureUtilities.getTextExtents(texts[2], getFont());
		g.drawString(texts[2], nextX, bounds.y + (bounds.height - dim.height)
				/ 2);
		// rect
		nextX += dim.width + 2;
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.setBackgroundColor(ColorConstants.WHITE);
		r4 = new Rectangle(nextX, bounds.y + 3, 46, bounds.height - 6);
		g.fillRectangle(r4);
		g.drawImage(combImg, new Rectangle(combImg.getBounds()), new Rectangle(
				r4.x + 29, r4.y + 1, 16, 16));
		g.drawRectangle(r4);
		nextX = r4.x + r4.width + 5;
		g.setForegroundColor(ColorConstants.BLACK);
		dim = FigureUtilities.getTextExtents(texts[3], getFont());
		g.drawString(texts[3], nextX, bounds.y + (bounds.height - dim.height)
				/ 2);
		// sep
		nextX += dim.width + 5;
		r3 = new Rectangle(nextX, bounds.y, 2, bounds.height);
		g.drawImage(sepImg, new Rectangle(sepImg.getBounds()), r3);
		// last..
		nextX += 3;
		dim = FigureUtilities.getTextExtents(texts[4], getFont());
		if (bounds.width - nextX < dim.width)
			return;
		g.drawString(texts[4], bounds.x + bounds.width - dim.width - 3,
				bounds.y + (bounds.height - dim.height) / 2);
		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.drawLine(bounds.getTopLeft(), bounds.getTopRight());
	}

	public void setModel(Navbar model) {
		this.model = model;
	}

	private Image getImage(String key) {
		return PrototypeImagesUtils.getImage(key);
	}

}
