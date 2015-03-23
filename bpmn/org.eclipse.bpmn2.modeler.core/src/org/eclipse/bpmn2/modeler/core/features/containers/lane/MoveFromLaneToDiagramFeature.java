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

public class MoveFromLaneToDiagramFeature extends MoveLaneFeature {

	public MoveFromLaneToDiagramFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		return true;
	}

	@Override
	protected void internalMove(IMoveShapeContext context) {
		modifyModelStructure(context);
		layoutPictogramElement(context.getSourceContainer());
	}

	private void modifyModelStructure(IMoveShapeContext context) {
		Lane sourceLane = (Lane) getBusinessObjectForPictogramElement(context.getSourceContainer());
		Lane movedLane = getMovedLane(context);
		sourceLane.getChildLaneSet().getLanes().remove(movedLane);
		
		LaneSet newLaneSet = createLaneSet();
		newLaneSet.getLanes().add(movedLane);
		Process targetProcess = getProcess(context.getTargetContainer());
		targetProcess.getLaneSets().add(newLaneSet);
	}

}