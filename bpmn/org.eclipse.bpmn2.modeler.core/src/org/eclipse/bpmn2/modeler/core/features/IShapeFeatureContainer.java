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

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;

// TODO: Auto-generated Javadoc
/**
 * The Interface IShapeFeatureContainer.
 */
public interface IShapeFeatureContainer extends IFeatureContainer {

	/**
	 * Gets the creates the feature.
	 *
	 * @param fp the fp
	 * @return the creates the feature
	 */
	ICreateFeature getCreateFeature(IFeatureProvider fp);
	
	/**
	 * Gets the move feature.
	 *
	 * @param fp the fp
	 * @return the move feature
	 */
	IMoveShapeFeature getMoveFeature(IFeatureProvider fp);
	
	/**
	 * Gets the resize feature.
	 *
	 * @param fp the fp
	 * @return the resize feature
	 */
	IResizeShapeFeature getResizeFeature(IFeatureProvider fp);
}