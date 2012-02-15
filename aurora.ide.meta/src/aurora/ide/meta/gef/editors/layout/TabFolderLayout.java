package aurora.ide.meta.gef.editors.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.TabBodyPart;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;
import aurora.ide.meta.gef.editors.parts.TabItemPart;

public class TabFolderLayout extends BackLayout {
	private static final Insets PADDING = new Insets(8, 16, 8, 6);

	@Override
	public Rectangle layout(ComponentPart ep) {
		TabFolderPart part = (TabFolderPart) ep;
		Point pos = part.getFigure().getBounds().getTopLeft().translate(2, 2);
		Point maxSize = new Point(0, 0);
		@SuppressWarnings("unchecked")
		List<ComponentPart> list = part.getChildren();
		int tabItemTotalWidth = 0;
		for (ComponentPart cp : list) {
			if (cp instanceof TabItemPart) {
				TabItemPart tip = (TabItemPart) cp;
				Rectangle bounds = tip.getModel().getBounds();
				bounds.setLocation(pos);
				pos.x += bounds.width + 2;
				tabItemTotalWidth = pos.x;
				tip.getFigure().setBounds(bounds);
			} else if (cp instanceof TabBodyPart) {
				Rectangle rect = part.getFigure().getBounds().getCopy();
				rect.y += TabItem.HEIGHT + 2;
				rect.height -= TabItem.HEIGHT + 2;
				cp.getFigure().setBounds(rect);
				// calc children size
				cp.getFigure().isVisible();
				int x = PADDING.left, y = PADDING.top;
				for (Object obj : cp.getChildren()) {
					ComponentPart object = (ComponentPart) obj;
					Dimension size = object.getFigure().getSize();
					x = Math.max(x, size.width + PADDING.left);
					y += size.height + PADDING.top;
				}
				maxSize.x = Math.max(maxSize.x, x);
				maxSize.y = Math.max(maxSize.y, y);
				Dimension d = cp.getFigure().getSize();
				if (d.width < x || d.height < y) {
					cp.getFigure().setSize(Math.max(d.width, x),
							Math.max(d.height, y - 2));
				}

			}
		}
		maxSize.y += TabItem.HEIGHT;
		Rectangle rect = super.layout(ep);
		rect.width = Math.max(maxSize.x,
				Math.max(rect.width, tabItemTotalWidth));
		rect.height = Math.max(maxSize.y, rect.height);
		return rect;
	}

	@Override
	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		super.applyToFigure(ep, layout);
	}

}
