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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.core.runtime.Assert;

/**
 * Abstract base class for Connection Routers. This is a container for common utility functions
 * related to routing.
 */
public abstract class AbstractConnectionRouter implements IConnectionRouter {
	
	/** The Constant peService. */
	protected static final IPeService peService = Graphiti.getPeService();
	
	/** The Constant gaService. */
	protected static final IGaService gaService = Graphiti.getGaService();
	
	/**
	 * The connection routing directions.
	 */
	public enum Direction {
		 UP,
		 DOWN,
		 LEFT,
		 RIGHT,
		 NONE
	};

	/** The Feature Provider. */
	protected IFeatureProvider fp;

	/**
	 * Instantiates a new abstract connection router.
	 *
	 * @param fp the Feature Provider
	 */
	public AbstractConnectionRouter(IFeatureProvider fp) {
		this.fp = fp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IConnectionRouter#route(org.eclipse.graphiti.mm.pictograms.Connection)
	 */
	@Override
	public abstract boolean route(Connection connection);
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IConnectionRouter#dispose()
	 */
	@Override
	public abstract void dispose();
	
	/**
	 * Adds the routing info.
	 *
	 * @param connection the connection
	 * @param info the info
	 * @return the string
	 */
	public static String addRoutingInfo(Connection connection, String info) {
		Assert.isTrue(info!=null && !info.isEmpty());
		String newInfo = getRoutingInfo(connection);
		if (!newInfo.isEmpty())
			newInfo += ","; //$NON-NLS-1$
		newInfo += info;
		
		peService.setPropertyValue(connection, ROUTING_INFO, newInfo);
		return newInfo;
	}
	
	/**
	 * Removes the routing info.
	 *
	 * @param connection the connection
	 */
	public static void removeRoutingInfo(Connection connection) {
		removeRoutingInfo(connection, getRoutingInfo(connection));
	}
	
	/**
	 * Removes the routing info.
	 *
	 * @param connection the connection
	 * @param info the info
	 * @return the string
	 */
	public static String removeRoutingInfo(Connection connection, String info) {
		String newInfo = null;
		if (info!=null && !info.isEmpty()) {
			newInfo = getRoutingInfo(connection);
			if (newInfo!=null && !newInfo.isEmpty()) {
				String a[] = newInfo.split(","); //$NON-NLS-1$
				String b[] = info.split(","); //$NON-NLS-1$
				for (int i=0; i<a.length; ++i) {
					for (String sb : b) {
						if (a[i].startsWith(sb)) {
							a[i] = null;
							break;
						}
					}
				}
				newInfo = ""; //$NON-NLS-1$
				for (int i=0; i<a.length; ++i) {
					if (a[i]!=null && !a[i].isEmpty()) {
						if (!newInfo.isEmpty())
							newInfo += ","; //$NON-NLS-1$
						newInfo += a[i];
					}
				}
			}
		}
		if (newInfo==null || newInfo.isEmpty())
			peService.removeProperty(connection, ROUTING_INFO);
		else
			peService.setPropertyValue(connection, ROUTING_INFO, newInfo);
		return newInfo;
	}
	
	/**
	 * Gets the routing info.
	 *
	 * @param connection the connection
	 * @return the routing info
	 */
	public static String getRoutingInfo(Connection connection) {
		String info = peService.getPropertyValue(connection, ROUTING_INFO);
		if (info==null || info.isEmpty())
			return ""; //$NON-NLS-1$
		return info;
	}

	/**
	 * Sets the routing info.
	 *
	 * @param connection the connection
	 * @param info the info
	 * @param value the value
	 * @return the string
	 */
	public static String setRoutingInfoInt(Connection connection, String info, int value) {
		removeRoutingInfo(connection, info+"="); //$NON-NLS-1$
		return addRoutingInfo(connection, info+"="+value); //$NON-NLS-1$
	}

	/**
	 * Gets the routing info.
	 *
	 * @param connection the connection
	 * @param info the info
	 * @return the routing info
	 */
	public static int getRoutingInfoInt(Connection connection, String info) {
		String oldInfo = getRoutingInfo(connection);
		String a[] = oldInfo.split(","); //$NON-NLS-1$
		for (String s : a) {
			if (oldInfo.startsWith(info+"=")) { //$NON-NLS-1$
				try {
					String b[] = s.split("="); //$NON-NLS-1$
					return Integer.parseInt(b[1]);
				}
				catch (Exception e) {
				}
			}
		}
		return -1;
	}

	/**
	 * Sets the force routing.
	 *
	 * @param connection the connection
	 * @param force the force
	 */
	public static void setForceRouting(Connection connection, boolean force) {
		if (force)
			addRoutingInfo(connection, ROUTING_INFO_FORCE);
		else
			removeRoutingInfo(connection, ROUTING_INFO_FORCE);
	}
	
	/**
	 * Force routing.
	 *
	 * @param connection the connection
	 * @return true, if successful
	 */
	public static boolean forceRouting(Connection connection) {
		return getRoutingInfo(connection).contains(ROUTING_INFO_FORCE);
	}
}
