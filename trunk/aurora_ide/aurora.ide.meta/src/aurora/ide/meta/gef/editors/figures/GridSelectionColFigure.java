package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.plugin.source.gen.screen.model.Dataset;
import aurora.plugin.source.gen.screen.model.GridSelectionCol;
  
/**
 * 
 */
public class GridSelectionColFigure extends GridColumnFigure {

	private static final Image img_check = PrototypeImagesUtils
			.getImage("palette/checkbox_01.png");
	private static final Image img_radio = PrototypeImagesUtils
			.getImage("palette/radio_01.png");
	private static final Image img_border = PrototypeImagesUtils.getImage("grid_bg.gif");
	private GridSelectionCol model;

	public GridSelectionColFigure() {
		setLayoutManager(null);
		setBorder(null);
		setFocusTraversable(false);
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics g) {
		if (Dataset.SELECT_NONE.equals(model.getSelectionMode()))
			return;
		Rectangle rect = getBounds().getCopy();
		g.setBackgroundColor(ColorConstants.GRID_ROW);
		g.fillRectangle(rect);
		int ch = getColumnHight();
		Rectangle columnHeaderRect = new Rectangle(rect.x, rect.y, 25, ch);
		org.eclipse.swt.graphics.Rectangle imgBounds = img_border.getBounds();
		Rectangle imgRect = new Rectangle(imgBounds);
		imgRect.setHeight(Math.min(ch, imgRect.height));
		columnHeaderRect.height = Math.min(columnHeaderRect.height,
				imgRect.height);
		g.drawImage(img_border, imgRect, columnHeaderRect);

		Image img = img_radio;
		imgRect = new Rectangle(img.getBounds());
		if (Dataset.SELECT_MULTI.equals(model.getSelectionMode())) {
			img = img_check;
			imgRect = new Rectangle(img.getBounds());
			g.drawImage(img, rect.x + (rect.width - imgRect.width) / 2, rect.y
					+ (ch - imgRect.height) / 2);
		}

		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
		g.drawLine(rect.x, rect.y + ch - 1, rect.x + rect.width, rect.y + ch
				- 1);
		for (int i = rect.y + ch; i < rect.y + rect.height; i += 25) {
			Rectangle rc = new Rectangle(rect.x, i, rect.width, 25);
			if (i > (rect.y + ch)) {
				g.drawLine(rc.getTopLeft(), rc.getTopRight());
			}
			g.drawImage(img, imgRect, rc.getShrinked(
					(rc.width - imgRect.width) / 2,
					(rc.height - imgRect.height) / 2));
		}
		g.drawLine(rect.getTopRight().getTranslated(-1, 0), rect
				.getBottomRight().getTranslated(-1, 0));
	}

	public void setModel(GridSelectionCol component) {
		this.model = component;

	}

}
