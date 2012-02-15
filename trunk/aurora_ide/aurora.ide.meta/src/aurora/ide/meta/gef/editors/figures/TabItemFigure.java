package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;
import aurora.ide.meta.gef.editors.models.TabItem;

public class TabItemFigure extends Figure {
	private static Image bgImg = ImagesUtils.getImage("toolbar_bg");
	private TabItem model;

	public TabItemFigure() {
	}

	public void setModel(TabItem model) {
		this.model = model;
	}

	public TabItem getModel() {
		return model;
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		if (!model.isCurrent()) {
			g.drawImage(bgImg, new Rectangle(bgImg.getBounds()), getBounds());
		}
		String prompt = model.getPrompt();
		Rectangle bounds = getBounds().getCopy();
		Point center = bounds.getCenter();
		Dimension textExtents = FigureUtilities.getTextExtents(prompt,
				getFont());
		g.setForegroundColor(ColorConstants.BLACK);
		g.drawText(prompt, center.translate(-textExtents.width / 2,
				-textExtents.height / 2));
		g.setForegroundColor(ColorConstants.FIELDSET_BORDER);
		g.drawRoundRectangle(getBounds().getResized(-1, 1), 6, 6);
	}
}
