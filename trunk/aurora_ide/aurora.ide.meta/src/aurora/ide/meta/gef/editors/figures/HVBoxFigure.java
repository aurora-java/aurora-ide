package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;

import aurora.ide.meta.MetaPlugin;
import aurora.plugin.source.gen.screen.model.BOX;

public class HVBoxFigure extends BoxFigure {

	private BOX box;

	public HVBoxFigure() {
		this.setLayoutManager(new DummyLayout());
		this.addMouseMotionListener(new MouseMotionListener() {

			public void mouseMoved(MouseEvent me) {
			}

			public void mouseHover(MouseEvent me) {
			}

			public void mouseExited(MouseEvent me) {
				setBorder(null);
			}

			public void mouseEntered(MouseEvent me) {
				if(MetaPlugin.isDemonstrate ) return;
				setBorder(new VirtualBoxBorder(box.getComponentType()));
			}

			public void mouseDragged(MouseEvent me) {
			}
		});
	}

	public int getLabelWidth() {
		return box.getLabelWidth();
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

	public void setBox(BOX model) {
		this.box = model;
	}

	public BOX getBox() {
		return box;
	}

}
