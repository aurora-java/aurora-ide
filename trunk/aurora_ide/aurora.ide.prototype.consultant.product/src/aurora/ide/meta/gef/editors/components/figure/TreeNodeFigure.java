package aurora.ide.meta.gef.editors.components.figure;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;
import aurora.plugin.source.gen.screen.model.CustomTreeNode;

public class TreeNodeFigure extends Figure {
	private String text;

	// private boolean isFolder;
	private boolean isExpand;

	private AuroraComponent treeNode;

	static final private int IMG_FOLDER = 0, IMG_LEAF = 1, IMG_CHECK = 3;

	static final private int IMG_MINUS_TOP = 4, IMG_MINUS = 5,
			IMG_MINUS_BOTTOM = 6;

	static final private int IMG_PLUS_TOP = 7, IMG_PLUS = 8,
			IMG_PLUS_BOTTOM = 9;

	static final private int IMG_JOIN_TOP = 10, IMG_JOIN = 11,
			IMG_JOIN_BOTTOM = 12;


	public TreeNodeFigure(AuroraComponent treeNode) {
		this.setTreeNode(treeNode);
	}

	public void refreshVisuals() {
//		this.treeNode = treeNode;
		this.setText(getTreeNode().getName());
		this.repaint();
	}

	private void setText(String name) {
		this.text = name;
	}

	private int getNodeLocation() {

		if (getTreeNode() != null && getTreeNode().getParent() != null) {
			List<AuroraComponent> children = getTreeNode().getParent().getChildren();
			int index = children.indexOf(this.getTreeNode());
			if (index == 0) {
				return 0;
			}
			int size = children.size();
			if (size - index == 1) {
				return 2;
			}
			return 1;
		}
		return 0;
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		Rectangle area = getBounds().getShrinked(getInsets());
		// 18 * 24
		int x = area.x;
		int nl = getNodeLocation();
		if (isFolder()) {
			if (isExpand()) {
				x = drawImage(graphics, IMG_MINUS_TOP + nl, x, area.y, 18);
				drawLine(graphics);
			} else {
				x = drawImage(graphics, IMG_PLUS_TOP + nl, x, area.y, 18);
			}
			x = drawImage(graphics, IMG_FOLDER, x, area.y, 18);
		} else {
			x = drawImage(graphics, IMG_JOIN_TOP + (nl == 0 ? 1 : nl), x,
					area.y, 18);
			x = drawImage(graphics, IMG_LEAF, x, area.y, 18);
		}
		if (isCheckTree()) {
			x = drawImage(graphics, IMG_CHECK, x, area.y - 3, 18);
		}

		Rectangle clientArea = this.getClientArea();
		graphics.drawText(getText(), x, clientArea.y);
	}

	private int drawImage(Graphics graphics, int img, int x, int y,
			int img_width) {
		Image image = getImage(img);
		if (image != null) {
			graphics.drawImage(image, x, y);
			x += img_width;
		}
		return x;
	}

	private boolean isFolder() {
		return this.getTreeNode() instanceof CustomTreeContainerNode;
	}

	private void drawLine(Graphics graphics) {
		Rectangle area = getBounds().getShrinked(getInsets());
		if (area.getSize().height > 24) {
			int size = ((CustomTreeContainerNode) getTreeNode()).getChildren()
					.size();
			int y = area.y;
			for (int i = 0; i < size; i++) {
				y += 24;
				graphics.drawImage(
						PrototypeImagesUtils.getImage("tree/line.gif"), area.x,
						y);
			}
		}

	}

	private Image getImage(int type) {
		switch (type) {

		case IMG_FOLDER:
			return PrototypeImagesUtils.getImage("tree/folder.gif");
		case IMG_LEAF:
			return PrototypeImagesUtils.getImage("tree/leaf.gif");
		case IMG_CHECK:
			return PrototypeImagesUtils.getImage("tree/checkbox_0.gif");
		case IMG_MINUS:
			return PrototypeImagesUtils.getImage("tree/minus.gif");
		case IMG_MINUS_TOP:
			return PrototypeImagesUtils.getImage("tree/minustop.gif");
		case IMG_MINUS_BOTTOM:
			return PrototypeImagesUtils.getImage("tree/minusbottom.gif");
		case IMG_PLUS:
			return PrototypeImagesUtils.getImage("tree/plus.gif");
		case IMG_PLUS_TOP:
			return PrototypeImagesUtils.getImage("tree/plustop.gif");
		case IMG_PLUS_BOTTOM:
			return PrototypeImagesUtils.getImage("tree/plusbottom.gif");
		case IMG_JOIN:
			return PrototypeImagesUtils.getImage("tree/join.gif");
		case IMG_JOIN_TOP:
			return PrototypeImagesUtils.getImage("tree/jointop.gif");
		case IMG_JOIN_BOTTOM:
			return PrototypeImagesUtils.getImage("tree/joinbottom.gif");
		}
		return null;
	}

	private String getText() {
		return "" + text;
	}

	private boolean isCheckTree() {
		return SWT.CHECK == getTreeType();
	}

	private int getTreeType() {
		// SWT.NONE
		return SWT.CHECK;
	}

	public boolean isExpand() {
		return isExpand;
	}

	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	public AuroraComponent getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(AuroraComponent treeNode) {
		this.treeNode = treeNode;
	}
}
