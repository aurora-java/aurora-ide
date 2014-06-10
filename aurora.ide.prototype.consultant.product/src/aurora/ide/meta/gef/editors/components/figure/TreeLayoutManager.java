/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package aurora.ide.meta.gef.editors.components.figure;

import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class TreeLayoutManager extends AbstractLayout {
	public static final int NODE_DEFUAULT_WIDTH = 150;
	public static final int NODE_DEFUAULT_HIGHT = 24;
	public static final int X_STEP = 15;
	public static final int Y_STEP = NODE_DEFUAULT_HIGHT;
	public static final Point NODE_RELATIVE_LOCATION = new Point(X_STEP, Y_STEP);
	public static final Dimension NODE_DEFUAULT_SIZE = new Dimension(NODE_DEFUAULT_WIDTH, NODE_DEFUAULT_HIGHT);
	public static final Dimension TREE_DEFUAULT_SIZE = new Dimension(200, 180);
	public static final Dimension ZERO_SIZE = new Dimension(0, 0);

	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		Dimension result = TREE_DEFUAULT_SIZE.getCopy();
		List children = container.getChildren();
		for (int i = 0; i < children.size(); i++) {
			IFigure child = (IFigure) children.get(i);
			Dimension childSize = child.getPreferredSize();
			childSize.expand(child.getBounds().getLocation());
			result.union(childSize);
		}
		return result;
		// container.validate();
		// List children = container.getChildren();
		// Rectangle result = new Rectangle().setLocation(container
		// .getClientArea().getLocation());
		// for (int i = 0; i < children.size(); i++)
		// result.union(((IFigure) children.get(i)).getBounds());
		// result.resize(container.getInsets().getWidth(), container.getInsets()
		// .getHeight());
		// return result.getSize();
	}

	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
		IFigure parent = child.getParent();
//		Dimension size = parent.getSize();
		int childWidth = child.getSize().width;
		int childHeight = child.getSize().height;
		Dimension newSize = new Dimension(childWidth >= 200 ? childWidth+X_STEP*2 : 200,
				childHeight >= 180 ? childHeight+Y_STEP*2 : 200);
//		System.out.println(newSize);
		parent.setSize(newSize);
	}

	@SuppressWarnings("unchecked")
	public void layout(IFigure container) {
		List<IFigure> trees = container.getChildren();

		for (IFigure treeNode : trees) {
			Rectangle bounds = treeNode.getBounds();
			Point location = NODE_RELATIVE_LOCATION.getCopy();
			Dimension size = bounds.getSize();
			if (size.equals(0, 0))
				treeNode.setSize(NODE_DEFUAULT_SIZE);
			location.translate(container.getBounds().getLocation());
			treeNode.getBounds().setLocation(location);
		}
		container.repaint();
	}

}
