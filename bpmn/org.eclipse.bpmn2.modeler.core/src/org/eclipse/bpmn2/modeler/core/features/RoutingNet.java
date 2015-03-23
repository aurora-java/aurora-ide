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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.eclipse.bpmn2.modeler.core.features.RoutingLane.Adjacence;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

// TODO: Auto-generated Javadoc
/**
 * This class manages a network of RoutingLane nodes. The nodes are linked if their
 * physical rectangles share an edge. Depending on the orientation in which the net was created,
 * only the left/right (for a "vertical" or non-rotated net) or top/bottom (a "horizontal"
 * or rotated net) edges are tested for adjacency.
 */
public class RoutingNet extends ArrayList<RoutingLane> {
	
	private static final long serialVersionUID = -3041403111796385182L;
	
	/** The Constant gaService. */
	protected static final IGaService gaService = Graphiti.getGaService();
	
	/** The Constant peService. */
	protected static final IPeService peService = Graphiti.getPeService();
	
	/** The Constant CONNECTION. */
	public static final String CONNECTION = "ROUTING_NET_CONNECTION"; //$NON-NLS-1$
	
	/** The Constant LANE. */
	public static final String LANE = "ROUTING_NET_LANE"; //$NON-NLS-1$
	
	/** The is rotated. */
	boolean isRotated = false;
	
	/** The source. */
	Shape source;
	
	/** The target. */
	Shape target;
	
	/** The source adjacent lanes. */
	List<RoutingLane> sourceAdjacentLanes = new ArrayList<RoutingLane>();
	
	/** The target adjacent lanes. */
	List<RoutingLane> targetAdjacentLanes = new ArrayList<RoutingLane>();
	
	/** The solution stack. */
	Stack<RoutingLane> solutionStack;
	
	/** The all solutions. */
	List< List<RoutingLane> > allSolutions;
	
	/** The min dist. */
	int minDist = Integer.MAX_VALUE;
	
	/** The fp. */
	IFeatureProvider fp;
	
	/**
	 * Instantiates a new routing net.
	 *
	 * @param fp the fp
	 */
	public RoutingNet(IFeatureProvider fp) {
		this.fp = fp;
	}

	/* (non-Javadoc)
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(RoutingLane a) {
		if (!contains(a) && a.getWidth()>0 && a.getHeight()>0)
			return super.add(a);
		return false;
	}

	/**
	 * Adds the.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @return true, if successful
	 */
	public boolean add(int x, int y, int width, int height) {
		RoutingLane a = new RoutingLane(x,y,width,height);
		return this.add(a);
	}
	
	/**
	 * Sets the feature provider.
	 *
	 * @param fp the new feature provider
	 */
	public void setFeatureProvider(IFeatureProvider fp) {
		this.fp = fp;
	}
	
	/**
	 * Link.
	 */
	public void link() {
		for (RoutingLane a1 : this) {
			for (RoutingLane a2 : this) {
				if (a1!=a2) {
					switch (a1.adjacent(a2)) {
					case LEFT:
					case TOP:
						a1.addLeft(a2);
						break;
					case RIGHT:
					case BOTTOM:
						a1.addRight(a2);
						break;
					case NONE:
						break;
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.ArrayList#clear()
	 */
	public void clear() {
		super.clear();
		sourceAdjacentLanes.clear();
		targetAdjacentLanes.clear();
		if (solutionStack!=null)
			solutionStack.clear();
		if (allSolutions!=null)
			allSolutions.clear();
	}
	
	/**
	 * Find solutions.
	 *
	 * @param source2 the source2
	 * @param target2 the target2
	 * @return the list
	 */
	public List< List<RoutingLane> > findSolutions(Shape source2, Shape target2) {
		allSolutions = new ArrayList< List<RoutingLane> >();
		this.source = source2;
		this.target = target2;
		if (source2==null || target2==null) {
			return allSolutions;
		}
		Rectangle sourceBounds = getBounds(false, source2);
		Rectangle targetBounds = getBounds(false, target2);
		sourceAdjacentLanes.clear();
		targetAdjacentLanes.clear();
		
		for (RoutingLane a1 : this) {
			if (a1.adjacent(sourceBounds) != RoutingLane.Adjacence.NONE) {
				sourceAdjacentLanes.add(a1);
			}
			if (a1.adjacent(targetBounds) != RoutingLane.Adjacence.NONE) {
				targetAdjacentLanes.add(a1);
			}
		}
		
		solutionStack = new Stack<RoutingLane>();
		minDist = Integer.MAX_VALUE;
		
		for (RoutingLane sa : sourceAdjacentLanes) {
			for (RoutingLane ta : targetAdjacentLanes) {
				sa.navigateTo(ta, this);
			}
		}
		Collections.sort(allSolutions, new Comparator<List<RoutingLane>>() {
			@Override
			public int compare(List<RoutingLane> arg0, List<RoutingLane> arg1) {
				int i;
				double m0 = merit(arg0);
				double m1 = merit(arg1);
				if (m0<m1)
					i = 1;
				else if (m0>m1)
					i = -1;
				else
					i = 0;
				if (true)
					return i;
				
				i = getManhattanDistance(arg0) - getManhattanDistance(arg1);
				if (i==0) {
					// find the lane with the easiest passage
					int arg0min = Integer.MAX_VALUE;
					int arg1min = Integer.MAX_VALUE;
					if (isRotated) {
						for (RoutingLane a : arg0) {
							int d = a.getHeight();
							if (d<arg0min)
								arg0min = d;
						}
						for (RoutingLane a : arg1) {
							int d = a.getHeight();
							if (d<arg1min)
								arg1min = d;
						}
					}
					else {
						for (RoutingLane a : arg0) {
							int d = a.getWidth();
							if (d<arg0min)
								arg0min = d;
						}
						for (RoutingLane a : arg1) {
							int d = a.getWidth();
							if (d<arg1min)
								arg1min = d;
						}
					}
					i = arg1min - arg0min;
				}
				return i;
			}
		});
		return allSolutions;
	}
	
	/**
	 * Merit.
	 *
	 * @param list the list
	 * @return the double
	 */
	public double merit(List<RoutingLane> list) {
		ILocation sourceLoc = Graphiti.getPeService().getLocationRelativeToDiagram(source);
		IDimension sourceSize = GraphicsUtil.calculateSize(source);
		ILocation targetLoc = Graphiti.getPeService().getLocationRelativeToDiagram(target);
		IDimension targetSize = GraphicsUtil.calculateSize(target);
		Point p1 = GraphicsUtil.getShapeCenter(source);
		Point p2 = GraphicsUtil.getShapeCenter(target);
		if (isRotated) {
			if (sourceLoc.getY() + sourceSize.getHeight() < targetLoc.getY()) {
				p1.setY(sourceLoc.getY() + sourceSize.getHeight());
				p2.setY(targetLoc.getY() );
			}
			else if (targetLoc.getY() + targetSize.getHeight() < sourceLoc.getY()) {
				p1.setY(sourceLoc.getY());
				p2.setY(targetLoc.getY() + targetSize.getHeight());
			}
		}
		else {
			if (sourceLoc.getX() + sourceSize.getWidth() < targetLoc.getX()) {
				p1.setX(sourceLoc.getX() + sourceSize.getWidth());
				p2.setX(targetLoc.getX() );
			}
			else if (targetLoc.getX() + targetSize.getWidth() < sourceLoc.getX()) {
				p1.setX(sourceLoc.getX());
				p2.setX(targetLoc.getX() + targetSize.getWidth());
			}
		}
		int i = 0;
		double length = GraphicsUtil.getLength(p1, p2);
		for (RoutingLane rl : list) {
			if ( GraphicsUtil.RectangleIntersectsLine.intersectsLine(
					p1.getX(), p1.getY(), p2.getX(), p2.getY(),
					rl.rect.x, rl.rect.y, rl.rect.width, rl.rect.height)) {
				i += length;
			}
			else 
			{
				Point c = GraphicsUtil.createPoint(rl.rect.getCenter().x, rl.rect.getCenter().y);
				double d = pointToLineDistance(p1, p2, c);
				i += length / d;
			}
		}
		return (double) i / (double)(length * list.size());
	}

	/**
	 * Point to line distance.
	 *
	 * @param p1 the p1
	 * @param p2 the p2
	 * @param p the p
	 * @return the double
	 */
	public double pointToLineDistance(Point p1, Point p2, Point p) {
		double normalLength = Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
		return Math.abs((p.getX() - p1.getX()) * (p2.getY() - p1.getY()) - (p.getY() - p1.getY()) * (p2.getX() - p1.getX())) / normalLength;
	}

	/**
	 * Pop.
	 */
	public void pop() {
		solutionStack.pop();
	}

	/**
	 * Visited.
	 *
	 * @param lane the lane
	 * @return true, if successful
	 */
	public boolean visited(RoutingLane lane) {
		return solutionStack.contains(lane);
	}

	/**
	 * Push.
	 *
	 * @param lane the lane
	 */
	public void push(RoutingLane lane) {
		solutionStack.push(lane);
	}

	/**
	 * Gets the manhattan distance.
	 *
	 * @param lanes the lanes
	 * @return the manhattan distance
	 */
	public int getManhattanDistance(List<RoutingLane> lanes) {
		int dist = 0;
		Rectangle r0 = getBounds(false, source);
		int x0 = r0.x + r0.width/2;
		int y0 = r0.y + r0.height/2;
		Rectangle r1 = getBounds(false, target);
		int x1 = r1.x + r1.width/2;
		int y1 = r1.y + r1.height/2;
		int dx = Math.abs(x0 - x1);
		int dy = Math.abs(y0 - y1);

		RoutingLane a0 = lanes.get(0);
		RoutingLane a1;
		RoutingLane a2 = null;

		if (isRotated) {
			if (dy>a0.getHeight())
				dist += a0.getHeight();
			dist += dx;
			for (int i=1; i<lanes.size(); ++i) {
				a1 = lanes.get(i);
				int d = 0;
				if (dy>a1.getHeight())
					d = a1.getHeight();
				else
					d = dy;
				if (i+1<lanes.size()) {
					a2 = lanes.get(i+1);
					if (a0.getRight().contains(a1) && a1.getRight().contains(a2) ||
							a0.getLeft().contains(a1) && a1.getLeft().contains(a2)) {
						dist += d;
					}
				}
				else  {
					dist += d;
				}
				
				d = 0;
				int right = a1.getX() + a1.getWidth();
				int left = a1.getX();
				if (right < x0) {
					d = x0 - right;
					x0 = right;
				}
				else if (left > x0) {
					d = left - x0;
					x0 = left;
				}
				dist += d;
				a0 = a1;
			}
		}
		else {
			if (dx>a0.getWidth())
				dist += a0.getWidth();
			double dd = (double) a0.getWidth() / (double) a0.getHeight();
			dist += dy;
			for (int i=1; i<lanes.size(); ++i) {
				a1 = lanes.get(i);
				int d = 0;
				if (dx>a1.getWidth())
					d = a1.getWidth();
				else
					d = dx;
				if (i+1<lanes.size()) {
					a2 = lanes.get(i+1);
					if (a0.getRight().contains(a1) && a1.getRight().contains(a2) ||
							a0.getLeft().contains(a1) && a1.getLeft().contains(a2)) {
						dist += d;
					}
				}
				else  {
					dist += d;
				}
				
				d = 0;
				int bottom = a1.getY() + a1.getHeight();
				int top = a1.getY();
				if (bottom < y0) {
					d = y0 - bottom;
					y0 = bottom;
				}
				else if (top > y0) {
					d = top - y0;
					y0 = top;
				}
				dist += d;
				a0 = a1;
			}
		}
		return dist;
	}
	
	/**
	 * Solution found.
	 *
	 * @return true, if successful
	 */
	public boolean solutionFound() {
		
		if (!allSolutions.contains(solutionStack)) {
//			int d = getManhattanDistance(solutionStack);
//			if (d - 0.5 * minDist < minDist) {
//				if (d < minDist)
//					minDist = d;
				List<RoutingLane> solution = new ArrayList<RoutingLane>(solutionStack);
				allSolutions.add(solution);
//			}
		}
		return true;
	}
	
	/**
	 * Rotate.
	 *
	 * @param b the b
	 */
	public void rotate(boolean b) {
		if (isRotated!=b) {
			for (RoutingLane node : this) {
				node.rotate(b);
			}
			isRotated = b;
		}
	}
	
	/**
	 * Gets the lanes adjacent to.
	 *
	 * @param shape the shape
	 * @param adjacence the adjacence
	 * @return the lanes adjacent to
	 */
	public List<RoutingLane> getLanesAdjacentTo(ContainerShape shape, Adjacence adjacence) {
		List<RoutingLane> adjacentLanes;
		List<RoutingLane> list = new ArrayList<RoutingLane>();
		if (shape==source) {
			adjacentLanes = sourceAdjacentLanes;
		}
		else if (shape==target) {
			adjacentLanes = targetAdjacentLanes;
		}
		else
			return list;
		
		Rectangle bounds = getBounds(isRotated, shape);
		for (RoutingLane a : adjacentLanes) {
			if (a.adjacent(bounds) == adjacence)
				list.add(a);
		}
		return list;
	}

	private Rectangle getBounds(Shape target2) {
		return getBounds(isRotated,target2);
	}

	/**
	 * Gets the bounds.
	 *
	 * @param rotate the rotate
	 * @param source2 the source2
	 * @return the bounds
	 */
	protected static Rectangle getBounds(boolean rotate, Shape source2) {
		ILocation loc = peService.getLocationRelativeToDiagram(source2);
		IDimension size = GraphicsUtil.calculateSize(source2);
		if (rotate) {
			return rotateRectangle(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
		}
		return new Rectangle(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
	}

	/**
	 * Rotate rectangle.
	 *
	 * @param x the x
	 * @param y the y
	 * @param width the width
	 * @param height the height
	 * @return the rectangle
	 */
	protected static Rectangle rotateRectangle(int x, int y, int width, int height) {
		return rotateRectangle(new Rectangle(x,y,width,height));
	}

	/**
	 * Rotate rectangle.
	 *
	 * @param r the r
	 * @return the rectangle
	 */
	public static Rectangle rotateRectangle(Rectangle r) {
		int y = r.x;
		int x = r.y;
		int w = r.height;
		int h = r.width;
		r.x = x;
		r.y = y;
		r.width = w;
		r.height = h;
		return r;
	}

	/**
	 * **************************************************************************************
	 * 
	 * Debug stuff
	 * 
	 * **************************************************************************************.
	 */
	protected class AddRoutingLaneFeature extends AbstractAddShapeFeature {
		
		/**
		 * Instantiates a new adds the routing lane feature.
		 *
		 * @param fp the fp
		 */
		public AddRoutingLaneFeature(IFeatureProvider fp) {
			super(fp);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
		 */
		@Override
		public boolean canAdd(IAddContext context) {
			return true;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
		 */
		@Override
		public PictogramElement add(IAddContext context) {
			int x = context.getX();
			int y = context.getY();
			int width = context.getWidth();
			int height = context.getHeight();
			IColorConstant foreground = new ColorConstant(0,0,255);
			RoutingLane lane = (RoutingLane)context.getNewObject();
			IColorConstant background = new ColorConstant(128,128,128);
			double transparency = 0.75;
			
			Rectangle bounds = getBounds(source);
			if (lane==null) {
				Object bg = context.getProperty("background"); //$NON-NLS-1$
				if (bg instanceof ColorConstant)
					background = (ColorConstant)bg;
				else
					background = new ColorConstant(0,255,255);
				transparency = .50;
			}
			else {
				boolean sourceAdjacent = false;
				if (lane.adjacent(bounds) != RoutingLane.Adjacence.NONE) {
					background = new ColorConstant(0,255,0);
					transparency = 0.25;
					sourceAdjacent = true;
				}
				bounds = getBounds(target);
				if (lane.adjacent(bounds) != RoutingLane.Adjacence.NONE) {
					if (sourceAdjacent) {
						background = new ColorConstant(255,255,0);
					}
					else {
						background = new ColorConstant(255,0,0);
					}
					transparency = 0.25;
				}
			}
			
			Diagram diagram = getDiagram();
			
			ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);
			org.eclipse.graphiti.mm.algorithms.Rectangle invisibleRect = gaService.createInvisibleRectangle(containerShape);
			gaService.setLocationAndSize(invisibleRect, x, y, width, height);

			Shape rectShape = peService.createShape(containerShape, false);
			RoundedRectangle roundedRect = gaService.createRoundedRectangle(rectShape, 1, 1);
			roundedRect.setForeground(gaService.manageColor(diagram, foreground));
			roundedRect.setBackground(gaService.manageColor(diagram, background));
			roundedRect.setFilled(true);
			roundedRect.setTransparency(transparency);
			roundedRect.setLineWidth(2);

//			link(rectShape, context.getNewObject());
			peService.setPropertyValue(containerShape, LANE, "true"); //$NON-NLS-1$
			
			gaService.setLocationAndSize(roundedRect, 0, 0, width, height);
			peService.sendToFront(containerShape);
			return containerShape;
		}
	}
	
	private class DeleteRoutingLaneFeature extends DefaultDeleteFeature {

		public DeleteRoutingLaneFeature(IFeatureProvider fp) {
			super(fp);
		}
		
	}
	
	private class AddRoutingLaneConnectionFeature extends AbstractAddShapeFeature {

		public AddRoutingLaneConnectionFeature(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canAdd(IAddContext ac) {
			return true;
		}

		@Override
		public PictogramElement add(IAddContext ac) {
			IAddConnectionContext context = (IAddConnectionContext) ac;
			Anchor sourceAnchor = context.getSourceAnchor();
			Anchor targetAnchor = context.getTargetAnchor();
			ContainerShape sourceShape = (ContainerShape) sourceAnchor.getParent();
			ContainerShape targetShape = (ContainerShape) targetAnchor.getParent();
			Object[] newObject = (Object[]) context.getNewObject();
			RoutingLane sourceNode = (RoutingLane)newObject[0];
			RoutingLane targetNode = (RoutingLane)newObject[1];
			
			Diagram diagram = getDiagram();
			Connection connection = peService.createFreeFormConnection(diagram);
			connection.setStart(sourceAnchor);
			connection.setEnd(targetAnchor);
			peService.setPropertyValue(connection, CONNECTION, "true"); //$NON-NLS-1$

			Polyline connectionLine = Graphiti.getGaService().createPolyline(connection);

			connectionLine.setLineWidth(1);
			IColorConstant foreground = new ColorConstant(0,0,255);
			
			int w = 3;
			int l = 15;
			
			ConnectionDecorator decorator = peService.createConnectionDecorator(connection, false, 1.0, true);
			Polyline arrowhead = gaService.createPolygon(decorator, new int[] { -l, w, 0, 0, -l, -w, -l, w });
			arrowhead.setForeground(gaService.manageColor(diagram, foreground));
			connectionLine.setForeground(gaService.manageColor(diagram, foreground));

			return connection;
		}
	}
	
	/**
	 * Draw lanes.
	 */
	public void drawLanes() {
		if (fp!=null) {
			Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
			for (RoutingLane a : this) {
				AddContext context = new AddContext();
				context.setTargetContainer(diagram);
				context.setNewObject(a);
				context.setX(a.getX());
				context.setY(a.getY());
				context.setSize(a.getWidth(), a.getHeight());
				AddRoutingLaneFeature feature = new AddRoutingLaneFeature(fp);
				a.setShape(feature.add(context));
			}
		}
	}
	
	/**
	 * Draw connections.
	 */
	public void drawConnections() {
		if (fp!=null) {
			Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
			ContainerShape sourceShape;
			Anchor sourceAnchor;
			ContainerShape targetShape;
			Anchor targetAnchor;
			
			for (RoutingLane n1 : this) {
				for (RoutingLane n2 : n1.getRight()) {
					if (n1!=n2) {
						sourceShape = n1.getShape();
						targetShape = n2.getShape();
						if (sourceShape!=null && targetShape!=null) {
							if (sourceShape.getAnchors().size()>0)
								sourceAnchor = sourceShape.getAnchors().get(0);
							else {
								FixPointAnchor a = peService.createFixPointAnchor(sourceShape);
								Rectangle r = getBounds(false,sourceShape);
								a.setLocation(GraphicsUtil.createPoint(r.width/2, r.height/2));
								gaService.createInvisibleRectangle(a);
								sourceAnchor = a;
							}
	
							if (targetShape.getAnchors().size()>0)
								targetAnchor = targetShape.getAnchors().get(0);
							else {
								FixPointAnchor a = peService.createFixPointAnchor(targetShape);
								Rectangle r = getBounds(false,targetShape);
								a.setLocation(GraphicsUtil.createPoint(r.width/2, r.height/2));
								gaService.createInvisibleRectangle(a);
								targetAnchor = a;
							}
							AddConnectionContext context = new AddConnectionContext(sourceAnchor, targetAnchor);
							context.setTargetContainer(diagram);
							context.setNewObject(new Object[] {n1, n2});
							AddRoutingLaneConnectionFeature feature = new AddRoutingLaneConnectionFeature(fp);
							feature.add(context);
						}
					}
				}
			}
		}
	}

	/**
	 * Draw solution.
	 *
	 * @param net the net
	 * @param i the i
	 */
	public void drawSolution(List<RoutingLane> net, int i) {
		if (fp!=null) {
			Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
			for (RoutingLane a : net) {
				AddContext context = new AddContext();
				context.setTargetContainer(diagram);
				context.setNewObject( null );
				context.setLocation(a.getX(), a.getY());
				context.setSize(a.getWidth(), a.getHeight());
//				context.putProperty("background", new ColorConstant(32+i*(i%3),32+i*(i%6),32+i*(i%9)));
				AddRoutingLaneFeature feature = new AddRoutingLaneFeature(fp);
				a.setShape(feature.add(context));
			}
		}
	}

	/**
	 * Erase lanes.
	 */
	public void eraseLanes() {
		if (fp!=null) {
			for (RoutingLane a : this) {
				Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
				List<ContainerShape> deleted = new ArrayList<ContainerShape>();
				TreeIterator iter = diagram.eAllContents();
				while (iter.hasNext()) {
					Object o = iter.next();
					if (o instanceof ContainerShape) {
						ContainerShape s = (ContainerShape)o;
						if (peService.getPropertyValue(s, LANE)!=null) {
							deleted.add(s);
						}
					}
				}
				for (ContainerShape s : deleted) {
					DeleteContext context = new DeleteContext(s);
					DeleteRoutingLaneFeature feature = new DeleteRoutingLaneFeature(fp);
					feature.delete(context);
				}
			}
		}
	}
}
