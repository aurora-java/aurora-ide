package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class BackLayout {
//	protected static final Insets PADDING = new Insets(8, 16, 8, 6);//8,6,8,6

	public Rectangle layout(ComponentPart ep) {
		Rectangle bounds = ep.getComponent().getBoundsCopy();
		return bounds;
	}

	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		ep.getFigure().setBounds(layout);
	}
	protected void applyToModel(ComponentPart ep, Rectangle layout) {
		ep.getComponent().applyToModel(layout);
	}
}
