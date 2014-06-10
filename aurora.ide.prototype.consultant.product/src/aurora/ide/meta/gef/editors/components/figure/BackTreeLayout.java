package aurora.ide.meta.gef.editors.components.figure;

import java.util.List;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.components.part.CustomTreeNodePart;
import aurora.ide.meta.gef.editors.layout.BackLayout;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ContainerPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

public class BackTreeLayout extends BackLayout {

	// public Object getConstraint(IFigure child) {
	// // TODO Auto-generated method stub
	// return super.getConstraint(child);
	// }

	private static final Insets TREE_PADDING = new Insets(24, 18, 0, 0);

	private static final int D_WIDTH = 18 + 18 + 40, D_HIGHT = 24;

	public void setConstraint(IFigure child, Object constraint) {
		// super.setConstraint(child, constraint);
		IFigure parent = child.getParent();
		// for delete if child delete parent is null.
		if (parent == null)
			parent = (IFigure) constraint;
		Dimension newSize = new Dimension();
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure node = (IFigure) children.get(i);
			Dimension nodeSize = node.getSize();
			newSize.width = newSize.width >= nodeSize.width
					+ TreeLayoutManager.X_STEP ? newSize.width : nodeSize.width
					+ TreeLayoutManager.X_STEP;
			newSize.height += nodeSize.height;
		}
		IFigure god = parent.getParent();
		newSize.height += TreeLayoutManager.NODE_DEFUAULT_HIGHT;
		// for delete has no child
		if (newSize.width == 0)
			newSize.width = TreeLayoutManager.NODE_DEFUAULT_WIDTH;
		parent.setSize(newSize);
		// for expand resize re-layout
		// Deprecated cause delete.
		// if (TreeExpandSupportEditPolicy.SIZE_CHANGED.equals(constraint))
		// layout(parent);
		// for delete if child delete parent is null.
		if (constraint instanceof IFigure)
			layout(parent);
		god.getLayoutManager().setConstraint(parent, constraint);
	}

	protected Dimension calculatePreferredSize(ComponentPart treeContainer) {
		Dimension rc = new Dimension();
		Dimension textExtents = FigureUtilities.getTextExtents(treeContainer
				.getComponent().getPrompt(), treeContainer.getFigure()
				.getFont());
		return rc.setSize(D_WIDTH + textExtents.width, D_HIGHT);
	}

	private Rectangle getBounds(ComponentPart cp) {
		return this.toDraw2d(cp.getComponent().getBoundsCopy());
	}

	public Rectangle layout(ComponentPart treeContainer) {

		AuroraComponent component = treeContainer.getComponent();
		boolean isExpand = false;
		if (component instanceof CustomTreeContainerNode) {
			isExpand = ((CustomTreeContainerNode) component).isExpand();
		}
		List children = treeContainer.getChildren();
		if (treeContainer instanceof CustomTreeNodePart || children == null
				|| children.size() == 0 || isExpand == false) {
			Rectangle rc = new Rectangle();
			return rc.setSize(calculatePreferredSize(treeContainer));
		}

		Point location = treeContainer.getFigure().getBounds().getLocation();
		Point cl = location.setX(location.x + TREE_PADDING.left);

		Dimension size = calculatePreferredSize(treeContainer);

		for (Object object : children) {
			if (object instanceof ComponentPart) {
				Rectangle layout = ((ComponentPart) object).layout();
				cl.setY(cl.y + TREE_PADDING.top);
				layout.setLocation(cl);
				this.applyToFigure((ComponentPart) object, layout);
				size.setWidth(Math.max(size.width, layout.width
						+ TREE_PADDING.left));
				size.expand(0, TREE_PADDING.top);
			}
		}
		return getBounds(treeContainer).setSize(size);
	}

	private Rectangle calculateRectangle(ComponentPart parent) {

		Dimension newSize = new Dimension();
		List children = parent.getFigure().getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure node = (IFigure) children.get(i);
			Dimension nodeSize = node.getSize();
			newSize.width = newSize.width >= nodeSize.width
					+ TreeLayoutManager.X_STEP ? newSize.width : nodeSize.width
					+ TreeLayoutManager.X_STEP;
			newSize.height += nodeSize.height;
		}
		newSize.height += TreeLayoutManager.NODE_DEFUAULT_HIGHT;
		// for delete has no child
		if (newSize.width == 0)
			newSize.width = TreeLayoutManager.NODE_DEFUAULT_WIDTH;

		return new Rectangle().setSize(newSize);
	}

	@SuppressWarnings("unchecked")
	public void layout(IFigure container) {
		List<IFigure> trees = container.getChildren();
		IFigure temp = null;
		for (IFigure treeNode : trees) {
			Rectangle bounds = treeNode.getBounds();
			Point location = TreeLayoutManager.NODE_RELATIVE_LOCATION.getCopy();
			Dimension size = bounds.getSize();
			if (temp != null) {
				Rectangle lastBounds = temp.getBounds();
				location = lastBounds.getBottomLeft().getCopy();
				location = new Point(location.x, location.y);

			} else {
				location.translate(container.getBounds().getLocation());
			}
			if (size.equals(0, 0))
				treeNode.setSize(TreeLayoutManager.NODE_DEFUAULT_SIZE);
			treeNode.getBounds().setLocation(location);
			temp = treeNode;
			LayoutManager layoutManager = treeNode.getLayoutManager();
			if (layoutManager != null)
				layoutManager.layout(treeNode);
		}
		container.repaint();
	}

	// un use
	private int getTreeLevel(IFigure temp) {
		List children = temp.getChildren();
		int level = 0;
		int childLevel = 0;
		for (int i = 0; i < children.size(); i++) {
			level = 1;
			IFigure child = (IFigure) children.get(i);
			int treeLevel = getTreeLevel(child);
			childLevel = childLevel >= treeLevel ? childLevel : treeLevel;
		}

		return level + childLevel;
	}

}
