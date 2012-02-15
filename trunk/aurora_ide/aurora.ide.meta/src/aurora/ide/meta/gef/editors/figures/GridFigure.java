package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;

import aurora.ide.meta.gef.editors.models.Container;

/**
 * 
 */
public class GridFigure extends Figure {

	private int labelWidth;
	private Container grid;

	public GridFigure() {
		this.setLayoutManager(new DummyLayout());
		this.setBorder(new GridBorder());
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
	}

	public void setModel(Container component) {
		this.grid = component;

	}

}
