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
package org.eclipse.bpmn2.modeler.ui.features.activity.subprocess;


import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.activity.AbstractAddActivityFeature;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

public abstract class AddExpandableActivityFeature<T extends Activity>
	extends AbstractAddActivityFeature<T> {

	public AddExpandableActivityFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void decorateShape(IAddContext context, ContainerShape containerShape, T businessObject) {
		super.decorateShape(context, containerShape, businessObject);
		
		boolean isTriggeredByEvent = false;
		boolean isExpanded = true;
		
		if (businessObject instanceof SubProcess) {
			SubProcess subprocess = (SubProcess) businessObject;
			isTriggeredByEvent = subprocess.isTriggeredByEvent();

			BPMNShape bpmnShape = DIUtils.findBPMNShape(subprocess);
			if (bpmnShape != null) {
				isExpanded = bpmnShape.isIsExpanded();
			}
		}
		peService.setPropertyValue(containerShape, GraphitiConstants.TRIGGERED_BY_EVENT, Boolean.toString(isTriggeredByEvent));
		FeatureSupport.setElementExpanded(containerShape, isExpanded);
		
		if (!isExpanded){
			ShapeDecoratorUtil.showActivityMarker(containerShape, GraphitiConstants.ACTIVITY_MARKER_EXPAND);
			FeatureSupport.updateCollapsedSize(containerShape);
		}
		else {
			ShapeDecoratorUtil.hideActivityMarker(containerShape, GraphitiConstants.ACTIVITY_MARKER_EXPAND);
			FeatureSupport.updateExpandedSize(containerShape);
		}
	}
}