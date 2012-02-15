package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

import aurora.ide.meta.gef.editors.parts.GraphAnimation;

/**

 */
public class DummyLayout extends AbstractLayout {

	/**
	 * @see org.eclipse.draw2d.AbstractLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure,
	 *      int, int)
	 */
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		return null;
	}

	/**
	 * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
	 */
	public void layout(IFigure container) {
		if(GraphAnimation.playbackState(container)){
			return;
		}
	}

}
