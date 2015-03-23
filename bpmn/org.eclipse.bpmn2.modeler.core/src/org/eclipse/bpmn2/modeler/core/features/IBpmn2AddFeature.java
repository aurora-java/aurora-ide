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
import org.eclipse.graphiti.IExecutionInfo;
import org.eclipse.graphiti.features.context.IAddContext;

// TODO: Auto-generated Javadoc
/**
 * The Interface IBpmn2AddFeature.
 *
 * @param <T> the generic type
 */
public interface IBpmn2AddFeature<T extends EObject> {

	/**
	 * Gets the business object.
	 *
	 * @param context the context
	 * @return the business object
	 */
	public T getBusinessObject(IAddContext context);
	
	/**
	 * Put business object.
	 *
	 * @param context the context
	 * @param businessObject the business object
	 */
	public void putBusinessObject(IAddContext context, T businessObject);
	
	/**
	 * Post execute.
	 *
	 * @param executionInfo the execution info
	 */
	public void postExecute(IExecutionInfo executionInfo);
}
