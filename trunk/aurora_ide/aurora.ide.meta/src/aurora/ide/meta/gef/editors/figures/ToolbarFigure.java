package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;

/**
 */
public class ToolbarFigure extends Figure {

	public ToolbarFigure() {
		this.setLayoutManager(new DummyLayout());
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Rectangle rec = this.getBounds().getCopy();
		graphics.clipRect(rec);
		Image i = getBGImage();
		Rectangle src = new Rectangle(i.getBounds().x, i.getBounds().y,
				i.getBounds().width, i.getBounds().height);
		graphics.drawImage(i, src, rec);
		Rectangle r = rec.getResized(-1, -1);
		graphics.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		graphics.drawRectangle(r);
	}

	private Image getBGImage() {
		return ImagesUtils.getImage("toolbar_bg");
	}
}
