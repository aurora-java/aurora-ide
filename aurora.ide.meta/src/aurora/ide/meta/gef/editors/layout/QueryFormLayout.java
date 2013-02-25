package aurora.ide.meta.gef.editors.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.QueryFormPart;

public class QueryFormLayout extends BackLayout {
	private static final int GAP = 5;

	@Override
	public Rectangle layout(ComponentPart ep) {
		QueryFormPart part = (QueryFormPart) ep;
		Rectangle rect = super.layout(ep);
		int maxHeight = 0;
		@SuppressWarnings("unchecked")
		List<ComponentPart> list = part.getChildren();
		for (int i = 0; i < list.size(); i++) {
			ComponentPart cp = list.get(i);
			Rectangle r = new Rectangle();
			r.x = rect.x;
			r.y = rect.y + maxHeight;
			r.width = rect.width;
			r.height = 40;
			cp.getFigure().setBounds(r);
			applyToModel(cp, r);
			r = cp.layout();
			cp.getFigure().setSize(new Dimension(rect.width, r.height));
			maxHeight += r.height + GAP;
		}
		rect.height = maxHeight;
		if (list.size() > 0)
			rect.height -= GAP;
		return rect;
	}

	@Override
	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		super.applyToFigure(ep, layout);
	}

}
