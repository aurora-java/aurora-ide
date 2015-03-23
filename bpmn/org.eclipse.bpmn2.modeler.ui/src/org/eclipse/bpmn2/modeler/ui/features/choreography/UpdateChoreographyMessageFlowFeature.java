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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.InteractionNode;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.features.AbstractUpdateBaseElementFeature;
import org.eclipse.bpmn2.modeler.core.features.choreography.ChoreographyUtil;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILinkService;
import org.eclipse.graphiti.services.IPeService;

public class UpdateChoreographyMessageFlowFeature extends AbstractUpdateBaseElementFeature<ChoreographyTask> {

	private final IPeService peService = Graphiti.getPeService();
	private final ILinkService linkService = Graphiti.getLinkService();

	public UpdateChoreographyMessageFlowFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		boolean result = false;
		if (super.canUpdate(context)) {
			PictogramElement pe = context.getPictogramElement();
			result = ChoreographyUtil.isChoreographyActivity(pe) || ChoreographyUtil.isChoreographyMessage(pe);
		}
		return result;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		IReason reason = super.updateNeeded(context);
		if (reason.toBoolean()) {
			return reason;
		}
		
		PictogramElement pe = context.getPictogramElement();
		if (ChoreographyUtil.isChoreographyMessage(pe)) {
			Message message = BusinessObjectUtil.getFirstElementOfType(pe, Message.class);
			TreeIterator<EObject> iter = message.eContainer().eAllContents();
			while (iter.hasNext()) {
				EObject eo = iter.next();
				if (eo instanceof ChoreographyTask) {
					ChoreographyTask choreographyTask = (ChoreographyTask)eo;
					for (MessageFlow mf : choreographyTask.getMessageFlowRef()) {
						if (mf.getMessageRef()==message) {
							String oldLabel = peService.getPropertyValue(pe, ChoreographyUtil.MESSAGE_NAME);
							if (oldLabel==null || oldLabel.isEmpty())
								oldLabel = ""; //$NON-NLS-1$
							String newLabel = ChoreographyUtil.getMessageFlowName(mf);
							if (newLabel==null || newLabel.isEmpty())
								newLabel = ""; //$NON-NLS-1$
							if (!newLabel.equals(oldLabel)) {
								reason = Reason.createTrueReason("Choreography Message");
								break;
						}
					}
				}
			}
		}
		}
		else {
			ContainerShape choreographyTaskShape = ChoreographyUtil.getChoreographyActivityShape(pe);
			ChoreographyTask choreographyTask = BusinessObjectUtil.getFirstElementOfType(
					choreographyTaskShape, ChoreographyTask.class);

			String shapeIds = ChoreographyUtil.getMessageRefIds(choreographyTaskShape);
			String taskIds = ChoreographyUtil.getMessageRefIds(choreographyTask);
			if (!shapeIds.equals(taskIds))
				reason = Reason.createTrueReason("Choreography Message Link");
			else {
				shapeIds = ChoreographyUtil.getParticipantRefIds(choreographyTaskShape);
				taskIds = ChoreographyUtil.getParticipantRefIds(choreographyTask);
				if (!shapeIds.equals(taskIds))
					reason = Reason.createTrueReason("Participants");
		}
	}

		return reason;
	}
	
	@Override
	public boolean update(IUpdateContext context) {
		boolean result = false;
		PictogramElement pe = context.getPictogramElement();
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pe);
		BaseElement be = BusinessObjectUtil.getFirstElementOfType(pe,BaseElement.class);
		if (be instanceof ChoreographyTask) {
			result = update((ContainerShape)pe, (ChoreographyTask)be);
		}
		else if (ChoreographyUtil.isChoreographyMessage(pe)) {
			int updates = 0;
			TreeIterator<EObject> iter = be.eContainer().eAllContents();
			while (iter.hasNext()) {
				EObject eo = iter.next();
				if (eo instanceof ChoreographyTask) {
					ChoreographyTask choreographyTask = (ChoreographyTask)eo;
					for (MessageFlow mf : choreographyTask.getMessageFlowRef()) {
						if (mf.getMessageRef()==be) {
							for (PictogramElement cs : linkService.getPictogramElements(diagram, choreographyTask)) {
								if (cs instanceof ContainerShape) {
									if (update((ContainerShape)cs, choreographyTask))
										++updates;
								}
							}
						}
					}
				}
			}
			
			result = updates>0;
		}
		
		return result;
	}
	
	public boolean update(ContainerShape choreographyActivityShape, ChoreographyTask choreographyTask) {
		List<InteractionNode> sources = new ArrayList<InteractionNode>();
		for (MessageFlow message : choreographyTask.getMessageFlowRef()) {
			sources.add(message.getSourceRef());
		}

		for (ContainerShape band : FeatureSupport.getParticipantBandContainerShapes(choreographyActivityShape)) {
			Participant participant = BusinessObjectUtil.getFirstElementOfType(band, Participant.class);
			BPMNShape bpmnShape = BusinessObjectUtil.getFirstElementOfType(band, BPMNShape.class);
			if (!sources.contains(participant) && bpmnShape.isIsMessageVisible()) {
				bpmnShape.setIsMessageVisible(false);
				peService.setPropertyValue(choreographyActivityShape, ChoreographyUtil.MESSAGE_VISIBLE, Boolean.toString(false));
			} else if (sources.contains(participant) && !bpmnShape.isIsMessageVisible()) {
				bpmnShape.setIsMessageVisible(true);
				peService.setPropertyValue(choreographyActivityShape, ChoreographyUtil.MESSAGE_VISIBLE, Boolean.toString(true));
			}
		}

		String choreoIds = ChoreographyUtil.getMessageRefIds(choreographyTask);
		peService.setPropertyValue(choreographyActivityShape, ChoreographyUtil.MESSAGE_REF_IDS, choreoIds);
		return true;
	}
}