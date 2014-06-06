package aurora.ide.meta.gef.editors.components.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.plugin.source.gen.screen.model.AuroraComponent;

public class TreeNodeFigure extends ImageFigure {
	private String text;

	public TreeNodeFigure(Image image) {
		super(image, PositionConstants.NORTH_WEST);
	}

	public void refreshVisuals(AuroraComponent treeNode) {
		this.setText(treeNode.getName());
		this.repaint();
	}

	private void setText(String name) {
		this.text = name;
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		Rectangle clientArea = this.getClientArea();
		graphics.drawText(getText(), clientArea.x + 15, clientArea.y);
	}

	private String getText() {
		return "" + text;
	}
}
