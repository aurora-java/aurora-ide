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

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;

// TODO: Auto-generated Javadoc
/**
 * The Interface FeatureResolver.
 */
public interface FeatureResolver {

	/**
	 * Gets the creates the connection features.
	 *
	 * @param fp the fp
	 * @return the creates the connection features
	 */
	List<ICreateConnectionFeature> getCreateConnectionFeatures(IFeatureProvider fp);

	/**
	 * Gets the creates the features.
	 *
	 * @param fp the fp
	 * @return the creates the features
	 */
	List<ICreateFeature> getCreateFeatures(IFeatureProvider fp);

	/**
	 * Gets the adds the feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the adds the feature
	 */
	IAddFeature getAddFeature(IFeatureProvider fp, BaseElement e);

	/**
	 * Gets the direct editing feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the direct editing feature
	 */
	IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp, BaseElement e);

	/**
	 * Gets the layout feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the layout feature
	 */
	ILayoutFeature getLayoutFeature(IFeatureProvider fp, BaseElement e);

	/**
	 * Gets the update feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the update feature
	 */
	IUpdateFeature getUpdateFeature(IFeatureProvider fp, BaseElement e);

	/**
	 * Gets the move feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the move feature
	 */
	IMoveShapeFeature getMoveFeature(IFeatureProvider fp, BaseElement e);

	/**
	 * Gets the resize feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the resize feature
	 */
	IResizeShapeFeature getResizeFeature(IFeatureProvider fp, BaseElement e);

	/**
	 * Gets the delete feature.
	 *
	 * @param fp the fp
	 * @param e the e
	 * @return the delete feature
	 */
	IDeleteFeature getDeleteFeature(IFeatureProvider fp, BaseElement e);
}