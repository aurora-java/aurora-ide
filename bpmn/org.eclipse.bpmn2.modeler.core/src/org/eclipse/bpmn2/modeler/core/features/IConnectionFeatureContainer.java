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
package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;

// TODO: Auto-generated Javadoc
/**
 * The Interface IConnectionFeatureContainer.
 */
public interface IConnectionFeatureContainer extends IFeatureContainer {

	/**
	 * Gets the creates the connection feature.
	 *
	 * @param fp the fp
	 * @return the creates the connection feature
	 */
	ICreateConnectionFeature getCreateConnectionFeature(IFeatureProvider fp);
	
	/**
	 * Gets the reconnection feature.
	 *
	 * @param fp the fp
	 * @return the reconnection feature
	 */
	IReconnectionFeature getReconnectionFeature(IFeatureProvider fp);
	
	/**
	 * Gets the Connection Decorator move feature. This is used to move labels
	 * attached as Decorators on a Connection.
	 * 
	 * @param fp
	 * @return
	 */
	IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IFeatureProvider fp);
}