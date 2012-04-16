package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class FigureUtil {
	public static void paintImageAtCenter(Graphics g, Rectangle r, Image i) {
		if (r == null || i == null)
			return;
		Rectangle b = new Rectangle(i.getBounds());
		int dx = b.width >> 1, dy = b.height >> 1;
		g.drawImage(i, r.getCenter().translate(-dx, -dy));
	}

	public static void paintTextAtCenter(Graphics g, Rectangle r, String s) {
		paintTextAtCenter(g, r, s, false);
	}

	public static void paintTextAtCenter(Graphics g, Rectangle r, String s,
			boolean u) {
		if (r == null || s == null)
			return;
		Dimension d = FigureUtilities.getStringExtents(s, g.getFont());
		int dx = d.width >> 1, dy = d.height >> 1;
		g.drawString(s, r.getCenter().translate(-dx, -dy));
		if (u) {
			Point p1 = r.getCenter().translate(-dx, dy);
			Point p2 = r.getCenter().translate(dx, dy);
			g.drawLine(p1, p2);
		}
	}
}
