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

package org.eclipse.bpmn2.modeler.core.features;

import org.eclipse.graphiti.features.IFeatureProvider;

/**
 * An interface that extends the standard Graphiti Feature Provider with a method to
 * fetch a Feature Container for a given BPMN2 model object class.
 * For each BPMN2 model class, there can be only one Feature Container that handles the
 * object's lifecycle.
 */
public interface IBpmn2FeatureProvider extends IFeatureProvider {
	
	/**
	 * Gets the feature container.
	 *
	 * @param bpmn2class the BPMN2 model object type
	 * @return the feature container
	 */
	public IFeatureContainer getFeatureContainer(Class bpmn2class);
}
