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
 * @author Innar Made
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.choreography;

import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

public class AddCallChoreographyFeature extends AddChoreographyActivityFeature<CallChoreography> {

	public AddCallChoreographyFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return super.canAdd(context)
				|| BusinessObjectUtil.containsElementOfType(context.getTargetContainer(), FlowElementsContainer.class);
	}

	@Override
	protected void decorateShape(IAddContext context, ContainerShape containerShape, CallChoreography businessObject) {
		super.decorateShape(context, containerShape, businessObject);
		RoundedRectangle rect = (RoundedRectangle)getGraphicsAlgorithm(containerShape);
		rect.setLineWidth(4);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
	 */
	@Override
	public Class getBusinessObjectType() {
		// TODO Auto-generated method stub
		return CallChoreography.class;
	}
}