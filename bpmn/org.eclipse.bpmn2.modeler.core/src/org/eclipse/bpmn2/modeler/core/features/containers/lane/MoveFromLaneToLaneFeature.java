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

public class MoveFromLaneToLaneFeature extends MoveLaneFeature {

	public MoveFromLaneToLaneFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		Lane targetLane = getTargetLane(context);

		if (targetLane.getFlowNodeRefs() != null && targetLane.getFlowNodeRefs().size() > 0) {
			return false;
		}

		Lane movedLane = getMovedLane(context);

		if (targetLane.getChildLaneSet() != null && targetLane.getChildLaneSet().getLanes().contains(movedLane)) {
			return false;
		}

		return true;
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		Lane movedLane = getMovedLane(context);
		Lane targetLane = getTargetLane(context);
		Lane sourceLane = getSourceLane(context);

		modifyModelStructure(sourceLane, targetLane, movedLane);

		layoutPictogramElement(context.getSourceContainer());
		layoutPictogramElement(context.getTargetContainer());
	}

	private void modifyModelStructure(Lane sourceLane, Lane targetLane, Lane movedLane) {
		if (targetLane.getChildLaneSet() == null) {
			LaneSet newLaneSet = createLaneSet();
			targetLane.setChildLaneSet(newLaneSet);
		}

		Process sourceProcess = getProcess(sourceLane);
		Process targetProcess = getProcess(targetLane);
		moveLane(movedLane, sourceProcess, targetProcess);

		targetLane.getChildLaneSet().getLanes().add(movedLane);
		sourceLane.getChildLaneSet().getLanes().remove(movedLane);

		if (sourceLane.getChildLaneSet().getLanes().isEmpty()) {
			sourceLane.setChildLaneSet(null);
		}
	}
}
