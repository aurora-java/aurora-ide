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
 * @author Bob Brodt
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.AnchorLocation;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil.BoundaryAnchor;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;

/**
 * Router for connections that can have user-settable bendpoints.
 */
public class BendpointConnectionRouter extends DefaultConnectionRouter {

	/** The minimum distance between a bendpoint and a shape when rerouting to avoid collisions. */
	protected static final int margin = 10;
	/** The connection, must be a {@code FreeFormConnection}. */
	protected FreeFormConnection ffc;
	/** The moved or added bendpoint (if any). */
	protected Point movedBendpoint;
	/** The removed bendpoint. */
	protected Point removedBendpoint;
	/** The list of old connection cuts (including the end cuts) for determining if a route has changed */
	protected List<Point> oldPoints;
	/** flag to disable automatic collision avoidance and optimization. */
	protected boolean manual = true;
	
	/**
	 * Instantiates a new bendpoint connection router.
	 *
	 * @param fp the Feature Provider
	 */
	public BendpointConnectionRouter(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.DefaultConnectionRouter#route(org.eclipse.graphiti.mm.pictograms.Connection)
	 */
	@Override
	public boolean route(Connection connection) {
		super.route(connection);
		
		if (connection instanceof FreeFormConnection)
			ffc = (FreeFormConnection)connection;
		else
			return false;
		
		initialize();
		ConnectionRoute route = calculateRoute();
		boolean changed = isRouteChanged(route);
		if (changed) {
			applyRoute(route);
		}
		dispose();

		return changed;
	}
	
	/**
	 * Sets the manual routing.
	 *
	 * @param manual the new manual routing
	 */
	public void setManualRouting(boolean manual) {
		this.manual = manual;
	}
	
	/**
	 * Checks if is manual routing.
	 *
	 * @return true, if is manual routing
	 */
	public boolean isManualRouting() {
		return manual;
	}
	
	/**
	 * Initialize the newPoints list and set the new start and end anchors.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		
		movedBendpoint = getMovedBendpoint(ffc);
		if (movedBendpoint==null)
			movedBendpoint = getAddedBendpoint(ffc);
		removedBendpoint = getRemovedBendpoint(ffc);
	
		/**
		 * Save the connection's start/end anchors, and their locations as well as
		 * the bendpoints. This is used to compare against the new ConnectionRoute
		 */
		oldPoints = new ArrayList<Point>();
		oldPoints.add(GraphicsUtil.createPoint(ffc.getStart()));
		for (Point p : ffc.getBendpoints()) {
			oldPoints.add(GraphicsUtil.createPoint(p));
		}
		oldPoints.add(GraphicsUtil.createPoint(ffc.getEnd()));
	}
	
	/**
	 * Calculate route.
	 *
	 * @return the connection route
	 */
	protected ConnectionRoute calculateRoute() {
		if (isSelfConnection()) {
			return calculateSelfConnectionRoute();
		}

		ConnectionRoute route = new ConnectionRoute(this, 1, source, target);
		
		Point pStart;
		Point pEnd;
		if (sourceAnchor==null) {
			BoundaryAnchor ba = AnchorUtil.findNearestBoundaryAnchor(source, oldPoints.get(1));
			pStart = GraphicsUtil.createPoint(ba.anchor);
		}
		else {
			// can't move the original AdHoc anchor - this is our starting point
			pStart = oldPoints.get(0);
		}
		if (targetAnchor==null) {
			BoundaryAnchor ba = AnchorUtil.findNearestBoundaryAnchor(target, oldPoints.get(oldPoints.size()-2));
			pEnd = GraphicsUtil.createPoint(ba.anchor);
		}
		else {
			// can't move the original target AdHoc anchor - this is our end 
			pEnd = oldPoints.get( oldPoints.size()-1 );
		}

		route.add(pStart);
		if (!manual) {
			oldPoints.clear();
			oldPoints.add(pStart);
			if (movedBendpoint!=null)
				oldPoints.add(movedBendpoint);
			oldPoints.add(pEnd);
		}
		
		Point p1 = pStart;
		Point p2;
		for (int i=1; i<oldPoints.size() - 1; ++i) {
			p2 = oldPoints.get(i);
			ContainerShape shape = getCollision(p1,p2);
			if (shape!=null && !manual) {
				if (shape==target) {
					// find a better target anchor if possible
					if (targetAnchor==null) {
						BoundaryAnchor ba = AnchorUtil.findNearestBoundaryAnchor(target, p1);
						pEnd = GraphicsUtil.createPoint(ba.anchor);
					}
					// and we're done
					break;
				}
				// navigate around this shape
				DetourPoints detour = new DetourPoints(shape, margin);
				for (Point d : detour.calculateDetour(p1, p2)) {
					if (!route.add(d)) {
						++i;
						break;
					}
					p2 = d;
				}
				--i;
			}
			else
				route.add(p2);
			p1 = p2;
		}

		route.add(pEnd);
		
		oldPoints.clear();
		
		return route;
	}
	
	/**
	 * Route connections whose source and target are the same. This only
	 * reroutes the connection if there are currently no bendpoints in the
	 * connection - we don't want to reroute a connection that may have already
	 * been manually rerouted.
	 *
	 * @return true if the router has done any work
	 */
	protected ConnectionRoute calculateSelfConnectionRoute() {
		if (!isSelfConnection())
			return null;
		
		if (movedBendpoint==null) {
			if (ffc.getStart() != ffc.getEnd() && ffc.getBendpoints().size()>0) {
				// this connection starts and ends at the same node but it has different
				// anchor cuts and at least one bendpoint, which makes it likely that
				// this connection was already routed previously and the self-connection
				// is how the user wants it. But, check if the user wants to force routing.
				if (!forceRouting(ffc))
					return null;
			}
		}
		
		Map<AnchorLocation, BoundaryAnchor> targetBoundaryAnchors = AnchorUtil.getBoundaryAnchors(target);
		BoundaryAnchor targetTop = targetBoundaryAnchors.get(AnchorLocation.TOP);
		BoundaryAnchor targetRight = targetBoundaryAnchors.get(AnchorLocation.RIGHT);

		// create the bendpoints that loop the connection around the top-right corner of the figure
		ILocation loc = peService.getLocationRelativeToDiagram((Shape)target);
		IDimension size = GraphicsUtil.calculateSize(target);
		int x1 = loc.getX() + size.getWidth() + 20;
		int y1 = loc.getY() + size.getHeight() / 2;
		int x2 = loc.getX() + size.getWidth() / 2;
		int y2 = loc.getY() - 20;
		Point right = gaService.createPoint(x1, y1); // the point to the right of the node
		Point corner = gaService.createPoint(x1, y2); // point above the top-right corner 
		Point top = gaService.createPoint(x2, y2); // point above the node
		
		// adjust these cuts to the moved or added bendpoint if possible
		Point p = movedBendpoint;
		if (p!=null) {
			int x = p.getX();
			int y = p.getY();
			if (x > loc.getX() + size.getWidth() + 2) {
				right.setX(x);
				corner.setX(x);
			}
			if (y < loc.getY() - 2) {
				top.setY(y);
				corner.setY(y);
			}
		}

		// and add them to the new Route
		ConnectionRoute route = new ConnectionRoute(this,1,source,target);
		route.add(GraphicsUtil.createPoint(targetRight.anchor));
		route.add(right);
		route.add(corner);
		route.add(top);
		route.add(GraphicsUtil.createPoint(targetTop.anchor));

		return route;
	}
	
	/**
	 * Compare the connection's original start/end locations and all of its
	 * bendpoints with the newly calculated route.
	 *
	 * @param route the route
	 * @return true if the connection is different from the newly calculated
	 *         route
	 */
	protected boolean isRouteChanged(ConnectionRoute route) {
		if (route==null || route.size()==0)
			return false;
		if (oldPoints.size()!=route.size()) {
			return true;
		}
		for (int i=0; i<oldPoints.size(); ++i) {
			Point p1 = oldPoints.get(i);
			Point p2 = route.get(i);
			if (!GraphicsUtil.pointsEqual(p1, p2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the connection's new start/end point anchors and the newly calculated
	 * bendpoints.
	 *
	 * @param route the route
	 */
	protected void applyRoute(ConnectionRoute route) {
		route.apply(ffc, sourceAnchor, targetAnchor);
		DIUtils.updateDIEdge(ffc);
	}

	/**
	 * Gets the detour points.
	 *
	 * @param shape the shape
	 * @return the detour points
	 */
	protected DetourPoints getDetourPoints(ContainerShape shape) {
		DetourPoints detour = new DetourPoints(shape, margin);
//		if (allShapes==null)
//			findAllShapes();
//
//		for (int i=0; i<allShapes.size(); ++i) {
//			ContainerShape s = allShapes.get(i);
//			if (shape==s)
//				continue;
//			DetourPoints d = new DetourPoints(s, margin);
//			if (detour.intersects(d) && !detour.contains(d)) {
//				detour.merge(d);
//				i = -1;
//			}
//		}

		return detour;
	}

	/**
	 * Set a property in the given FreeFormConnection that represents the index
	 * of an existing bendpoint that has been moved by the user. This bendpoint
	 * is taken into consideration in the new routing calculations.
	 * 
	 * @param connection - FreeFormConnection to check
	 * @param index - index of a bendpoint. If this value is out of range, the
	 *            property will be remmoved from the connection
	 */
	public static void setMovedBendpoint(Connection connection, int index) {
		setInterestingBendpoint(connection, "moved.", index); //$NON-NLS-1$
	}

	/**
	 * Sets the added bendpoint.
	 *
	 * @param connection the connection
	 * @param index the index
	 */
	public static void setAddedBendpoint(Connection connection, int index) {
		setInterestingBendpoint(connection, "added.", index); //$NON-NLS-1$
	}

	/**
	 * Sets the removed bendpoint.
	 *
	 * @param connection the connection
	 * @param index the index
	 */
	public static void setRemovedBendpoint(Connection connection, int index) {
		setInterestingBendpoint(connection, "removed.", index); //$NON-NLS-1$
	}

	/**
	 * Sets the fixed bendpoint.
	 *
	 * @param connection the connection
	 * @param index the index
	 */
	public static void setFixedBendpoint(Connection connection, int index) {
		setInterestingBendpoint(connection, "fixed."+index+".", index); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Sets the interesting bendpoint.
	 *
	 * @param connection the connection
	 * @param type the type
	 * @param index the index
	 */
	protected static void setInterestingBendpoint(Connection connection, String type, int index) {
		if (connection instanceof FreeFormConnection) {
			int size = ((FreeFormConnection)connection).getBendpoints().size();
			if (index>=0 && size>0) {
				if (index>=size)
					index = size-1;
				AbstractConnectionRouter.setRoutingInfoInt(connection, type+ROUTING_INFO_BENDPOINT, index);
			}
			else
				AbstractConnectionRouter.removeRoutingInfo(connection, type+ROUTING_INFO_BENDPOINT);
		}
	}

	/**
	 * Return the "moved bendpoint" property that was previously set in the
	 * FreeFormConnection by setMovedBendpoint()
	 * 
	 * @param connection - FreeFormConnection to check
	 * @return a Graphiti Point in Diagram-relative coordinates, or null if the
	 *         property is not set
	 */
	public static Point getMovedBendpoint(Connection connection) {
		return getInterestingBendpoint(connection, "moved."); //$NON-NLS-1$
	}
	
	/**
	 * Gets the added bendpoint.
	 *
	 * @param connection the connection
	 * @return the added bendpoint
	 */
	public static Point getAddedBendpoint(Connection connection) {
		return getInterestingBendpoint(connection, "added."); //$NON-NLS-1$
	}
	
	/**
	 * Gets the removed bendpoint.
	 *
	 * @param connection the connection
	 * @return the removed bendpoint
	 */
	public static Point getRemovedBendpoint(Connection connection) {
		return getInterestingBendpoint(connection, "removed."); //$NON-NLS-1$
	}
	
	/**
	 * Gets the fixed bendpoint.
	 *
	 * @param connection the connection
	 * @param index the index
	 * @return the fixed bendpoint
	 */
	public static Point getFixedBendpoint(Connection connection, int index) {
		return getInterestingBendpoint(connection, "fixed."+index+"."); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Gets the interesting bendpoint.
	 *
	 * @param connection the connection
	 * @param type the type
	 * @return the interesting bendpoint
	 */
	protected static Point getInterestingBendpoint(Connection connection, String type) {
		try {
			int index = AbstractConnectionRouter.getRoutingInfoInt(connection, type+ROUTING_INFO_BENDPOINT);
			return ((FreeFormConnection)connection).getBendpoints().get(index);
		}
		catch (Exception e) {
		}
		return null;
	}
}
