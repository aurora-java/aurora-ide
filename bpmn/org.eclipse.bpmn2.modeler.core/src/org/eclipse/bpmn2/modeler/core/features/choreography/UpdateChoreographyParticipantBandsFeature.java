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

package org.eclipse.bpmn2.modeler.core.features.choreography;

import java.util.List;

import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2UpdateFeature;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 *
 */
public class UpdateChoreographyParticipantBandsFeature extends AbstractBpmn2UpdateFeature {

	IUpdateFeature updateFeature;
	
	/**
	 * @param fp
	 */
	public UpdateChoreographyParticipantBandsFeature(IFeatureProvider fp) {
		super(fp);
		
		updateFeature = new UpdateLabelFeature(getFeatureProvider()) {
			
			@Override
			protected LabelPosition getLabelPosition(AbstractText text) {
				return LabelPosition.CENTER;
			}

			@Override
			protected ContainerShape getTargetContainer(PictogramElement ownerPE) {
				return (ContainerShape) ownerPE;
			}
		};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IUpdate#canUpdate(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	@Override
	public boolean canUpdate(IUpdateContext context) {
		return context.getPictogramElement() instanceof ContainerShape;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		IReason reason = super.updateNeeded(context);
		if (reason.toBoolean())
			return reason;
		
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		List<ContainerShape> bandShapes = FeatureSupport.getParticipantBandContainerShapes(containerShape);
		for (ContainerShape s : bandShapes) {
			IUpdateContext newContext = new UpdateContext(s);
			if (updateFeature.updateNeeded(newContext).toBoolean())
				return Reason.createTrueReason("Particpant Name Changed");
		}
		return Reason.createFalseReason();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IUpdate#update(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	@Override
	public boolean update(IUpdateContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		List<ContainerShape> bandShapes = FeatureSupport.getParticipantBandContainerShapes(containerShape);
		for (ContainerShape s : bandShapes) {
			IUpdateContext newContext = new UpdateContext(s);
			updateFeature.update(newContext);
		}
		return true;
	}

}
