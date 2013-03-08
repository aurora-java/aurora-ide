package aurora.ide.meta.gef.editors.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
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
			Rectangle r = ((AuroraComponent) cp.getModel()).getBounds();
			applyToModel(cp, r.getCopy().setSize(rect.width, 40));
			r = cp.layout();
			r.x = rect.x;
			r.y = rect.y + maxHeight;
			r.width = rect.width;
			cp.getFigure().setBounds(r);
			maxHeight += r.height + GAP;
		}
		rect.height = maxHeight;
		if (list.size() > 1)
			rect.height -= GAP;
		return rect;
	}

	@Override
	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		super.applyToFigure(ep, layout);
	}

}
