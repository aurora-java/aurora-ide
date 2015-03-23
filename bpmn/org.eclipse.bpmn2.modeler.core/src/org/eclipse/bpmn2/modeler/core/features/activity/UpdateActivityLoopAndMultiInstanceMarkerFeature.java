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
package org.eclipse.bpmn2.modeler.core.features.activity;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.LoopCharacteristics;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.StandardLoopCharacteristics;
import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.utils.ShapeDecoratorUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

// TODO: Auto-generated Javadoc
/**
 * The Class UpdateActivityLoopAndMultiInstanceMarkerFeature.
 */
public class UpdateActivityLoopAndMultiInstanceMarkerFeature extends AbstractUpdateMarkerFeature<Activity> {

	/**
	 * The Enum LoopCharacteristicType.
	 */
	enum LoopCharacteristicType {
		
		NULL("null"),  //$NON-NLS-1$
		LOOP(StandardLoopCharacteristics.class.getSimpleName()), 
		MULTI_PARALLEL(MultiInstanceLoopCharacteristics.class.getSimpleName() + ":parallel"),  //$NON-NLS-1$
		MULTI_SEQUENTIAL(MultiInstanceLoopCharacteristics.class.getSimpleName() + ":sequential"); //$NON-NLS-1$

		private String name;

		private LoopCharacteristicType(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}
	}

	/**
	 * Instantiates a new update activity loop and multi instance marker feature.
	 *
	 * @param fp the fp
	 */
	public UpdateActivityLoopAndMultiInstanceMarkerFeature(IFeatureProvider fp) {
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
	protected boolean isPropertyChanged(Activity activity, String propertyValue) {
		return !getLoopCharacteristicsValue(activity).getName().equals(propertyValue);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.features.activity.AbstractUpdateMarkerFeature#doUpdate(org.eclipse.bpmn2.FlowElement, org.eclipse.graphiti.mm.pictograms.ContainerShape)
	 */
	@Override
	protected void doUpdate(Activity activity, ContainerShape markerContainer) {
		switch (getLoopCharacteristicsValue(activity)) {
		case LOOP:
			ShapeDecoratorUtil.showActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
			break;
		case MULTI_PARALLEL:
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_STANDARD);
			ShapeDecoratorUtil.showActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_PARALLEL);
			ShapeDecoratorUtil.hideActivityMarker(markerContainer, GraphitiConstants.ACTIVITY_MARKER_LC_MULTI_SEQUENTIAL);
			break;
		case MULTI_SEQUENTIAL:
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
	protected String convertPropertyToString(Activity activity) {
		return getLoopCharacteristicsValue(activity).getName();
	}

	/**
	 * Gets the loop characteristics value.
	 *
	 * @param activity the activity
	 * @return the loop characteristics value
	 */
	private LoopCharacteristicType getLoopCharacteristicsValue(Activity activity) {
		LoopCharacteristics loopCharacteristics = activity.getLoopCharacteristics();
		LoopCharacteristicType type = LoopCharacteristicType.NULL;

		if (loopCharacteristics != null) {
			if (loopCharacteristics instanceof MultiInstanceLoopCharacteristics) {
				MultiInstanceLoopCharacteristics multi = (MultiInstanceLoopCharacteristics) loopCharacteristics;
				type = multi.isIsSequential() ? LoopCharacteristicType.MULTI_SEQUENTIAL
				        : LoopCharacteristicType.MULTI_PARALLEL;
			} else {
				type = LoopCharacteristicType.LOOP;
			}
		}

		return type;
	}
}