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

import org.eclipse.bpmn2.modeler.core.runtime.CustomTaskDescriptor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.custom.ICustomFeature;

// TODO: Auto-generated Javadoc
/**
 * The Interface ICustomElementFeatureContainer.
 */
public interface ICustomElementFeatureContainer extends IFeatureContainer {

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public abstract void setId(String id);

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public abstract String getId();

	/**
	 * Sets the custom task descriptor.
	 *
	 * @param customTaskDescriptor the new custom task descriptor
	 */
	public abstract void setCustomTaskDescriptor(CustomTaskDescriptor customTaskDescriptor);

	/**
	 * Return a Custom Task ID from inspection of the given model object. If the object
	 * is not a custom task, return null.
	 * 
	 * @param object - the model object to inspect
	 * @return a Custom Task ID string or null
	 */
	// FIXME: change parameter type to Object. This will allow the Target Runtime extension
	// to handle DND objects from Project Explorer because AddContext.getNewObject() will
	// be an IFile or IType or something other than EObject. By inspecting the NewObject,
	// the extension plugin can decide whether it wants to handle DND of different types
	// of files onto the canvas.
	public abstract String getId(EObject object);

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	public abstract ICustomFeature[] getCustomFeatures(IFeatureProvider fp);
}