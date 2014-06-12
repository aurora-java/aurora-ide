package aurora.ide.meta.gef.editors.components.figure;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import aurora.ide.meta.gef.editors.PrototypeImagesUtils;
import aurora.ide.meta.gef.editors.figures.ColorConstants;
import aurora.ide.meta.gef.editors.figures.ResourceDisposer;
import aurora.ide.meta.gef.util.TextStyleUtil;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.Container;
import aurora.plugin.source.gen.screen.model.CustomTree;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;
import aurora.plugin.source.gen.screen.model.StyledStringText;
import aurora.plugin.source.gen.screen.model.properties.ComponentInnerProperties;
import aurora.plugin.source.gen.screen.model.properties.ComponentProperties;

public class TreeNodeFigure extends Figure {
	private String text;

	// private boolean isFolder;
	// private boolean isExpand;

	private AuroraComponent treeNode;

	private CustomTree tree;

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
		// this.treeNode = treeNode;
		this.setText(getTreeNode().getPrompt());
		this.repaint();
	}

	private void setText(String name) {
		this.text = name;
	}

	private int getNodeLocation() {

		if (getTreeNode() != null && getTreeNode().getParent() != null) {
			Container parent = getTreeNode().getParent();
			List<AuroraComponent> children = parent.getChildren();
			int index = children.indexOf(this.getTreeNode());

			// if (parent instanceof CustomTree && index == 0) {
			// return ;
			// }

			int size = children.size();
			if (size - index == 1) {
				if (parent instanceof CustomTree && index == 0)
					return 100;
				return 2;
			}
			if (index == 0) {
				return 0;
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

		Container parent = this.treeNode.getParent();
		if (nl == 0 && !(parent instanceof CustomTree)) {
			nl = 1;
		}
		boolean line = nl != 2 && nl != 100;
		if (nl == 100) {
			nl = 0;
		}
		if (isFolder()) {
			if (isExpand()) {
				x = drawImage(graphics, IMG_MINUS_TOP + nl, x, area.y, 18);
				if (line)
					drawLine(graphics);
			} else {
				x = drawImage(graphics, IMG_PLUS_TOP + nl, x, area.y, 18);
			}
			x = drawImage(graphics, IMG_FOLDER, x, area.y + 2, 18);
		} else {
			x = drawImage(graphics, IMG_JOIN_TOP + nl, x, area.y, 18);
			x = drawImage(graphics, IMG_LEAF, x, area.y + 2, 18);
		}
		if (isCheckTree()) {
			x = drawImage(graphics, IMG_CHECK, x, area.y, 18);
		}

		Rectangle clientArea = this.getClientArea();

		if (TextStyleUtil.isTextLayoutUseless(this.treeNode,
				ComponentProperties.prompt) == false) {
			paintStyledText(graphics, this.getText(),
					ComponentProperties.prompt, x, clientArea.y + 4);
		} else {
			graphics.drawText(getText(), x, clientArea.y + 4);
		}

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
			for (int i = 24; i < area.height; i += 24) {
				graphics.drawImage(
						PrototypeImagesUtils.getImage("tree/line.gif"), area.x,
						area.y + i);
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
		return getTree().isCheckedTree();
	}

	public boolean isExpand() {
		return treeNode
				.getBooleanPropertyValue(CustomTreeContainerNode.CONTAINER_EXPAND);
	}

	// public void setExpand(boolean isExpand) {
	// this.isExpand = isExpand;
	// }

	private CustomTree getTree() {
		if (this.tree == null) {
			return this.tree = getTree(treeNode);
		}
		return tree;
	}

	private CustomTree getTree(AuroraComponent treeNode) {
		Container parent = treeNode.getParent();
		if (parent instanceof CustomTree) {
			return (CustomTree) parent;
		} else
			return getTree(parent);
	}

	public AuroraComponent getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(AuroraComponent treeNode) {
		this.treeNode = treeNode;
	}

	protected void paintStyledText(Graphics g, String text, String property_id,
			int x, int y) {
		g.pushState();
		this.disposer.disposeResource(property_id);
		g.setForegroundColor(ColorConstants.BLACK);
		TextLayout tl = new TextLayout(null);
		tl.setText(text);
		tl.setFont(getFont());
		Object obj = this.treeNode.getPropertyValue(property_id
				+ ComponentInnerProperties.TEXT_STYLE);
		TextStyle ts = null;
		if (obj instanceof StyledStringText) {
			ts = TextStyleUtil.createTextStyle((StyledStringText) obj,
					Display.getDefault(), getFont());
		} else {
			ts = new TextStyle();
		}
		tl.setStyle(ts, 0, text.length() - 1);
		if (ComponentProperties.prompt.equals(property_id))
			g.drawTextLayout(tl, x, y);
		this.disposer.handleResource(property_id, tl);
		g.popState();
	}

	private ResourceDisposer disposer = new ResourceDisposer();

	public void disposeResource() {
		disposer.disposeResource();
		disposer = null;
	}

	protected void handleResource(String id, Resource r) {
		disposer.handleResource(id, r);
	}

	protected void disposeResource(String prop_id) {
		disposer.disposeResource(prop_id);
	}
}
