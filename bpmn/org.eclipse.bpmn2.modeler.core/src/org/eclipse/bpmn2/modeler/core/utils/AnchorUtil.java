/******************************************************************************* 
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.BoundaryEventPositionHelper.PositionOnLine;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
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
import org.eclipse.graphiti.services.ICreateService;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.services.IPeService;

public class AnchorUtil {

	public static final int CONNECTION_POINT_SIZE = 4;

	private static final IPeService peService = Graphiti.getPeService();
	private static final IGaService gaService = Graphiti.getGaService();
	private static final ICreateService createService = Graphiti.getCreateService();
	private static final ILayoutService layoutService = Graphiti.getLayoutService();
	
	public static class AnchorTuple {
		public FixPointAnchor sourceAnchor;
		public FixPointAnchor targetAnchor;
	}

	public enum AnchorLocation {
		TOP("anchor.top"), BOTTOM("anchor.bottom"), LEFT("anchor.left"), RIGHT("anchor.right"), CENTER("anchor.center"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
		private final String key;
	
		private AnchorLocation(String key) {
			this.key = key;
		}
	
		public String getKey() {
			return key;
		}
	
		public static AnchorLocation getLocation(String key) {
			for (AnchorLocation l : values()) {
				if (l.getKey().equals(key)) {
					return l;
				}
			}
			return null;
		}
	}

	public static class BoundaryAnchor {
		public FixPointAnchor anchor;
		public AnchorLocation locationType;
		public ILocation location;
	}
	
	public static Point stringToPoint(String s) {
		if (s!=null) {
			String[] a = s.split(","); //$NON-NLS-1$
			try {
				return gaService.createPoint(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
			}
			catch (Exception e) {
			}
		}
		return null;
	}
	
	public static String pointToString(Point loc) {
		return loc.getX() + "," + loc.getY(); //$NON-NLS-1$
	}
	
	public static FixPointAnchor createBoundaryAnchor(AnchorContainer ac, AnchorLocation loc, int x, int y) {
		FixPointAnchor anchor = peService.createFixPointAnchor(ac);
		peService.setPropertyValue(anchor, GraphitiConstants.BOUNDARY_FIXPOINT_ANCHOR, loc.getKey());
		anchor.setLocation(gaService.createPoint(x, y));
		gaService.createInvisibleRectangle(anchor);

		return anchor;
	}
	
	public static AnchorLocation getBoundaryAnchorLocation(Anchor anchor) {
		String property = peService.getPropertyValue(anchor, GraphitiConstants.BOUNDARY_FIXPOINT_ANCHOR);
		if (property != null && anchor instanceof FixPointAnchor) {
			return AnchorLocation.getLocation(property);
		}
		return null;
	}
	
	public static FixPointAnchor createAdHocAnchor(AnchorContainer ac, int x, int y) {
		return createAdHocAnchor(ac, gaService.createPoint(x, y));
	}
	
	public static FixPointAnchor createAdHocAnchor(AnchorContainer ac, Point p) {
		FixPointAnchor anchor = peService.createFixPointAnchor(ac);
		peService.setPropertyValue(anchor, GraphitiConstants.BOUNDARY_ADHOC_ANCHOR, "true"); //$NON-NLS-1$
		anchor.setLocation(p);
		gaService.createInvisibleRectangle(anchor);

		return anchor;
	}

	public static Map<AnchorLocation, BoundaryAnchor> getConnectionBoundaryAnchors(Shape connectionPointShape) {
		Map<AnchorLocation, BoundaryAnchor> map = new HashMap<AnchorLocation, BoundaryAnchor>(4);
		BoundaryAnchor a = new BoundaryAnchor();
		a.anchor = getConnectionPointAnchor(connectionPointShape);
		for (AnchorLocation al : AnchorLocation.values() ) {
			a.locationType = al;
			a.location = getConnectionPointLocation(connectionPointShape);
			map.put(a.locationType, a);
		}
		return map;
	}
	
	public static Map<AnchorLocation, BoundaryAnchor> getBoundaryAnchors(AnchorContainer ac) {
		Map<AnchorLocation, BoundaryAnchor> map = new HashMap<AnchorLocation, BoundaryAnchor>(4);
		
		if (ac instanceof Connection) {
			// the anchor container is a Connection which does not have any predefined BoundaryAnchors
			// so we have to synthesize these by looking for connection point shapes owned by the connection
			for (Shape connectionPointShape : getConnectionPoints((Connection)ac)) {
				// TODO: if there are multiple connection points, figure out which one to use
				return getConnectionBoundaryAnchors(connectionPointShape);
			}
		}
		else if (AnchorUtil.isConnectionPoint(ac)) {
			return getConnectionBoundaryAnchors((Shape)ac);
		}
		else {
			// anchor container is a ContainerShape - these already have predefined BoundaryAnchors
			Iterator<Anchor> iterator = ac.getAnchors().iterator();
			while (iterator.hasNext()) {
				Anchor anchor = iterator.next();
				String property = peService.getPropertyValue(anchor, GraphitiConstants.BOUNDARY_FIXPOINT_ANCHOR);
				if (property != null && anchor instanceof FixPointAnchor) {
					BoundaryAnchor a = new BoundaryAnchor();
					a.anchor = (FixPointAnchor) anchor;
					a.locationType = AnchorLocation.getLocation(property);
					a.location = peService.getLocationRelativeToDiagram(anchor);
					map.put(a.locationType, a);
				}
			}
		}
		return map;
	}

	public static Point getCenterPoint(Shape s) {
		GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
		ILocation loc = peService.getLocationRelativeToDiagram(s);
		return gaService.createPoint(loc.getX() + (ga.getWidth() / 2), loc.getY() + (ga.getHeight() / 2));
	}

	@SuppressWarnings("restriction")
	public static Tuple<FixPointAnchor, FixPointAnchor> getSourceAndTargetBoundaryAnchors(AnchorContainer source, AnchorContainer target,
			Connection connection) {
		Map<AnchorLocation, BoundaryAnchor> sourceBoundaryAnchors = getBoundaryAnchors(source);
		Map<AnchorLocation, BoundaryAnchor> targetBoundaryAnchors = getBoundaryAnchors(target);
		BoundaryAnchor sourceTop = sourceBoundaryAnchors.get(AnchorLocation.TOP);
		BoundaryAnchor sourceBottom = sourceBoundaryAnchors.get(AnchorLocation.BOTTOM);
		BoundaryAnchor sourceLeft = sourceBoundaryAnchors.get(AnchorLocation.LEFT);
		BoundaryAnchor sourceRight = sourceBoundaryAnchors.get(AnchorLocation.RIGHT);
		BoundaryAnchor targetTop = targetBoundaryAnchors.get(AnchorLocation.TOP);
		BoundaryAnchor targetBottom = targetBoundaryAnchors.get(AnchorLocation.BOTTOM);
		BoundaryAnchor targetLeft = targetBoundaryAnchors.get(AnchorLocation.LEFT);
		BoundaryAnchor targetRight = targetBoundaryAnchors.get(AnchorLocation.RIGHT);
		FixPointAnchor newStartAnchor = null;
		FixPointAnchor newEndAnchor = null;

		if (connection==null) {
			Point p1 = GraphicsUtil.getShapeCenter(source);
			Point p2 = GraphicsUtil.getShapeCenter(target);
			newStartAnchor = findNearestAnchor(source, p2);
			newEndAnchor = findNearestAnchor(target,p1);
			return new Tuple<FixPointAnchor, FixPointAnchor>(newStartAnchor,newEndAnchor);
		}
		
		Anchor oldStartAnchor = connection.getStart();
		Anchor oldEndAnchor = connection.getEnd();
		// if the source and target shape are the same, we're done - just return
		// the existing anchors because the connection router will handle this
		if (	source == target &&
				connection instanceof FreeFormConnection &&
				oldStartAnchor instanceof FixPointAnchor &&
				oldEndAnchor instanceof FixPointAnchor
		) {
			return new Tuple<FixPointAnchor, FixPointAnchor>((FixPointAnchor)oldStartAnchor, (FixPointAnchor)oldEndAnchor);
		}
		
		// Get source and target locations at the time the connection was created.
		Point targetLoc = stringToPoint(peService.getPropertyValue(connection, GraphitiConstants.CONNECTION_TARGET_LOCATION));
		Point sourceLoc = stringToPoint(peService.getPropertyValue(connection, GraphitiConstants.CONNECTION_SOURCE_LOCATION));
		if (targetLoc!=null) {
			// These are relative to the source and target shapes, so we
			// need to translate them to diagram-relative coordinates.
			ILocation loc = peService.getLocationRelativeToDiagram((Shape)target);
			Point p = GraphicsUtil.createPoint(targetLoc);
			p.setX(p.getX() + loc.getX());
			p.setY(p.getY() + loc.getY());
			BoundaryAnchor ba = findNearestBoundaryAnchor(target, p);
			if (useAdHocAnchors(target,connection)) {
				if (ba.locationType==AnchorLocation.TOP)
					targetLoc.setY(targetTop.anchor.getLocation().getY());
				else if (ba.locationType==AnchorLocation.BOTTOM)
					targetLoc.setY(targetBottom.anchor.getLocation().getY());
				else if (ba.locationType==AnchorLocation.LEFT)
					targetLoc.setX(targetLeft.anchor.getLocation().getX());
				else if (ba.locationType==AnchorLocation.RIGHT)
					targetLoc.setX(targetRight.anchor.getLocation().getX());
				adjustPoint(target,targetLoc);
				// if this is a newly created connection, adjust the source location if necessary:
				// if the calculated boundary of the source and target figures are above/below or
				// left/right of each other, align the source location so that the connection line
				// is vertical/horizontal
				if (peService.getPropertyValue(connection, GraphitiConstants.CONNECTION_CREATED)!=null && sourceLoc!=null) {
					FixPointAnchor sourceAnchor = (FixPointAnchor) newStartAnchor;
					if ((newEndAnchor == targetTop.anchor && sourceAnchor == sourceBottom.anchor) ||
						(newEndAnchor == targetBottom.anchor && sourceAnchor == sourceTop.anchor)) {
						// ensure a vertical connection line
						sourceLoc.setX(targetLoc.getX());
					}
					else if ((newEndAnchor == targetRight.anchor && sourceAnchor == sourceLeft.anchor) ||
						(newEndAnchor == targetLeft.anchor && sourceAnchor == sourceRight.anchor)) {
						// ensure a horizontal line
						sourceLoc.setY(targetLoc.getY());
					}
					peService.removeProperty(connection, GraphitiConstants.CONNECTION_CREATED);
				}
				peService.setPropertyValue(connection, GraphitiConstants.CONNECTION_TARGET_LOCATION,
						AnchorUtil.pointToString(targetLoc));

				newEndAnchor = createAdHocAnchor(target, targetLoc);
			}
			else
				newEndAnchor = ba.anchor;
		}
		else {
			if (oldEndAnchor instanceof FixPointAnchor)
				newEndAnchor = (FixPointAnchor)oldEndAnchor;
			else {
				Point p = GraphicsUtil.getShapeCenter(source);
				newEndAnchor = findNearestBoundaryAnchor(target, p).anchor;
			}
		}
		
		if (sourceLoc!=null) {
			ILocation loc = peService.getLocationRelativeToDiagram((Shape)source);
			Point p = GraphicsUtil.createPoint(sourceLoc);
			p.setX(p.getX() + loc.getX());
			p.setY(p.getY() + loc.getY());
			BoundaryAnchor ba = findNearestBoundaryAnchor(source, p);
			if (sourceLoc!=null && useAdHocAnchors(source,connection)) {
				if (ba.locationType==AnchorLocation.TOP)
					sourceLoc.setY(sourceTop.anchor.getLocation().getY());
				else if (ba.locationType==AnchorLocation.BOTTOM)
					sourceLoc.setY(sourceBottom.anchor.getLocation().getY());
				else if (ba.locationType==AnchorLocation.LEFT)
					sourceLoc.setX(sourceLeft.anchor.getLocation().getX());
				else if (ba.locationType==AnchorLocation.RIGHT)
					sourceLoc.setX(sourceRight.anchor.getLocation().getX());
				adjustPoint(source, sourceLoc);
				newStartAnchor = createAdHocAnchor(source, sourceLoc);
				
				peService.setPropertyValue(connection,
						GraphitiConstants.CONNECTION_SOURCE_LOCATION,
						AnchorUtil.pointToString(sourceLoc));
			}
			else
				newStartAnchor = ba.anchor;
		}
		else {
			if (oldStartAnchor instanceof FixPointAnchor)
				newStartAnchor = (FixPointAnchor)oldStartAnchor;
			else {
				Point p = GraphicsUtil.getShapeCenter(target);
				newStartAnchor = findNearestBoundaryAnchor(source, p).anchor;
			}
		}

		return new Tuple<FixPointAnchor, FixPointAnchor>(newStartAnchor,newEndAnchor);
	}

	private static void adjustPoint(PictogramElement pe, Point p) {
		IDimension size = gaService.calculateSize(pe.getGraphicsAlgorithm());
		if (p.getX()<0)
			p.setX(0);
		if (p.getY()<0)
			p.setY(0);
		if (p.getX()>size.getWidth())
			p.setX(size.getWidth());
		if (p.getY()>size.getHeight())
			p.setY(size.getHeight());
	}
		
	public static FixPointAnchor findNearestAnchor(AnchorContainer ac, Point p1) {
		BoundaryAnchor ba = findNearestBoundaryAnchor(ac,p1);
		return ba.anchor;
	}
	
	public static BoundaryAnchor findNearestBoundaryAnchor(AnchorContainer ac, Point p1) {
		Map<AnchorLocation, BoundaryAnchor> boundaryAnchors = getBoundaryAnchors(ac);
		
		// If the shape is a BoundaryEvent, only look at the BoundaryAnchors that are outside
		// of the parent shape.
		String boundaryEventPos = peService.getPropertyValue(
				ac, GraphitiConstants.BOUNDARY_EVENT_RELATIVE_POS);
		if (boundaryEventPos!=null) {
			PositionOnLine pol = PositionOnLine.fromString(boundaryEventPos);
			switch (pol.getLocationType()) {
			case TOP:
				boundaryAnchors.remove(AnchorLocation.BOTTOM);
				boundaryAnchors.remove(AnchorLocation.LEFT);
				boundaryAnchors.remove(AnchorLocation.RIGHT);
				break;
			case TOP_LEFT:
				boundaryAnchors.remove(AnchorLocation.BOTTOM);
				boundaryAnchors.remove(AnchorLocation.RIGHT);
				break;
			case TOP_RIGHT:
				boundaryAnchors.remove(AnchorLocation.BOTTOM);
				boundaryAnchors.remove(AnchorLocation.LEFT);
				break;
			case BOTTOM:
				boundaryAnchors.remove(AnchorLocation.TOP);
				boundaryAnchors.remove(AnchorLocation.LEFT);
				boundaryAnchors.remove(AnchorLocation.RIGHT);
				break;
			case BOTTOM_LEFT:
				boundaryAnchors.remove(AnchorLocation.TOP);
				boundaryAnchors.remove(AnchorLocation.RIGHT);
				break;
			case BOTTOM_RIGHT:
				boundaryAnchors.remove(AnchorLocation.TOP);
				boundaryAnchors.remove(AnchorLocation.LEFT);
				break;
			case LEFT:
				boundaryAnchors.remove(AnchorLocation.TOP);
				boundaryAnchors.remove(AnchorLocation.BOTTOM);
				boundaryAnchors.remove(AnchorLocation.RIGHT);
				break;
			case RIGHT:
				boundaryAnchors.remove(AnchorLocation.TOP);
				boundaryAnchors.remove(AnchorLocation.BOTTOM);
				boundaryAnchors.remove(AnchorLocation.LEFT);
				break;
			case UNKNOWN:
				break;
			}
		}
		
//		System.out.println("findNearestBoundaryAnchor for="+p1.getX()+","+p1.getY());
		double minDist = Double.MAX_VALUE;
		double d1Dist = 0;
		BoundaryAnchor nearestBoundaryAnchor = null;
		for (Entry<AnchorLocation, BoundaryAnchor> entry : boundaryAnchors.entrySet()) {
			BoundaryAnchor ba = entry.getValue();
			Point p = GraphicsUtil.createPoint(ba.anchor); 
			double dist = GraphicsUtil.getLength(p1, p);
//			System.out.println("  at="+p.getX()+","+p.getY()+" anchor="+ba.locationType+" dist="+dist);
			if (dist < minDist)
			{
				double d1 = 0;
				if (false) {
					minDist = dist;
					nearestBoundaryAnchor = ba;
				}
				else {
					// is this really the best choice?
					switch (ba.locationType) {
					case TOP:
						d1 = p.getY() - p1.getY();
						break;
					case BOTTOM:
						d1 = p1.getY() - p.getY();
						break;
					case LEFT:
						d1 = p.getX() - p1.getX();
						break;
					case RIGHT:
						d1 = p1.getX() - p.getX();
						break;
					}
				}
				
				// is this really the best choice?
				if (nearestBoundaryAnchor==null) {
					d1Dist = d1;
					minDist = dist;
					nearestBoundaryAnchor = ba;
				}
				else {
					p = GraphicsUtil.createPoint(nearestBoundaryAnchor.anchor); 
					switch (nearestBoundaryAnchor.locationType) {
					case TOP:
						d1Dist = p.getY() - p1.getY();
						break;
					case BOTTOM:
						d1Dist = p1.getY() - p.getY();
						break;
					case LEFT:
						d1Dist = p.getX() - p1.getX();
						break;
					case RIGHT:
						d1Dist = p1.getX() - p.getX();
						break;
					}
//					System.out.println("  d1="+d1+" d1Dist="+d1Dist);
					if (d1 > d1Dist) {
						d1Dist = d1;
						minDist = dist;
						nearestBoundaryAnchor = ba;
					}
				}
			}
		}
//		System.out.println("  found="+nearestBoundaryAnchor.locationType);
		return nearestBoundaryAnchor;
	}

//	private static void updateEdge(BPMNEdge edge, Diagram diagram) {
//		List<PictogramElement> elements;
//		elements =  Graphiti.getLinkService().getPictogramElements(diagram, edge.getSourceElement());
//		if (elements.size()==0 || !(elements.get(0) instanceof AnchorContainer))
//			return;
//		AnchorContainer source = (AnchorContainer) elements.get(0);
//		
//		elements =  Graphiti.getLinkService().getPictogramElements(diagram, edge.getTargetElement());
//		if (elements.size()==0 || !(elements.get(0) instanceof AnchorContainer))
//			return;
//		AnchorContainer target = (AnchorContainer) elements.get(0);
//		
//		elements = Graphiti.getLinkService().getPictogramElements(diagram, edge);
//		if (elements.size()==0)
//			return;
//		Connection connection = (Connection) elements.get(0);
//		Tuple<FixPointAnchor, FixPointAnchor> anchors = getSourceAndTargetBoundaryAnchors(source, target, connection);
//
//		ILocation loc = peService.getLocationRelativeToDiagram(anchors.getFirst());
//		org.eclipse.dd.dc.Point p = edge.getWaypoint().get(0);
//		p.setX(loc.getX());
//		p.setY(loc.getY());
//
//		loc = peService.getLocationRelativeToDiagram(anchors.getSecond());
//		p = edge.getWaypoint().get(edge.getWaypoint().size() - 1);
//		p.setX(loc.getX());
//		p.setY(loc.getY());
//
//		relocateConnection(source, target, anchors);
//		connection.setStart(anchors.getFirst());
//		connection.setEnd(anchors.getSecond());
//		deleteEmptyAdHocAnchors(source);
//		deleteEmptyAdHocAnchors(target);
//		
//		if (connection instanceof FreeFormConnection) {
//			List<Point> points = ((FreeFormConnection)connection).getBendpoints();
//			if (points.size() == edge.getWaypoint().size()-2) {
//				for (int i=0; i<points.size(); ++i) {
//					p = edge.getWaypoint().get(i+1);
//					p.setX((float)points.get(i).getX());
//					p.setY((float)points.get(i).getY());
//				}
//			}
//		}
//	}
//
//	private static void relocateConnection(AnchorContainer source, AnchorContainer target,
//			Tuple<FixPointAnchor, FixPointAnchor> newAnchors) {
//
//		EList<Anchor> sourceAnchors = source.getAnchors();
//		EList<Anchor> targetAnchors = target.getAnchors();
//		List<Connection> connectionsToBeUpdated = new ArrayList<Connection>();
//
//		for (Anchor anchor : sourceAnchors) {
//			if (!isBoundaryAnchor(anchor)) {
//				continue;
//			}
//
//			for (Connection connection : anchor.getOutgoingConnections()) {
//				if (connection.getEnd().eContainer().equals(target)) {
//					connectionsToBeUpdated.add(connection);
//				}
//			}
//		}
//
//		for (Connection c : connectionsToBeUpdated) {
//			c.setStart(newAnchors.getFirst());
//			c.setEnd(newAnchors.getSecond());
//		}
//	}

	public static void deleteEmptyAdHocAnchors(AnchorContainer target) {
		if (target!=null && !AnchorUtil.isConnectionPoint(target)) {
			List<Integer> indexes = new ArrayList<Integer>();
	
			for (int i = target.getAnchors().size()-1; i>=0; --i) {
				Anchor a = target.getAnchors().get(i);
				if (!(a instanceof FixPointAnchor)) {
					continue;
				}
	
				if (peService.getProperty(a, GraphitiConstants.BOUNDARY_FIXPOINT_ANCHOR) == null && a.getIncomingConnections().isEmpty()
						&& a.getOutgoingConnections().isEmpty()) {
					indexes.add(i);
				}
			}
	
			for (int i : indexes) {
				peService.deletePictogramElement(target.getAnchors().get(i));
			}
		}
	}

	public static boolean isBoundaryAnchor(Anchor anchor) {
		if (anchor instanceof FixPointAnchor) {
			if (peService.getProperty(anchor, GraphitiConstants.BOUNDARY_FIXPOINT_ANCHOR) != null)
				return true;
		}
		return false;
	}

	public static boolean isAdHocAnchor(Anchor anchor) {
		if (anchor instanceof FixPointAnchor) {
			if (peService.getProperty(anchor, GraphitiConstants.BOUNDARY_ADHOC_ANCHOR) != null)
				return true;
		}
		return false;
	}

	// TODO: consider using a Preference to determine if we should use AdHoc anchors vs BoundaryAnchors
	public static boolean useAdHocAnchors(PictogramElement pictogramElement, Connection connection) {
		BaseElement baseElement = BusinessObjectUtil.getFirstBaseElement(pictogramElement);
		BaseElement flowElement = BusinessObjectUtil.getFirstBaseElement(connection);
		return useAdHocAnchors(baseElement, flowElement);
	}
	
	public static boolean useAdHocAnchors(BaseElement baseElement, BaseElement flowElement) {
		if (baseElement instanceof Participant || baseElement instanceof SequenceFlow || baseElement instanceof Group) {
			return true;
		}
		return false;
	}
	
	public static void addFixedPointAnchors(Shape shape, GraphicsAlgorithm ga) {
		IDimension size = gaService.calculateSize(ga);
		int w = size.getWidth();
		int h = size.getHeight();
		createBoundaryAnchor(shape, AnchorLocation.TOP, w / 2, 0);
		createBoundaryAnchor(shape, AnchorLocation.RIGHT, w, h / 2);
		createBoundaryAnchor(shape, AnchorLocation.BOTTOM, w / 2, h);
		createBoundaryAnchor(shape, AnchorLocation.LEFT, 0, h / 2);
	}

	public static void relocateFixPointAnchors(Shape shape, int w, int h) {
		Map<AnchorLocation, BoundaryAnchor> anchors = getBoundaryAnchors(shape);

		FixPointAnchor anchor = anchors.get(AnchorLocation.TOP).anchor;
		anchor.setLocation(gaService.createPoint(w / 2, 0));

		anchor = anchors.get(AnchorLocation.RIGHT).anchor;
		anchor.setLocation(gaService.createPoint(w, h / 2));

		anchor = anchors.get(AnchorLocation.BOTTOM).anchor;
		anchor.setLocation(gaService.createPoint(w / 2, h));

		anchor = anchors.get(AnchorLocation.LEFT).anchor;
		anchor.setLocation(gaService.createPoint(0, h / 2));
	}

	// Connection points allow creation of anchors on FreeFormConnections
	
	private static class ConnectionPointShapeAdapter extends AdapterImpl {
		Connection connection;
		Shape shape;
		boolean deleting = false;
		double midpoint  = 0.5;
		
		public static ConnectionPointShapeAdapter adapt(Connection connection, Shape shape) {
			return new ConnectionPointShapeAdapter(connection, shape);
		}
		
		private ConnectionPointShapeAdapter(Connection connection, Shape shape) {
			this.connection = connection;
			this.shape = shape;
			connection.eAdapters().add(this);
			shape.eAdapters().add(this);
			shape.getAnchors().get(0).eAdapters().add(this);

			setTarget(connection);
			int x = shape.getGraphicsAlgorithm().getX();
			int y = shape.getGraphicsAlgorithm().getY();
			int dx = Integer.MAX_VALUE;
			int dy = Integer.MAX_VALUE;
			for (double d=0; d<=1.0; d += 0.05) {
				ILocation loc = Graphiti.getPeService().getConnectionMidpoint(connection, d);
				if (Math.abs(x - loc.getX()) < dx || Math.abs(y - loc.getY()) < dy) {
					dx = Math.abs(x - loc.getX());
					dy = Math.abs(y - loc.getY());
					midpoint = d;
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
		 */
		@Override
		public void notifyChanged(Notification notification) {
			// if the connection is no longer connected to this shape
			// then delete the Connection Point
			if (!deleting) {
				Anchor a = shape.getAnchors().get(0);
				if (notification.getNotifier() == a) {
					// something changed in the anchor
					if (a.getIncomingConnections().isEmpty() && a.getOutgoingConnections().isEmpty()) {
						// the anchor has no connections, so we need to delete it
						deleting = true;
						deleteConnectionPoint(shape);
						return;
					}
				}
				else 
				{
					ILocation loc = Graphiti.getPeService().getConnectionMidpoint(connection, midpoint);
					Graphiti.getGaService().setLocation(shape.getGraphicsAlgorithm(), loc.getX(), loc.getY());
				}
			}
		}
	}
	
	public static void deleteConnectionPoint(AnchorContainer shape) {
		if (isConnectionPoint(shape)) {
			Connection connection = getConnectionPointOwner(shape);
			if (connection!=null) {
				for (Adapter a : shape.eAdapters()) {
					if (a instanceof ConnectionPointShapeAdapter) {
						connection.eAdapters().remove(a);
					}
				}
				connection.getLink().getBusinessObjects().remove(shape);
			}
			Graphiti.getPeService().deletePictogramElement(shape);
		}
	}
	
	public static Shape createConnectionPoint(IFeatureProvider fp, Connection connection, ILocation location) {

		Shape connectionPointShape = null;

		Diagram diagram = fp.getDiagramTypeProvider().getDiagram();
		connectionPointShape = createConnectionPoint(location, diagram);
		fp.link(connectionPointShape, connection);
		connection.getLink().getBusinessObjects().add(connectionPointShape);
		BaseElement be = BusinessObjectUtil.getFirstBaseElement(connection);
		BPMNEdge bpmnEdge = DIUtils.findBPMNEdge(be);
		if (bpmnEdge!=null)
			fp.link(connectionPointShape, bpmnEdge);
		
		ConnectionPointShapeAdapter.adapt(connection, connectionPointShape);
		
		return connectionPointShape;
	}

	public static Shape createConnectionPoint(ILocation location, ContainerShape cs) {
		
		// create a circle for the connection point shape
		Shape connectionPointShape = createService.createShape(cs, true);
		peService.setPropertyValue(connectionPointShape, GraphitiConstants.CONNECTION_POINT_KEY, GraphitiConstants.CONNECTION_POINT);
		Ellipse ellipse = createService.createEllipse(connectionPointShape);
		int x = 0, y = 0;
		if (location != null) {
			x = location.getX();
			y = location.getY();
		}
		ellipse.setFilled(true);
		Diagram diagram = peService.getDiagramForPictogramElement(connectionPointShape);
		ellipse.setForeground(Graphiti.getGaService().manageColor(diagram, StyleUtil.CLASS_FOREGROUND));
		ellipse.setWidth(CONNECTION_POINT_SIZE);
		ellipse.setHeight(CONNECTION_POINT_SIZE);
		
		// create the anchor
		getConnectionPointAnchor(connectionPointShape);
		
		// set the location
		setConnectionPointLocation(connectionPointShape, x, y);
	
		return connectionPointShape;
	}
	
	public static FixPointAnchor getConnectionPointAnchor(Shape connectionPointShape) {
		if (connectionPointShape.getAnchors().size()==0) {
			FixPointAnchor anchor = createService.createFixPointAnchor(connectionPointShape);
			peService.setPropertyValue(anchor, GraphitiConstants.CONNECTION_POINT_KEY, GraphitiConstants.CONNECTION_POINT);
			
			// if the anchor doesn't have a GraphicsAlgorithm, GEF will throw a fit
			// so create an invisible rectangle for it
			createService.createInvisibleRectangle(anchor);
		}		
		return (FixPointAnchor)connectionPointShape.getAnchors().get(0);
	}

	public static ILocation getConnectionPointLocation(Shape connectionPointShape) {
		ILocation location = ShapeDecoratorUtil.peService.getLocationRelativeToDiagram(connectionPointShape);
		int x = location.getX() + CONNECTION_POINT_SIZE / 2;
		int y = location.getY() + CONNECTION_POINT_SIZE / 2;
		location.setX(x);
		location.setY(y);
		return location;
	}
	
	public static void setConnectionPointLocation(Shape connectionPointShape, int x, int y) {
		
		if (connectionPointShape.getAnchors().size()==0) {
			// anchor has not been created yet - need to set both location AND size
			layoutService.setLocationAndSize(
					connectionPointShape.getGraphicsAlgorithm(),
					x - CONNECTION_POINT_SIZE / 2, y - CONNECTION_POINT_SIZE / 2,
					CONNECTION_POINT_SIZE, CONNECTION_POINT_SIZE);
		}
		else {
			// already created - just set the location
			layoutService.setLocation(
					connectionPointShape.getGraphicsAlgorithm(),
					x - CONNECTION_POINT_SIZE / 2, y - CONNECTION_POINT_SIZE / 2);
		}
		
		FixPointAnchor anchor = getConnectionPointAnchor(connectionPointShape);
		anchor.setLocation( Graphiti.getCreateService().createPoint(CONNECTION_POINT_SIZE / 2,CONNECTION_POINT_SIZE / 2) );
		layoutService.setLocation(
				anchor.getGraphicsAlgorithm(), 
				CONNECTION_POINT_SIZE / 2,CONNECTION_POINT_SIZE / 2);
	}
	
	public static List<Shape> getConnectionPoints(Connection connection) {
		ArrayList<Shape> list = new ArrayList<Shape>();
		if (connection.getLink()!=null) {
			for (Object o : connection.getLink().getBusinessObjects()) {
				if (o instanceof Shape && isConnectionPoint((Shape)o)) {
					list.add((Shape)o);
				}
			}
		}
		return list;
	}

	public static boolean isConnectionPoint(PictogramElement pe) {
		if (pe!=null) {
			String value =peService.getPropertyValue(pe, GraphitiConstants.CONNECTION_POINT_KEY);
			return GraphitiConstants.CONNECTION_POINT.equals(value);
		}
		return false;
	}
	
	public static Connection getConnectionPointOwner(AnchorContainer connectionPointShape) {
		if (isConnectionPoint(connectionPointShape) && connectionPointShape.getLink()!=null) {
			for (Object o : connectionPointShape.getLink().getBusinessObjects()) {
				if (o instanceof Connection)
					return (Connection) o;
			}
		}
		return null;
	}

	public static AnchorLocation findNearestEdge(Shape shape, Point p) {
		if (true)
			return findNearestBoundaryAnchor(shape,p).locationType;
		
		AnchorLocation al = AnchorLocation.TOP;
		ILocation loc = peService.getLocationRelativeToDiagram(shape);
		IDimension size = GraphicsUtil.calculateSize(shape);
		int minDist = Integer.MAX_VALUE;
		int dist;
		
		if (loc.getX()<=p.getX() && p.getX()<=loc.getX() + size.getWidth()) {
			// Point lies between left & right edge of shape so nearest edge
			// is either the top or bottom edge
			if (p.getY()<=loc.getY() + size.getHeight()/2)
				return AnchorLocation.TOP;
			return AnchorLocation.BOTTOM;
		}
		if (loc.getY()<=p.getY() && p.getY()<=loc.getY() + size.getHeight()) {
			// Point lies between top & bottom edge of shape so nearest edge
			// is either the left or right edge
			if (p.getX()<=loc.getX() + size.getWidth()/2)
				return AnchorLocation.LEFT;
			return AnchorLocation.RIGHT;
		}
		
		// check top edge first:
		dist = Math.abs(p.getY() - loc.getY());
		if (dist < minDist) {
			minDist = dist;
			al = AnchorLocation.TOP;
		}
		// left edge:
		dist = Math.abs(p.getX() - loc.getX());
		if (dist < minDist) {
			minDist = dist;
			al = AnchorLocation.LEFT;
		}
		// bottom edge:
		dist = Math.abs(p.getY() - (loc.getY() + size.getHeight()));
		if (dist < minDist) {
			minDist = dist;
			al = AnchorLocation.BOTTOM;
		}
		// right edge:
		dist = Math.abs(p.getX() - (loc.getX() + size.getWidth()));
		if (dist < minDist) {
			minDist = dist;
			al = AnchorLocation.RIGHT;
		}
		return al;
	}
}