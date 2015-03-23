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
package org.eclipse.bpmn2.modeler.core.features.containers.lane;

import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.modeler.core.features.containers.MoveContainerFeature;
import org.eclipse.bpmn2.modeler.core.model.ModelHandler;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.bpmn2.modeler.core.utils.FeatureSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.ITargetContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * Handles the moving of Lanes from one container to another. The source and
 * target containers may be Pools, other Lanes or the Diagram.
 * 
 * TODO: allow reordering of Lanes within their containers. For example, current
 * behavior when a Lane is dragged from a Diagram and dropped into another Lane
 * which already contains child Lanes, the dropped Lane is always added to the
 * bottom or left of existing child Lanes. It would be useful to allow the user
 * to select the location of the dropped Lane relative to existing child Lanes,
 * by looking at the mouse cursor position.
 */
public class MoveLaneFeature extends MoveContainerFeature {

	private MoveLaneFeature moveStrategy;
	protected ModelHandler modelHandler;
	
	public MoveLaneFeature(IFeatureProvider fp) {
		super(fp);
		modelHandler = ModelHandler.getInstance(getDiagram());
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		if (context.getSourceContainer() == null) {
			return false;
		}
		
		moveStrategy = getStrategy(context);

		if (moveStrategy == null) {
			return super.canMoveShape(context);
		}

		return moveStrategy.canMoveShape(context);
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		super.internalMove(context);
		if (moveStrategy != null) {
			moveStrategy.internalMove(context);
		}
	}

	private MoveLaneFeature getStrategy(IMoveShapeContext context) {

		if (context.getSourceContainer().equals(getDiagram())) { // from diagram

			if (FeatureSupport.isTargetLane(context)) { // to lane
				return new MoveFromDiagramToLaneFeature(getFeatureProvider());
			} else if (FeatureSupport.isTargetParticipant(context)) { // to participant
				return new MoveFromDiagramToParticipantFeature(getFeatureProvider());
			}

		} else if (FeatureSupport.isLane(context.getSourceContainer())) { // from lane

			if (context.getTargetContainer().equals(getDiagram())) { // to diagram
				return new MoveFromLaneToDiagramFeature(getFeatureProvider());
			} else if (FeatureSupport.isTargetLane(context)) { // to another lane
				return new MoveFromLaneToLaneFeature(getFeatureProvider());
			} else if (FeatureSupport.isTargetParticipant(context)) { // to participant
				return new MoveFromLaneToParticipantFeature(getFeatureProvider());
			}

		} else if (FeatureSupport.isParticipant(context.getSourceContainer())) { // from participant

			if (context.getTargetContainer().equals(getDiagram())) { // to diagram
				return new MoveFromParticipantToDiagramFeature(getFeatureProvider());
			} else if (FeatureSupport.isTargetLane(context)) { // to another lane
				return new MoveFromParticipantToLaneFeature(getFeatureProvider());
			} else if (FeatureSupport.isTargetParticipant(context)) { // to another participant
				return new MoveFromParticipantToParticipantFeature(getFeatureProvider());
			}
		}

		return null;
	}

	protected Lane getMovedLane(IMoveShapeContext context) {
		return (Lane) getBusinessObjectForPictogramElement(context.getShape());
	}
	
	protected Process getProcess(Object object) {
		Process process = null;
		if (object instanceof PictogramElement) {
			// this could be a Diagram or ContainerShape
			object = BusinessObjectUtil.getBusinessObjectForPictogramElement((PictogramElement)object);
		}
		if (object instanceof BPMNDiagram) {
			// the BPMNDiagram could be a Process or Collaboration/Choreography
			object = ((BPMNDiagram)object).getPlane().getBpmnElement();
		}
		if (object instanceof Collaboration) {
			// the Collaboration contain one or more Participants
			Participant participant = null;
			Collaboration collaboration = (Collaboration) object;
			for (Participant p : collaboration.getParticipants()) {
				if (p.getProcessRef()!=null) {
					process = p.getProcessRef();
					break;
				}
				else if (participant==null)
					participant = p;
			}
			if (process==null) {
				// create a new Process in the Collaboration's first Participant
				object = participant;
			}
		}
		if (object instanceof Participant) {
			Participant participant = (Participant) object;
			process = participant.getProcessRef();
			if (process == null) {
				// create a new Process
				process = modelHandler.create(Process.class);
				modelHandler.getDefinitions().getRootElements().add(process);
				process.setName(participant.getName() + " Process");
				if (participant.eContainer() instanceof Collaboration) {
					process.setDefinitionalCollaborationRef((Collaboration)participant.eContainer());
				}
				participant.setProcessRef(process);
			}
		}
		if (object instanceof Process) {
			process =(Process) object;
		}
		if (process==null && object instanceof EObject) {
			EObject o = (EObject) object;
			while (o.eContainer()!=null) {
				if (o instanceof Process) {
					process = (Process) o;
					break;
				}
				o = o.eContainer();
			}
		}
		return process;
	}

	protected Lane getTargetLane(ITargetContext context) {
		ContainerShape targetContainer = context.getTargetContainer();
		return (Lane) getBusinessObjectForPictogramElement(targetContainer);
	}

	protected Lane getSourceLane(IMoveShapeContext context) {
		ContainerShape sourceContainer = context.getSourceContainer();
		return (Lane) getBusinessObjectForPictogramElement(sourceContainer);
	}

	protected LaneSet createLaneSet() {
		return modelHandler.create(LaneSet.class);
	}
	
	protected void moveLane(Lane movedLane, Process sourceProcess, Process targetProcess) {
		if (sourceProcess!=targetProcess) {
			for (FlowNode node : movedLane.getFlowNodeRefs()) {
				modelHandler.moveFlowNode(node, sourceProcess, targetProcess);
			}
			if (movedLane.getChildLaneSet() != null && !movedLane.getChildLaneSet().getLanes().isEmpty()) {
				for (Lane lane : movedLane.getChildLaneSet().getLanes()) {
					moveLane(lane, sourceProcess, targetProcess);
				}
			}
		}
	}	
}