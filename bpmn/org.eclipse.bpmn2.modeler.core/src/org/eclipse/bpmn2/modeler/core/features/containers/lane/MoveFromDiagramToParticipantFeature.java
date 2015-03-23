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

import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;

public class MoveFromDiagramToParticipantFeature extends MoveLaneFeature {

	public MoveFromDiagramToParticipantFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		Participant p = (Participant) getBusinessObjectForPictogramElement(context.getTargetContainer());

		if (getMovedLane(context).getFlowNodeRefs().isEmpty()) {
			return true;
		}

		if (p.getProcessRef() == null) {
			return true;
		}

		if (!p.getProcessRef().getLaneSets().isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		modifyModelStructure(context);
		layoutPictogramElement(context.getTargetContainer());
	}

	private void modifyModelStructure(IMoveShapeContext context) {
		Lane movedLane = getMovedLane(context);
		Process sourceProcess = getProcess(context.getSourceContainer());
		Process targetProcess = getProcess(context.getTargetContainer());
		moveLane(movedLane, sourceProcess, targetProcess);
		
		for (LaneSet laneSet : sourceProcess.getLaneSets()) {
			if (laneSet.getLanes().contains(movedLane)) {
				laneSet.getLanes().remove(movedLane);
				if (laneSet.getLanes().isEmpty()) {
					// remove the LaneSet if it's empty
					sourceProcess.getLaneSets().remove(laneSet);
				}
				break;
			}
		}
		
		if (targetProcess.getLaneSets().isEmpty()) {
			// create a new LaneSet if needed
			LaneSet newLaneSet = createLaneSet();
			targetProcess.getLaneSets().add(newLaneSet);
		}
		targetProcess.getLaneSets().get(0).getLanes().add(movedLane);
	}
}
