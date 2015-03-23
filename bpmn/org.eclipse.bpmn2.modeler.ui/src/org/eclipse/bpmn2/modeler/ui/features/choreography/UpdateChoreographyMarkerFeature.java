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

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

public class UpdateChoreographyMarkerFeature extends AbstractUpdateMarkerFeature<ChoreographyActivity> {

	public UpdateChoreographyMarkerFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#getPropertyKey()
	 */
	@Override
	protected String getPropertyKey() {
		return GraphitiConstants.IS_LOOP_OR_MULTI_INSTANCE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#isPropertyChanged(org.eclipse.bpmn2.FlowElement, java.lang.String)
	 */
	@Override
	protected boolean isPropertyChanged(ChoreographyActivity element, String propertyValue) {
		return !convertPropertyToString(element).equals(propertyValue);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#doUpdate(org.eclipse.bpmn2.FlowElement, org.eclipse.graphiti.mm.pictograms.ContainerShape)
	 */
	@Override
	protected void doUpdate(ChoreographyActivity element, ContainerShape markerContainer) {
		switch (element.getLoopType()) {
		case STANDARD:
			ShapeDecoratorUtil.showActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
			break;
		case MULTI_INSTANCE_PARALLEL:
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);
			ShapeDecoratorUtil.showActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
			break;
		case MULTI_INSTANCE_SEQUENTIAL:
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
			ShapeDecoratorUtil.showActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
			break;
		default:
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#convertPropertyToString(org.eclipse.bpmn2.FlowElement)
	 */
	@Override
	protected String convertPropertyToString(ChoreographyActivity element) {
		String type = element.getLoopType().getName();
		if (type==null)
			return "";
		return type;
	}

}