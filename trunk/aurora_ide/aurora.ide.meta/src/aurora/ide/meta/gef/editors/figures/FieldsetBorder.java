package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class FieldsetBorder extends AbstractLabeledBorder {

	public FieldsetBorder(String type) {
		super(type);
	}

	public void paint(IFigure figure, Graphics g, Insets insets) {
		g.pushState();
		Rectangle rect = figure.getBounds();
		g.clipRect(rect);
		Rectangle r = rect.getResized(-1, -9).translate(0, 8);
		g.setForegroundColor(ColorConstants.FIELDSET_BORDER);
		g.drawRectangle(r);
		g.setForegroundColor(ColorConstants.BLACK);
		g.setBackgroundColor(ColorConstants.WHITE);
		g.fillText(getLabel(), r.x + 8, r.y - 8);
		g.popState();
	}

	@Override
	protected Insets calculateInsets(IFigure figure) {
		return new Insets(0, 0, 0, 0);
	}
}
