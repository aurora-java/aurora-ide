package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.plugin.source.gen.screen.model.BOX;

public class QueryFormBodyPart extends BoxPart {

	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		figure.setBox((BOX) getModel());
		figure.setBorder(new AbstractBorder() {

			public Insets getInsets(IFigure figure) {
				return new Insets();
			}

			public void paint(IFigure figure, Graphics g, Insets insets) {
				Rectangle rect = figure.getBounds();
				g.clipRect(rect);
				Rectangle r = rect.getResized(-1, -1);
				g.setForegroundColor(ColorConstants.EDITOR_BORDER);
				g.setLineStyle(Graphics.LINE_DOT);
				g.drawRoundRectangle(r, 6, 6);
			};
		});
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	@Override
	public boolean isLayoutHorizontal() {
		return ((BOX) getModel()).getCol() > 1;
	}

	public Rectangle layout() {
		return super.layout();
	}

	@Override
	public int getResizeDirection() {
		return NSEW;
	}
}
