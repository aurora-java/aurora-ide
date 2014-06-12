package aurora.ide.meta.gef.editors.components.figure;

import java.util.List;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.components.part.CustomTreeNodePart;
import aurora.ide.meta.gef.editors.layout.BackLayout;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.plugin.source.gen.screen.model.AuroraComponent;
import aurora.plugin.source.gen.screen.model.CustomTreeContainerNode;

public class BackTreeLayout extends BackLayout {

	// public Object getConstraint(IFigure child) {
	// // TODO Auto-generated method stub
	// return super.getConstraint(child);
	// }

	private static final int D_WIDTH = 18 + 18 + 40, D_HIGHT = 20;
	
	private static final Insets TREE_PADDING = new Insets(D_HIGHT, 18, 0, 0);



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
			hideChildren(treeContainer);
			Rectangle rc = new Rectangle();
			return rc.setSize(calculatePreferredSize(treeContainer));
		}

		Point location = treeContainer.getFigure().getBounds().getLocation();
		Point cl = location.setX(location.x + TREE_PADDING.left);

		Dimension size = calculatePreferredSize(treeContainer);
		cl.setY(cl.y + TREE_PADDING.top);
		for (Object object : children) {
			if (object instanceof ComponentPart) {
				Rectangle layout = ((ComponentPart) object).layout();
				layout.setLocation(cl);
				this.applyToFigure((ComponentPart) object, layout);
				size.setWidth(Math.max(size.width, layout.width
						+ TREE_PADDING.left));
				cl.setY(cl.y + layout.height);
				size.expand(0, layout.height);
			}
		}
		return getBounds(treeContainer).setSize(size);
	}



	private void hideChildren(ComponentPart treeContainer) {
		List children = treeContainer.getChildren();
		if(children !=null){
			for (Object object : children) {
				if (object instanceof ComponentPart) {
					this.applyToFigure((ComponentPart) object, new Rectangle());
				}
			}
		}
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
