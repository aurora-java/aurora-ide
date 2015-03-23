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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.ITargetContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

public class MoveFromDiagramToLaneFeature extends MoveLaneFeature {

	public MoveFromDiagramToLaneFeature(IFeatureProvider fp) {
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
		IGaService gaService = Graphiti.getGaService();
		Lane movedLane = getMovedLane(context);
		Process sourceProcess = getProcess(context.getSourceContainer());
		Process targetProcess = getProcess(context.getTargetContainer());
		moveLane(movedLane, sourceProcess, targetProcess);

		GraphicsAlgorithm laneGa = context.getShape().getGraphicsAlgorithm();

		Lane targetLane = getTargetLane(context);

		List<Shape> shapes = getFlowNodeShapes(context, targetLane);
		modifyModelStructure(targetLane, movedLane);

		if (getNumberOfLanes(context) == 1) {
			gaService.setLocationAndSize(laneGa, 15, 0, laneGa.getWidth() - 15, laneGa.getHeight());
			for (Shape s : shapes) {
				GraphicsUtil.sendToFront(s);
				s.setContainer((ContainerShape) context.getShape());
			}
		} else {
			gaService.setLocationAndSize(laneGa, 15, laneGa.getHeight() - 1, laneGa.getWidth() - 15, laneGa.getHeight() + 1);
		}

		layoutPictogramElement(context.getTargetContainer());
	}

	private void modifyModelStructure(Lane targetLane, Lane lane) {
		LaneSet laneSet = (LaneSet) lane.eContainer();
		laneSet.getLanes().remove(lane);

		if (laneSet.getLanes().size() == 0) {
			EObject container = laneSet.eContainer();
			if (container instanceof Process) {
				Process p = (Process) container;
				p.getLaneSets().remove(laneSet);
			}
		}

		if (targetLane.getChildLaneSet() == null) {
			LaneSet newLaneSet = createLaneSet();
			targetLane.setChildLaneSet(newLaneSet);
			ModelUtil.setID(newLaneSet);
		}

		List<Lane> lanes = targetLane.getChildLaneSet().getLanes();
		lanes.add(lane);

		if (lanes.size() == 1) {
			List<FlowNode> flowNodeRefs = targetLane.getFlowNodeRefs();
			for (FlowNode flowNode : flowNodeRefs) {
				flowNode.getLanes().add(lane);
			}
			targetLane.getFlowNodeRefs().clear();
		}
	}

	private List<Shape> getFlowNodeShapes(IMoveShapeContext context, Lane lane) {
		List<FlowNode> nodes = lane.getFlowNodeRefs();
		List<Shape> shapes = new ArrayList<Shape>();
		for (Shape s : context.getTargetContainer().getChildren()) {
			Object bo = getBusinessObjectForPictogramElement(s);
			if (bo != null && nodes.contains(bo)) {
				shapes.add(s);
			}
		}
		return shapes;
	}

	private int getNumberOfLanes(ITargetContext context) {
		ContainerShape targetContainer = context.getTargetContainer();
		Lane lane = (Lane) getBusinessObjectForPictogramElement(targetContainer);
		return lane.getChildLaneSet().getLanes().size();
	}
}