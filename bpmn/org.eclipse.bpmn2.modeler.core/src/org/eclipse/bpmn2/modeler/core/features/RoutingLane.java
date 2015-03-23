/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

// TODO: Auto-generated Javadoc
/**
 * This class represents a "routing lane" or blank space between shapes, which can
 * be used to lay connection lines. Routing Lanes are linked together in a network;
 * the linkage is created when two RoutingLane nodes share a common left or right
 * edge. See also RoutingNet.
 */
public class RoutingLane {
	
	/** The Constant EMPTY_LIST. */
	protected final static List<RoutingLane> EMPTY_LIST = new ArrayList<RoutingLane>();
	
	/** The rect. */
	protected Rectangle rect;
	
	/** The left adjacent. */
	protected List<RoutingLane> leftAdjacent;
	
	/** The right adjacent. */
	protected List<RoutingLane> rightAdjacent;
	
	/** The shape. */
	protected ContainerShape shape;

	/**
	 * The Enum Adjacence.
	 */
	public static enum Adjacence { /** The left. */
 LEFT, /** The top. */
 TOP, /** The bottom. */
 BOTTOM, /** The right. */
 RIGHT, /** The none. */
 NONE };
	
	/**
	 * Instantiates a new routing lane.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 */
	public RoutingLane(int x, int y, int width, int height) {
		this(new Rectangle(x,y,width,height));
	}
	
	private RoutingLane(Rectangle r) {
		rect = new Rectangle(r);
	}
	
	/**
	 * Adjacent.
	 *
	 * @param node the node
	 * @return the routing lane. adjacence
	 */
	public RoutingLane.Adjacence adjacent(RoutingLane node) {
		return adjacent(node.rect);
	}
	
	/**
	 * Adjacent.
	 *
	 * @param other the other
	 * @return the routing lane. adjacence
	 */
	public RoutingLane.Adjacence adjacent(Rectangle other) {
		if (rect.y==other.bottom()) {
			if (rect.right()<=other.x || other.right()<=rect.x)
				return Adjacence.NONE;
			return Adjacence.TOP;
		}
		if (rect.x==other.right()) {
			if (rect.bottom()<=other.y || other.bottom()<=rect.y)
				return Adjacence.NONE;
			return Adjacence.LEFT;
		}
		if (rect.right()==other.x) {
			if (rect.bottom()<=other.y || other.bottom()<=rect.y)
				return Adjacence.NONE;
			return Adjacence.RIGHT;
		}
		if (rect.bottom()==other.y) {
			if (rect.right()<=other.x || other.right()<=rect.x)
				return Adjacence.NONE;
			return Adjacence.BOTTOM;
		}
		return Adjacence.NONE;
	}
	
	/**
	 * Intersects.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean intersects(RoutingLane node) {
		return intersects(node.rect);
	}
	
	/**
	 * Intersects.
	 *
	 * @param rect the rect
	 * @return true, if successful
	 */
	public boolean intersects(Rectangle rect) {
		return GraphicsUtil.intersects(rect.x+1, rect.y+1, rect.width-2, rect.height-2,
				this.rect.x+1, this.rect.y+1, this.rect.width-2, this.rect.height-2);
	}
	
	/**
	 * Adds the left.
	 *
	 * @param node the node
	 */
	public void addLeft(RoutingLane node) {
		if (leftAdjacent==null)
			leftAdjacent = new ArrayList<RoutingLane>();
		if (!leftAdjacent.contains(node))
			leftAdjacent.add(node);
		if (node.rightAdjacent==null)
			node.rightAdjacent = new ArrayList<RoutingLane>();
		if (!node.rightAdjacent.contains(this))
			node.rightAdjacent.add(this);
	}
	
	/**
	 * Checks for left.
	 *
	 * @return true, if successful
	 */
	public boolean hasLeft() {
		return leftAdjacent!=null && leftAdjacent.size()>0;
	}
	
	/**
	 * Gets the left.
	 *
	 * @return the left
	 */
	public List<RoutingLane> getLeft() {
		if (hasLeft())
			return leftAdjacent;
		return EMPTY_LIST;
	}
	
	/**
	 * Adds the right.
	 *
	 * @param node the node
	 */
	public void addRight(RoutingLane node) {
		if (rightAdjacent==null)
			rightAdjacent = new ArrayList<RoutingLane>();
		if (!rightAdjacent.contains(node))
			rightAdjacent.add(node);
		if (node.leftAdjacent==null)
			node.leftAdjacent = new ArrayList<RoutingLane>();
		if (!node.leftAdjacent.contains(this))
			node.leftAdjacent.add(this);
	}
	
	/**
	 * Checks for right.
	 *
	 * @return true, if successful
	 */
	public boolean hasRight() {
		return rightAdjacent!=null && rightAdjacent.size()>0;
	}
	
	/**
	 * Gets the right.
	 *
	 * @return the right
	 */
	public List<RoutingLane> getRight() {
		if (hasRight())
			return rightAdjacent;
		return EMPTY_LIST;
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public int getX() {
		return rect.x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public int getY() {
		return rect.y;
	}

	/**
	 * Sets the x.
	 *
	 * @param i the new x
	 */
	public void setX(int i) {
		rect.x = i;
	}
	
	/**
	 * Sets the y.
	 *
	 * @param i the new y
	 */
	public void setY(int i) {
		rect.y = i;
	}
	
	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return rect.width;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return rect.height;
	}
	
	/**
	 * Sets the width.
	 *
	 * @param i the new width
	 */
	public void setWidth(int i) {
		rect.width = i;
	}

	/**
	 * Sets the height.
	 *
	 * @param i the new height
	 */
	public void setHeight(int i) {
		rect.height = i;
	}

	/**
	 * Sets the shape.
	 *
	 * @param shape the new shape
	 */
	public void setShape(PictogramElement shape) {
		this.shape = (ContainerShape)shape;
	}
	
	/**
	 * Gets the shape.
	 *
	 * @return the shape
	 */
	public ContainerShape getShape() {
		return shape;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		if (that instanceof RoutingLane)
			return this.rect.equals(((RoutingLane)that).rect);
		return false;
	}

	/**
	 * Rotate.
	 *
	 * @param b the b
	 */
	public void rotate(boolean b) {
		RoutingNet.rotateRectangle(rect);
	}

	/**
	 * Navigate to.
	 *
	 * @param ta the ta
	 * @param owner the owner
	 */
	public void navigateTo(RoutingLane ta, RoutingNet owner) {
		owner.push(this);
		if (this==ta) {
			owner.solutionFound();
		}
		else {
			for (RoutingLane a : getRight()) {
				if (!owner.visited(a))
					a.navigateTo(ta, owner);
			}
			for (RoutingLane a : getLeft()) {
				if (!owner.visited(a))
					a.navigateTo(ta, owner);
			}
		}
		owner.pop();
	}
}
