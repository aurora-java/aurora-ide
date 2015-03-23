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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.services.Graphiti;

// TODO: Auto-generated Javadoc
/**
 * The Class PropertyBasedFeatureContainer.
 */
public abstract class PropertyBasedFeatureContainer implements IShapeFeatureContainer, IConnectionFeatureContainer {

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getApplyObject(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public Object getApplyObject(IContext context) {
		if (context instanceof IPictogramElementContext) {
			return ((IPictogramElementContext) context).getPictogramElement();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#canApplyTo(java.lang.Object)
	 */
	@Override
	public boolean canApplyTo(Object o) {
		if (!(o instanceof PropertyContainer)) {
			return false;
		}
		String property = Graphiti.getPeService().getPropertyValue((PropertyContainer) o, getPropertyKey());
		if (property == null) {
			return false;
		}
		return canApplyToProperty(property);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#isAvailable(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public boolean isAvailable(IFeatureProvider fp) {
		return true;
	}

	/**
	 * Gets the property key.
	 *
	 * @return the property key
	 */
	protected abstract String getPropertyKey();

	/**
	 * Can apply to property.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	protected abstract boolean canApplyToProperty(String value);

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getRemoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return null;
	}
}