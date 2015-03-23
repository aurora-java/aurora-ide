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
import org.eclipse.bpmn2.Process;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

public class MoveFromParticipantToLaneFeature extends MoveLaneFeature {

	public MoveFromParticipantToLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		Lane movedLane = getMovedLane(context);
		boolean moveableHasFlowNodes = movedLane.getFlowNodeRefs().size() > 0;

		Lane targetLane = getTargetLane(context);
		boolean targetHasFlowNodeRefs = targetLane.getFlowNodeRefs().size() > 0;

		if (!moveableHasFlowNodes && !targetHasFlowNodeRefs) {
			return true;
		}

		return moveableHasFlowNodes ^ targetHasFlowNodeRefs;
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		modifyModelStructure(context);
		layoutPictogramElement(context.getSourceContainer());
		layoutPictogramElement(context.getTargetContainer());
	}

	private void modifyModelStructure(IMoveShapeContext context) {
		Lane movedLane = getMovedLane(context);
		Lane targetLane = getTargetLane(context);
		Process sourceProcess = getProcess(context.getSourceContainer());
		Process targetProcess = getProcess(targetLane);
		moveLane(movedLane, sourceProcess, targetProcess);

		for (LaneSet laneSet : sourceProcess.getLaneSets()) {
			if (laneSet.getLanes().contains(movedLane)) {
				laneSet.getLanes().remove(movedLane);
				if (laneSet.getLanes().isEmpty()) {
					sourceProcess.getLaneSets().remove(laneSet);
				}
				break;
			}
		}

		if (targetLane.getChildLaneSet() == null) {
			LaneSet newLaneSet = createLaneSet();
			targetLane.setChildLaneSet(newLaneSet);
		}
		targetLane.getChildLaneSet().getLanes().add(movedLane);
	}
}