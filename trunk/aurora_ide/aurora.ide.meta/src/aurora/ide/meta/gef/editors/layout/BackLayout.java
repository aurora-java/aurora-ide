package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class BackLayout {
	// protected static final Insets PADDING = new Insets(8, 16, 8, 6);//8,6,8,6
	protected Rectangle toDraw2d(aurora.plugin.source.gen.screen.model.Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}

	protected	aurora.plugin.source.gen.screen.model.Rectangle toAurora(Rectangle rect) {
		return new aurora.plugin.source.gen.screen.model.Rectangle(rect.x,
				rect.y, rect.width, rect.height);
	}

	public Rectangle layout(ComponentPart ep) {
		Rectangle bounds = toDraw2d(ep.getComponent().getBoundsCopy());
		return bounds;
	}

	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		ep.getFigure().setBounds(
				new org.eclipse.draw2d.geometry.Rectangle(layout.x, layout.y,
						layout.width, layout.height));
	}

	protected void applyToModel(ComponentPart ep, Rectangle layout) {
		ep.getComponent().applyToModel(toAurora(layout));
	}
}
