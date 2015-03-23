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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.modeler.core.features.label.RemoveLabelFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;

/**
 * This is the Graphiti FeatureContainer class for all BPMN2 model shape
 * elements that subclass {@link BaseElement}.
 */
public abstract class BaseElementFeatureContainer implements IShapeFeatureContainer {

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getApplyObject(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public Object getApplyObject(IContext context) {
		return BusinessObjectUtil.getBusinessObject(context, BaseElement.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#canApplyTo(java.lang.Object)
	 */
	@Override
	public boolean canApplyTo(Object o) {
		return o instanceof BaseElement;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getRemoveFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IRemoveFeature getRemoveFeature(IFeatureProvider fp) {
		return new RemoveLabelFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getDirectEditingFeature(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return new DirectEditBaseElementFeature(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#getCustomFeatures(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	@Override
	public ICustomFeature[] getCustomFeatures(IFeatureProvider fp) {
		return new ICustomFeature[] {
			new ShowDocumentationFeature(fp),
			new ShowPropertiesFeature(fp)
		};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.IFeatureContainer#isAvailable(org.eclipse.graphiti.features.IFeatureProvider)
	 */
	public boolean isAvailable(IFeatureProvider fp) {
		return true;
	}
}