package aurora.ide.meta.gef.editors.components.figure;

import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class TreeContainerLayoutManager extends AbstractLayout {

	@Override
	public Object getConstraint(IFigure child) {
		// TODO Auto-generated method stub
		return super.getConstraint(child);
	}

	private Dimension getNodeSize(IFigure node) {
		if (node instanceof TreeNodeFigure) {
			aurora.plugin.source.gen.screen.model.Point size = ((TreeNodeFigure) node)
					.getTreeNode().getSize();
			Dimension nodeSize = new Dimension(size.x, size.y);
			return nodeSize;
		}
		return node.getSize();
	}

	@Override
	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
		IFigure parent = child.getParent();
		// for delete if child delete parent is null.
		if (parent == null)
			parent = (IFigure) constraint;
		Dimension newSize = new Dimension();
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure node = (IFigure) children.get(i);
			if (node instanceof TreeNodeFigure) {
				Dimension nodeSize = getNodeSize(node);
				newSize.width = newSize.width >= nodeSize.width
						+ TreeLayoutManager.X_STEP ? newSize.width
						: nodeSize.width + TreeLayoutManager.X_STEP;
				newSize.height += nodeSize.height;
			}
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

	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		container.validate();
		Dimension result = getNodeSize(container);
		List children = container.getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure child = (IFigure) children.get(i);
			Dimension childSize = this.getNodeSize(child);
			// childSize = new Dimension(200,24);
			childSize.expand(child.getBounds().getLocation());
			result.union(childSize);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void layout(IFigure container) {
		List<IFigure> trees = container.getChildren();
		IFigure temp = null;
		for (IFigure treeNode : trees) {
			Rectangle bounds = treeNode.getBounds();
			Point location = TreeLayoutManager.NODE_RELATIVE_LOCATION.getCopy();
			Dimension size = this.getNodeSize(treeNode);
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
