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
package org.eclipse.bpmn2.modeler.core.features.choreography;

import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.ParticipantBandKind;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.IColorConstant;

public class UpdateChoreographyInitiatingParticipantFeature extends AbstractUpdateBaseElementFeature<ChoreographyActivity> {

	private final IPeService peService = Graphiti.getPeService();

	public UpdateChoreographyInitiatingParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape))
			return Reason.createFalseReason();

		ChoreographyActivity choreography = BusinessObjectUtil.getFirstElementOfType(pe, ChoreographyActivity.class);
		if (choreography==null)
			return Reason.createFalseReason();

		String id = peService.getPropertyValue(context.getPictogramElement(), ChoreographyUtil.INITIATING_PARTICIPANT_REF);
		Participant participant = choreography.getInitiatingParticipantRef();

		if ((id == null || id.equals("null")) && participant == null) { //$NON-NLS-1$
			return Reason.createFalseReason();
		}

		if (participant != null && (id != null && id.equals(participant.getId()))) {
			return Reason.createFalseReason();
		}

		return Reason.createTrueReason("Initiating Participant");
	}

	@Override
	public boolean update(IUpdateContext context) {

		ContainerShape container = (ContainerShape) context.getPictogramElement();

		ChoreographyActivity choreography = BusinessObjectUtil.getFirstElementOfType(container,
				ChoreographyActivity.class);

		boolean hasInitiatingParticipant = choreography.getInitiatingParticipantRef() != null;

		for (ContainerShape band : FeatureSupport.getParticipantBandContainerShapes(container)) {
			Participant participant = BusinessObjectUtil.getFirstElementOfType(band, Participant.class);
			boolean isInitiating = hasInitiatingParticipant
					&& participant.equals(choreography.getInitiatingParticipantRef());
			Color color = manageColor(isInitiating ? IColorConstant.WHITE : IColorConstant.LIGHT_GRAY);
			band.getGraphicsAlgorithm().setBackground(color);
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(band, BPMNShape.class);
			bpmnShape.setParticipantBandKind(getParticipantBandKind(isInitiating, bpmnShape.getParticipantBandKind()));
		}

		Participant initiatingParticipant = choreography.getInitiatingParticipantRef();
		String id = initiatingParticipant == null ? "null" : initiatingParticipant.getId(); //$NON-NLS-1$
		peService.setPropertyValue(context.getPictogramElement(), ChoreographyUtil.INITIATING_PARTICIPANT_REF, id);

		return true;
	}

	private ParticipantBandKind getParticipantBandKind(boolean initiating, ParticipantBandKind currentBandKind) {
		switch (currentBandKind) {
		case TOP_INITIATING:
			return initiating ? currentBandKind : ParticipantBandKind.TOP_NON_INITIATING;
		case MIDDLE_INITIATING:
			return initiating ? currentBandKind : ParticipantBandKind.MIDDLE_NON_INITIATING;
		case BOTTOM_INITIATING:
			return initiating ? currentBandKind : ParticipantBandKind.BOTTOM_NON_INITIATING;
		case TOP_NON_INITIATING:
			return initiating ? ParticipantBandKind.TOP_INITIATING : currentBandKind;
		case MIDDLE_NON_INITIATING:
			return initiating ? ParticipantBandKind.MIDDLE_INITIATING : currentBandKind;
		case BOTTOM_NON_INITIATING:
			return initiating ? ParticipantBandKind.BOTTOM_INITIATING : currentBandKind;
		default:
			return currentBandKind;
		}
	}
}