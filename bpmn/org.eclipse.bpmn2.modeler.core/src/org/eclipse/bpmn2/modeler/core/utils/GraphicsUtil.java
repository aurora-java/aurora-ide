/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.core.utils;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.AnchorLocation;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.services.IPeService;

/**
 *
 */
public class GraphicsUtil {

	static final IGaService gaService = Graphiti.getGaService();
	static final IPeService peService = Graphiti.getPeService();

	public interface IShapeFilter {
		boolean matches(Shape shape);
	}

	/**
	 * Shape Filter that matches only Container Shapes and not Labels
	 */
	public class ContainerShapeFilter implements GraphicsUtil.IShapeFilter {
		public boolean matches(Shape shape) {
			return shape instanceof ContainerShape && !FeatureSupport.isLabelShape(shape);
		}
	}

	/**
	 * Code copied from {@link java.awt.geom.Rectangle2D#intersectsLine(double, double, double, double)}
	 * in an attempt to avoid loading the java.awt package here...
	 */
	public static final class RectangleIntersectsLine {
	    private static final int OUT_LEFT = 1;
	    private static final int OUT_TOP = 2;
	    private static final int OUT_RIGHT = 4;
	    private static final int OUT_BOTTOM = 8;
	
	    private static int outcode(double pX, double pY, double rectX, double rectY, double rectWidth, double rectHeight) {
	        int out = 0;
	        if (rectWidth <= 0) {
	            out |= OUT_LEFT | OUT_RIGHT;
	        } else if (pX < rectX) {
	            out |= OUT_LEFT;
	        } else if (pX > rectX + rectWidth) {
	            out |= OUT_RIGHT;
	        }
	        if (rectHeight <= 0) {
	            out |= OUT_TOP | OUT_BOTTOM;
	        } else if (pY < rectY) {
	            out |= OUT_TOP;
	        } else if (pY > rectY + rectHeight) {
	            out |= OUT_BOTTOM;
	        }
	        return out;
	    }
	
	    public static boolean intersectsLine(double lineX1, double lineY1, double lineX2, double lineY2, double rectX, double rectY, double rectWidth, double rectHeight) {
	        int out1, out2;
	        if ((out2 = outcode(lineX2, lineY2, rectX, rectY, rectWidth, rectHeight)) == 0) {
	            return true;
	        }
	        while ((out1 = outcode(lineX1, lineY1, rectX, rectY, rectWidth, rectHeight)) != 0) {
	            if ((out1 & out2) != 0) {
	                return false;
	            }
	            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
	                double x = rectX;
	                if ((out1 & OUT_RIGHT) != 0) {
	                    x += rectWidth;
	                }
	                lineY1 = lineY1 + (x - lineX1) * (lineY2 - lineY1) / (lineX2 - lineX1);
	                lineX1 = x;
	            } else {
	                double y = rectY;
	                if ((out1 & OUT_BOTTOM) != 0) {
	                    y += rectHeight;
	                }
	                lineX1 = lineX1 + (y - lineY1) * (lineX2 - lineX1) / (lineY2 - lineY1);
	                lineY1 = y;
	            }
	        }
	        return true;
	    }
	}

	public static class LineSegment {
		private Point start;
		private Point end;
		
		public LineSegment() {
			this(0,0,0,0);
		}
		public LineSegment(Point start, Point end) {
			this(start.getX(),start.getY(), end.getX(),end.getY());
		}
		public LineSegment(int x1, int y1, int x2, int y2) {
			start = Graphiti.getCreateService().createPoint(x1, y1);
			end = Graphiti.getCreateService().createPoint(x2, y2);
		}
		public void setStart(Point p) {
			setStart(p.getX(),p.getY());
		}
		public void setStart(int x, int y) {
			start.setX(x);
			start.setY(y);
		}
		public void setEnd(Point p) {
			setEnd(p.getX(),p.getY());
		}
		public void setEnd(int x, int y) {
			end.setX(x);
			end.setY(y);
		}
		public Point getStart() {
			return start;
		}
		public Point getEnd() {
			return end;
		}
		public double getDistance(Point p) {
			// for vertical and horizontal line segments, the distance to a point
			// is the orthogonal distance if the point lies between the start and end
			// points of the line segment
			if (isHorizontal()) {
				if (p.getX()>=start.getX() && p.getX()<=end.getX())
					return Math.abs(start.getY() - p.getY());
			}
			if (isVertical()) {
				if (p.getY()>=start.getY() && p.getY()<=end.getY())
					return Math.abs(start.getX() - p.getX());
			}
			// otherwise, the distance is the minimum of the distances
			// of the point to the two endpoints of the line segment
	        double d1 = getDistanceToStart(p);
	        double d2 = getDistanceToEnd(p);
	        return Math.min(d1, d2);
		}
		public boolean isHorizontal() {
			return Math.abs(start.getY() - end.getY()) <= 1;
		}
		public boolean isVertical() {
			return Math.abs(start.getX() - end.getX()) <= 1;
		}
		public boolean isSlanted() {
			return !isHorizontal() && !isVertical();
		}
		public double getDistanceToStart(Point p) {
	        return Math.hypot(start.getX()-p.getX(), start.getY()-p.getY());
		}
		public double getDistanceToEnd(Point p) {
	        return Math.hypot(end.getX()-p.getX(), end.getY()-p.getY());
		}
		
		public String toString() {
			return "[" + start.getX() + "," + start.getY() +"]" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					" [" + end.getX() + "," + end.getY() +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * Set the location of the PE given absolute Diagram coordinates. If the PE
	 * is a child of a ContainerShape, adjust the coordinates so that they are
	 * relative to the ContainerShape.
	 *  
	 * @param pe the PictogramElement to move
	 * @param x the absolute Diagram-relative X coordinate
	 * @param y the absolute Diagram-relative Y coordinate
	 */
	public static void setLocationRelativeToDiagram(PictogramElement pe, int x, int y) {
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		Object o = pe.eContainer();
		if (o instanceof ContainerShape && !(o instanceof Diagram)) {
			ILocation containerLoc = peService.getLocationRelativeToDiagram((Shape)o);
			x -= containerLoc.getX();
			y -= containerLoc.getY();
		}
		gaService.setLocation(ga, x, y);
	}

	public static boolean contains(Shape parent, Shape child) {
		IDimension size = GraphicsUtil.calculateSize(child);
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(child);
		return GraphicsUtil.contains(parent, GraphicsUtil.createPoint(loc.getX(), loc.getY()))
				&& GraphicsUtil.contains(parent, GraphicsUtil.createPoint(loc.getX() + size.getWidth(), loc.getY()))
				&& GraphicsUtil.contains(parent, GraphicsUtil.createPoint(loc.getX() + size.getWidth(), loc.getY() + size.getHeight()))
				&& GraphicsUtil.contains(parent, GraphicsUtil.createPoint(loc.getX(), loc.getY() + size.getHeight()));
	}

	public static boolean contains(Shape shape, Point point) {
		IDimension size = GraphicsUtil.calculateSize(shape);
		ILocation loc = Graphiti.getLayoutService().getLocationRelativeToDiagram(shape);
		int x = point.getX();
		int y = point.getY();
		return x>loc.getX() && x<loc.getX() + size.getWidth() &&
				y>loc.getY() && y<loc.getY() + size.getHeight();
	}

	public static boolean intersects(Shape shape1, Shape shape2) {
		ILayoutService layoutService = Graphiti.getLayoutService();
		ILocation loc2 = layoutService.getLocationRelativeToDiagram(shape2);
		int x2 = loc2.getX();
		int y2 = loc2.getY();
		int w2 = ShapeDecoratorUtil.getShapeWidth(shape2);
		int h2 = ShapeDecoratorUtil.getShapeHeight(shape2);
		return GraphicsUtil.intersects(shape1, x2, y2, w2, h2);
	}

	public static boolean intersects(Shape shape1, int x2, int y2, int w2, int h2) {
		ILayoutService layoutService = Graphiti.getLayoutService();
		ILocation loc1 = layoutService.getLocationRelativeToDiagram(shape1);
		int x1 = loc1.getX();
		int y1 = loc1.getY();
		int w1 = ShapeDecoratorUtil.getShapeWidth(shape1);
		int h1 = ShapeDecoratorUtil.getShapeHeight(shape1);
		return GraphicsUtil.intersects(x1, y1, w1, h1, x2, y2, w2, h2);
	}

	public static boolean intersects(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if(x2<=x1 || y1<=y2) {  
			int t1, t2, t3, t4;
			t1 = x1; x1 = x2; x2 = t1;  
			t2 = y1; y1 = y2; y2 = t2;  
			t3 = w1; w1 = w2; w2 = t3;  
			t4 = h1; h1 = h2; h2 = t4;  
		}  
		if( y2 + h2 < y1 || y1 + h1 < y2 ||  x2 + w2 < x1 || x1 + w1 < x2 ) {
			return false;
		}
		return true;
	}

	public static boolean intersects(Shape shape, Connection connection) {
		Point p1 = GraphicsUtil.createPoint(connection.getStart());
		Point p3 = GraphicsUtil.createPoint(connection.getEnd());
		if (connection instanceof FreeFormConnection) {
			FreeFormConnection ffc = (FreeFormConnection) connection;
			Point p2 = p1;
			for (Point p : ffc.getBendpoints()) {
				if (GraphicsUtil.intersectsLine(shape, p1, p))
					return true;
				p2 = p1 = p;
			}
			if (GraphicsUtil.intersectsLine(shape, p2, p3))
				return true;
		}
		else if (GraphicsUtil.intersectsLine(shape, p1, p3))
			return true;
		return false;
	}

	public static boolean intersectsLine(Shape shape, Point p1, Point p2) {
			ILocation loc = peService.getLocationRelativeToDiagram(shape);
			IDimension size = GraphicsUtil.calculateSize(shape);
			// adjust the shape rectangle so that a point touching one of the edges
			// is not considered to be "intersecting"
			if (size.getWidth()>2) {
				loc.setX(loc.getX()+1);
				size.setWidth(size.getWidth()-2);
			}
			if (size.getHeight()>2) {
				loc.setY(loc.getY()+1);
				size.setHeight(size.getHeight()-2);
			}
			return GraphicsUtil.RectangleIntersectsLine.intersectsLine(
					p1.getX(), p1.getY(), p2.getX(), p2.getY(),
					loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
			
	//		java.awt.Rectangle rect = new java.awt.Rectangle(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());
	//		return rect.intersectsLine(start.getX(), start.getY(), end.getX(), end.getY());
		}

	public static boolean intersects(Point p1Start, Point p1End, Point p2Start, Point p2End) {
		return GraphicsUtil.isLineIntersectingLine(
				p1Start.getX(), p1Start.getY(),
				p1End.getX(), p1End.getY(),
				p2Start.getX(), p2Start.getY(),
				p2End.getX(), p2End.getY()
		);
	}

	/**
	 * Check if two line segments intersects. Integer domain.
	 * 
	 * @param x0, y0, x1, y1 End points of first line to check.
	 * @param x2, yy, x3, y3 End points of second line to check.
	 * @return True if the two lines intersects.
	 */
	public static boolean isLineIntersectingLine(int x0, int y0, int x1,
			int y1, int x2, int y2, int x3, int y3) {
		int s1 = GraphicsUtil.sameSide(x0, y0, x1, y1, x2, y2, x3, y3);
		int s2 = GraphicsUtil.sameSide(x2, y2, x3, y3, x0, y0, x1, y1);
	
		return s1 <= 0 && s2 <= 0;
	}

	/**
	 * Check if two points are on the same side of a given line. Algorithm from
	 * Sedgewick page 350.
	 * 
	 * @param x0, y0, x1, y1 The line.
	 * @param px0, py0 First point.
	 * @param px1, py1 Second point.
	 * @return <0 if points on opposite sides. =0 if one of the points is
	 *         exactly on the line >0 if points on same side.
	 */
	static int sameSide(int x0, int y0, int x1, int y1,
			int px0, int py0, int px1, int py1) {
		int sameSide = 0;
	
		int dx = x1 - x0;
		int dy = y1 - y0;
		int dx1 = px0 - x0;
		int dy1 = py0 - y0;
		int dx2 = px1 - x1;
		int dy2 = py1 - y1;
	
		// Cross product of the vector from the endpoint of the line to the
		// point
		int c1 = dx * dy1 - dy * dx1;
		int c2 = dx * dy2 - dy * dx2;
	
		if (c1 != 0 && c2 != 0)
			sameSide = c1 < 0 != c2 < 0 ? -1 : 1;
		else if (dx == 0 && dx1 == 0 && dx2 == 0)
			sameSide = !GraphicsUtil.isBetween(y0, y1, py0) && !GraphicsUtil.isBetween(y0, y1, py1) ? 1
					: 0;
		else if (dy == 0 && dy1 == 0 && dy2 == 0)
			sameSide = !GraphicsUtil.isBetween(x0, x1, px0) && !GraphicsUtil.isBetween(x0, x1, px1) ? 1
					: 0;
	
		return sameSide;
	}

	/**
	 * Return true if c is between a and b.
	 */
	static boolean isBetween(int a, int b, int c) {
		return b > a ? c >= a && c <= b : c >= b && c <= a;
	}

	public static Color clone(Color c) {
		return c;
	}

	public static boolean pointsEqual(Point p1, Point p2) {
		return p1.getX()==p2.getX() && p1.getY()==p2.getY();
	}

	public static Point createPoint(Point p) {
		return gaService.createPoint(p.getX(), p.getY());
	}

	public static Point createPoint(int x, int y) {
		return gaService.createPoint(x, y);
	}

	public static Point createPoint(Anchor a) {
		return GraphicsUtil.createPoint(peService.getLocationRelativeToDiagram(a));
	}

	public static Point createPoint(AnchorContainer ac) {
		if (ac instanceof Shape)
			return GraphicsUtil.createPoint(peService.getLocationRelativeToDiagram((Shape)ac));
		return null;
	}

	public static Point getShapeCenter(AnchorContainer shape) {
		Point p = createPoint(shape);
		IDimension size = GraphicsUtil.calculateSize(shape);
		p.setX( p.getX() + size.getWidth()/2 );
		p.setY( p.getY() + size.getHeight()/2 );
		return p;
	}

	public static Point createPoint(ILocation loc) {
		return createPoint(loc.getX(), loc.getY());
	}

	public static Point getMidpoint(Point p1, Point p2) {
		int dx = p2.getX() - p1.getX();
		int dy = p2.getY() - p1.getY();
		int x = p1.getX() + dx/2;
		int y = p1.getY() + dy/2;
		return createPoint(x,y);
	}

	public static double getLength(ILocation start, ILocation end) {
		double a = (double)(start.getX() - end.getX());
		double b = (double)(start.getY() - end.getY());
		return Math.sqrt(a*a + b*b);
	}

	public static double getLength(List<Point> points) {
		double length = 0;
		int size = points.size();
		if (size>=2) {
			Point p1 = points.get(0);
			for (int i=1; i<size-1; ++i) {
				Point p2 = points.get(i);
				length += GraphicsUtil.getLength(p1,p2);
				p1 = p2;
			}
		}
		return length;
	}

	public static double getLength(Point p1, Point p2) {
		double a = (double)(p1.getX() - p2.getX());
		double b = (double)(p1.getY() - p2.getY());
		return Math.sqrt(a*a + b*b);
	}

	/**
	 * Check if the line segment defined by the two Points is vertical.
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the line segment is vertical
	 */
	public final static boolean isVertical(Point p1, Point p2) {
		return Math.abs(p1.getX() - p2.getX()) == 0;
	}

	/**
	 * Check if the line segment defined by the two Points is horizontal.
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the line segment is horizontal
	 */
	public final static boolean isHorizontal(Point p1, Point p2) {
		return Math.abs(p1.getY() - p2.getY()) == 0;
	}

	/**
	 * Check if the line segment defined by the two Points is neither horizontal nor vertical.
	 * 
	 * @param p1
	 * @param p2
	 * @return true if the line segment is slanted
	 */
	public final static boolean isSlanted(Point p1, Point p2) {
		return !isHorizontal(p1, p2) && !isVertical(p1,p2);
	}

	public static Point getVertMidpoint(Point start, Point end, double fract) {
		Point m = createPoint(start);
		int d = (int)(fract * (double)(end.getY() - start.getY()));
		m.setY(start.getY()+d);
		return m;
	}

	public static Point getHorzMidpoint(Point start, Point end, double fract) {
		Point m = createPoint(start);
		int d = (int)(fract * (double)(end.getX() - start.getX()));
		m.setX(start.getX()+d);
		return m;
	}

	public static IDimension calculateSize(PictogramElement shape) {
		GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
		if (ga!=null)
			return gaService.calculateSize(ga);
		
		IDimension dim = null;
		if (shape instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape)shape;
			for (Shape s : cs.getChildren()) {
				ga = s.getGraphicsAlgorithm();
				if (ga!=null) {
					IDimension d = gaService.calculateSize(ga);
					if (dim==null)
						dim = d;
					else {
						if (d.getWidth() > dim.getWidth())
							dim.setWidth(d.getWidth());
						if (d.getHeight() > dim.getHeight())
							dim.setHeight(d.getHeight());
					}
				}
			}
		}
		return dim;
	}

	public static boolean debug = false;

	public static void dump(String label, List<ContainerShape> shapes) {
		if (shapes!=null) {
			if (debug) {
				System.out.println(label);
				for (ContainerShape shape : shapes)
					GraphicsUtil.dump(1, "",shape,0,0); //$NON-NLS-1$
				System.out.println(""); //$NON-NLS-1$
			}
		}
	}

	public static void dump(String label, Anchor anchor) {
		if (debug) {
			System.out.print(label+" "); //$NON-NLS-1$
			ILocation loc = peService.getLocationRelativeToDiagram(anchor);
			System.out.print(" at "+loc.getX()+", "+loc.getY()); //$NON-NLS-1$ //$NON-NLS-2$
			GraphicsUtil.dump(" parent=", (ContainerShape)anchor.getParent()); //$NON-NLS-1$
			if (AnchorUtil.isBoundaryAnchor(anchor)) {
				String property = Graphiti.getPeService().getPropertyValue(
						anchor, GraphitiConstants.BOUNDARY_FIXPOINT_ANCHOR);
				if (property != null && anchor instanceof FixPointAnchor) {
					System.out.println(" location="+AnchorLocation.getLocation(property)); //$NON-NLS-1$
				}
			}
		}
	}

	public static void dump(String label, ContainerShape shape) {
		GraphicsUtil.dump(0, label,shape,0,0);
	}

	public static void dump(int level, String label, ContainerShape shape) {
		GraphicsUtil.dump(level, label,shape,0,0);
	}

	public static void dump(int level, String label, ContainerShape shape, int x, int y) {
		if (debug) {
			String text = GraphicsUtil.getDebugText(shape);
			for (int i=0; i<level; ++i)
				System.out.print("    "); //$NON-NLS-1$
			System.out.print(label+" "+text); //$NON-NLS-1$
			if (x>0 && y>0) {
				System.out.println(" at "+x+", "+y); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
				System.out.println(""); //$NON-NLS-1$
		}
	}

	public static String getDebugText(ContainerShape shape) {
		EObject be = BusinessObjectUtil.getBusinessObjectForPictogramElement(shape);
		String id = ""; //$NON-NLS-1$
		if (be instanceof BaseElement) {
			id = " " + ((BaseElement)be).getId(); //$NON-NLS-1$
		}
		String text = be.eClass().getName()+id+": "+ExtendedPropertiesProvider.getTextValue(be); //$NON-NLS-1$
		return text;
	}

	public static void dump(String label) {
		if (debug) {
			System.out.println(label);
		}
	}

	public static GraphicsUtil.LineSegment[] getEdges(Shape shape) {
		ILocation loc = peService.getLocationRelativeToDiagram(shape);
		IDimension size = calculateSize(shape);
		GraphicsUtil.LineSegment top = new GraphicsUtil.LineSegment(loc.getX(),loc.getY(),
				loc.getX()+size.getWidth(), loc.getY());
		GraphicsUtil.LineSegment left = new GraphicsUtil.LineSegment(loc.getX(),loc.getY(), loc.getX(),
				loc.getY()+size.getHeight());
		GraphicsUtil.LineSegment bottom = new GraphicsUtil.LineSegment(loc.getX(), loc.getY()+size.getHeight(),
				loc.getX()+size.getWidth(), loc.getY()+size.getHeight());
		GraphicsUtil.LineSegment right = new GraphicsUtil.LineSegment(loc.getX()+size.getWidth(), loc.getY(),
				loc.getX()+size.getWidth(), loc.getY()+size.getHeight());
		return new GraphicsUtil.LineSegment[] {top, bottom, left, right};
	}

	public static GraphicsUtil.LineSegment findNearestEdge(Shape shape, Point p) {
		GraphicsUtil.LineSegment edges[] = getEdges(shape);
		GraphicsUtil.LineSegment top = edges[0];
		GraphicsUtil.LineSegment bottom = edges[1];
		GraphicsUtil.LineSegment left = edges[2];
		GraphicsUtil.LineSegment right = edges[3];
		double minDist;
		double dist;
		GraphicsUtil.LineSegment result;
		
		minDist = top.getDistance(p);
		result = top;
		
		dist = bottom.getDistance(p);
		if (dist<minDist) {
			minDist = dist;
			result = bottom;
		}
		dist = left.getDistance(p);
		if (dist<minDist) {
			minDist = dist;
			result = left;
		}
		dist = right.getDistance(p);
		if (dist<minDist) {
			minDist = dist;
			result = right;
		}
		return result;
	}

	public static void sendToFront(Shape shape) {
		peService.sendToFront(shape);
		BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(shape, BPMNShape.class);
		if (bpmnShape!=null) {
			BPMNPlane plane = (BPMNPlane)bpmnShape.eContainer();
			plane.getPlaneElement().remove(bpmnShape);
			plane.getPlaneElement().add(bpmnShape);
		}
	}

	public static void sendToBack(Shape shape) {
		peService.sendToBack(shape);
		BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(shape, BPMNShape.class);
		if (bpmnShape!=null) {
			BPMNPlane plane = (BPMNPlane)bpmnShape.eContainer();
			plane.getPlaneElement().remove(bpmnShape);
			plane.getPlaneElement().add(0,bpmnShape);
		}
	}

	public static Shape findShapeAt(ContainerShape containerShape, Point p, IShapeFilter filter) {
		for (Shape c : containerShape.getChildren()) {
			if (c.isActive()) {
				if (c instanceof ContainerShape) {
					Shape cc = findShapeAt((ContainerShape) c, p, filter);
					if (cc!=null)
						return cc;
				}
				if (contains(c, p)) {
					if (filter.matches(c)) {
						return c;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Check if the given Point is with a given distance of the given Location.
	 * 
	 * @param p - the Point to check
	 * @param loc - the target Location
	 * @param dist - the maximum distance horizontally and vertically from the given Location
	 * @return true if the point lies within the rectangular area of the Location.
	 */
	public static boolean isPointNear(Point p, ILocation loc, int dist) {
		int x = p.getX();
		int y = p.getY();
		int lx = loc.getX();
		int ly = loc.getY();
		return lx-dist <= x && x <= lx+dist && ly-dist <= y && y <= ly+dist;
	}

	public static Rectangle getBoundingRectangle(List<PictogramElement> pes) {
		int xMin = Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMax = Integer.MIN_VALUE;
		
		for (PictogramElement pe : pes) {
			if (pe instanceof Shape) {
				ILocation loc = Graphiti.getPeService().getLocationRelativeToDiagram((Shape)pe);
				IDimension size = calculateSize(pe);
				if (loc.getX()<xMin)
					xMin = loc.getX();
				if (loc.getY()<yMin)
					yMin = loc.getY();
				if (loc.getX() + size.getWidth()>xMax)
					xMax = loc.getX() + size.getWidth();
				if (loc.getY() + size.getHeight()>yMax)
					yMax = loc.getY() + size.getHeight();
			}
		}
		return new Rectangle(xMin, yMin, xMax-xMin, yMax-yMin);
	}
}
