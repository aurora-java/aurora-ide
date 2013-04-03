package aurora.ide.meta.gef.util;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class BoundsConvert {
	public static Dimension getSize(AuroraComponent component) {
		aurora.plugin.source.gen.screen.model.Point size = component.getSize();
		return new Dimension(size.x, size.y);
	}
	public static Rectangle toDraw2d(aurora.plugin.source.gen.screen.model.Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}

	public static aurora.plugin.source.gen.screen.model.Rectangle toAurora(Rectangle rect) {
		return new aurora.plugin.source.gen.screen.model.Rectangle(rect.x,
				rect.y, rect.width, rect.height);
	}
	public static Point toDraw2d(aurora.plugin.source.gen.screen.model.Point loc) {
		return new Point(loc.x, loc.y);
	}

	public static aurora.plugin.source.gen.screen.model.Point toAurora(Point loc) {
		return new aurora.plugin.source.gen.screen.model.Point(loc.x,
				loc.y);
	}
}
