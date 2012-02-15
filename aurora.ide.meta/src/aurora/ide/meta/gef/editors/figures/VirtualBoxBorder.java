package aurora.ide.meta.gef.editors.figures;

import aurora.ide.meta.gef.editors.ImagesUtils;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class VirtualBoxBorder extends AbstractLabeledBorder {
	static Image vImg = ImagesUtils.getImage("palette/vbox.png");
	static Image hImg = ImagesUtils.getImage("palette/hbox.png");
	String type = null;

	public VirtualBoxBorder(String type) {
		this.type = type;
		if (this.type == null)
			throw new NullPointerException("Border type can not be null.");
	}

	public void paint(IFigure figure, Graphics g, Insets insets) {
		g.pushState();
		Rectangle rect = figure.getBounds();
		g.clipRect(rect);
		Rectangle r = rect.getResized(-1, -1);
		g.setForegroundColor(ColorConstants.VIRTUAL_BORDER);
		g.setLineStyle(Graphics.LINE_DOT);
		g.drawRectangle(r);
		Image img = "H".equals(type) ? hImg : vImg;
		if (img != null) {
			g.drawImage(img, r.x, r.y);
		} else {
			g.drawText(type, r.x + 2, r.y);
		}
		g.popState();
	}

	@Override
	protected Insets calculateInsets(IFigure figure) {
		return new Insets(0, 0, 0, 0);
	}
}
