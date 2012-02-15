package aurora.ide.meta.gef.editors.figures;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.GridSelectionCol;
import aurora.ide.meta.gef.editors.models.ResultDataSet;

import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class GridSelectionColFigure extends GridColumnFigure {

	private static final Image img_check = ImagesUtils
			.getImage("palette/checkbox_01.png");
	private static final Image img_radio = ImagesUtils
			.getImage("palette/radio_01.png");
	private static final Image img_border = ImagesUtils.getImage("grid_bg.gif");
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
		if (ResultDataSet.SELECT_NONE.equals(model.getSelectionMode()))
			return;
		Rectangle rect = getBounds().getCopy();
		g.setBackgroundColor(ColorConstants.GRID_ROW);
		g.fillRectangle(rect);
		int ch = getColumnHight();
		Rectangle columnHeaderRect = new Rectangle(rect.x, rect.y, 25, ch);
		Rectangle imgRect = new Rectangle(img_border.getBounds());
		g.drawImage(img_border, imgRect.setHeight(ch), columnHeaderRect);

		Image img = img_radio;
		imgRect = new Rectangle(img.getBounds());
		if (ResultDataSet.SELECT_MULTI.equals(model.getSelectionMode())) {
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
				// 第一条线不画
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
