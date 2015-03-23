/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 * All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.views.outline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.emf.ecore.EObject;

public class LaneTreeEditPart extends AbstractGraphicsTreeEditPart {

	public LaneTreeEditPart(DiagramTreeEditPart dep, Lane baseElement) {
		super(dep, baseElement);
	}

	public BaseElement getBaseElement() {
		return (BaseElement) getModel();
	}

	public Lane getLane() {
		return (Lane) getModel();
	}
	
	// ======================= overwriteable behaviour ========================

	/**
	 * Creates the EditPolicies of this EditPart. Subclasses often overwrite
	 * this method to change the behaviour of the editpart.
	 */
	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> retList = new ArrayList<Object>();
		Lane lane = getLane();
		if (lane.getChildLaneSet()!=null && lane.getChildLaneSet().getLanes().size()>0)
			retList.addAll(lane.getChildLaneSet().getLanes());
		retList.addAll(lane.getFlowNodeRefs());
		
		// include all sequence flows connecting flow nodes
		// that are contained in this Lane
		List<SequenceFlow> flows = new ArrayList<SequenceFlow>();
		for (Object fn : retList) {
			if (fn instanceof FlowNode) {
				for (SequenceFlow sf : ((FlowNode)fn).getIncoming()) {
					if (
							(retList.contains(sf.getSourceRef()) ||
							retList.contains(sf.getTargetRef())) &&
							!flows.contains(sf)) {
						flows.add(sf);
					}
				}
				for (SequenceFlow sf : ((FlowNode)fn).getOutgoing()) {
					if (
							(retList.contains(sf.getSourceRef()) ||
							retList.contains(sf.getTargetRef())) &&
							!flows.contains(sf)) {
						flows.add(sf);
					}
				}
			}
		}
		retList.addAll(flows);
		return retList;
	}
}