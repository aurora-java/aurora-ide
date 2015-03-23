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
package org.eclipse.bpmn2.modeler.ui.features.callactivity;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.bpmn2.modeler.ui.features.activity.subprocess.AddExpandableActivityFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;


public abstract class AbstractCallGlobalTaskFeatureContainer<T extends GlobalTask> extends CallActivityFeatureContainer {
	
	@Override
	public abstract ICreateFeature getCreateFeature(IFeatureProvider fp);

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddCallGlobalTaskFeature(fp);
	}

	public static class AddCallGlobalTaskFeature extends AddExpandableActivityFeature<CallActivity> {

		/**
		 * @param fp
		 */
		public AddCallGlobalTaskFeature(IFeatureProvider fp) {
			super(fp);
		}
		@Override
		protected void decorateShape(IAddContext context, ContainerShape containerShape, CallActivity businessObject) {
			super.decorateShape(context, containerShape, businessObject);
			Graphiti.getPeService().setPropertyValue(containerShape, CALL_ACTIVITY_REF_PROPERTY,
					getCallableElementStringValue(businessObject.getCalledElementRef()));
			RoundedRectangle rect = (RoundedRectangle)getGraphicsAlgorithm(containerShape);
			rect.setLineWidth(4);
		}

		@Override
		protected int getMarkerContainerOffset() {
			return MARKER_OFFSET;
		}
		/* (non-Javadoc)
		 * @see org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2AddFeature#getBusinessObjectType()
		 */
		@Override
		public Class getBusinessObjectType() {
			return CallActivity.class;
		}
	}

}