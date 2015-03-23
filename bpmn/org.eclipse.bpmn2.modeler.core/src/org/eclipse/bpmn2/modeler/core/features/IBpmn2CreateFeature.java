/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.context.IContext;

// TODO: Auto-generated Javadoc
/**
 * The Interface IBpmn2CreateFeature.
 *
 * @param <T> the generic type
 * @param <C> the generic type
 */
public interface IBpmn2CreateFeature<T extends EObject, C extends IContext> {

	/**
	 * Creates the business object.
	 *
	 * @param context the context
	 * @return the t
	 */
	public T createBusinessObject(C context);
	
	/**
	 * Gets the business object.
	 *
	 * @param context the context
	 * @return the business object
	 */
	public T getBusinessObject(C context);
	
	/**
	 * Set the business object into the Graphiti context.
	 *
	 * @param context the Graphiti context
	 * @param businessObject the business object
	 */
	public void putBusinessObject(C context, T businessObject);
	
	/**
	 * Gets the business object class. This is the BPMN2 model object EClass.
	 *
	 * @return the business object class
	 */
	public EClass getBusinessObjectClass();
	
	/**
	 * Gets the BPMN2 model object class that the Feature Provider uses to map
	 * Feature Containers. This is usually the same as the Business Object class
	 * (except for example {@see CallGlobalTaskFeatureContainer}).
	 *
	 * @return the business object class
	 */
	public EClass getFeatureClass();
	
	/**
	 * Post execute.
	 *
	 * @param executionInfo the execution info
	 */
	public void postExecute(IExecutionInfo executionInfo);
}
