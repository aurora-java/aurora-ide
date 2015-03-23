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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CallChoreography;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.modeler.core.features.label.UpdateLabelFeature;
import org.eclipse.bpmn2.modeler.core.preferences.ShapeStyle.LabelPosition;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class UpdateChoreographyLabelFeature extends UpdateLabelFeature {

	/**
	 * @param fp
	 */
	public UpdateChoreographyLabelFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	protected LabelPosition getHorizontalLabelPosition(AbstractText text) {
		PictogramElement pe = FeatureSupport.getLabelOwner(text);
		Object bo = getBusinessObjectForPictogramElement(pe);
		if ((bo instanceof SubChoreography || bo instanceof CallChoreography) &&
				FeatureSupport.isElementExpanded((BaseElement)bo)) {
			return LabelPosition.LEFT;
		}
		return LabelPosition.CENTER;
	}
	
	protected LabelPosition getVerticalLabelPosition(AbstractText text) {
		PictogramElement pe = FeatureSupport.getLabelOwner(text);
		Object bo = getBusinessObjectForPictogramElement(pe);
		if ((bo instanceof SubChoreography || bo instanceof CallChoreography) &&
				FeatureSupport.isElementExpanded((BaseElement)bo)) {
			return LabelPosition.TOP;
		}
		return LabelPosition.CENTER;
	}

	@Override
	protected Rectangle getLabelBounds(PictogramElement pe, boolean isAddingLabel, Point offset) {
		Rectangle bounds = super.getLabelBounds(pe, isAddingLabel, offset);
		if (!isAddingLabel) {
			Object bo = getBusinessObjectForPictogramElement(pe);
			if ((bo instanceof SubChoreography || bo instanceof CallChoreography) &&
					FeatureSupport.isElementExpanded((BaseElement)bo)) {
				// This shape is expanded, so the label will appear at the top-left
				// corner of the shape. Adjust the vertical position so that the label
				// is just below the top Participant Band(s).
				ContainerShape choreographyActivityShape = (ContainerShape) FeatureSupport.getLabelOwner(pe);
				List<ContainerShape> bandShapes = FeatureSupport.getParticipantBandContainerShapes(choreographyActivityShape);			
				List<ContainerShape> topBandShapes = FeatureSupport.getTopAndBottomBands(bandShapes).getFirst();
				int dy = 0;
				for (ContainerShape s : topBandShapes) {
					dy += Graphiti.getGaLayoutService().calculateSize(s.getGraphicsAlgorithm()).getHeight();
				}
				bounds.setY(bounds.y + dy);
			}
		}
		return bounds;
	}

	@Override
	protected ContainerShape getTargetContainer(PictogramElement ownerPE) {
		return (ContainerShape) ownerPE;
	}
}