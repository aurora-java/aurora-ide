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

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

public class FlowElementTreeEditPart extends AbstractGraphicsTreeEditPart {
	
	public FlowElementTreeEditPart(DiagramTreeEditPart dep, FlowElement flowElement) {
		super(dep, flowElement);
	}

	public FlowElement getFlowElement() {
		return (FlowElement) getModel();
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
		FlowElement elem = getFlowElement();

		if (elem instanceof FlowElementsContainer) {
			FlowElementsContainer container = (FlowElementsContainer)elem;
			return getFlowElementsContainerChildren(container);
		}
		else if (elem instanceof ChoreographyActivity) {
			ChoreographyActivity ca = (ChoreographyActivity)elem;
			retList.addAll(ca.getParticipantRefs());
		}
		else if (elem instanceof CallActivity) {
			// render a Call Activity with its called activity target
			// (a Process or Global Task) as the child node.
			CallableElement target = ((CallActivity)elem).getCalledElementRef();
			if (target!=null) {
				retList.add(target);
			}
		}
		else if (elem instanceof CatchEvent) {
			retList.addAll(((CatchEvent)elem).getEventDefinitions());
			retList.addAll(((CatchEvent)elem).getDataOutputAssociation());
		}
		else if (elem instanceof ThrowEvent) {
			retList.addAll(((ThrowEvent)elem).getEventDefinitions());
			retList.addAll(((ThrowEvent)elem).getDataInputAssociation());
		}
		
		if (elem instanceof Activity) {
			// Boundary Events are children nodes of Activities
			Definitions definitions = ModelUtil.getDefinitions(elem);
			if (definitions!=null) {
				TreeIterator<EObject> iter = definitions.eAllContents();
				while (iter.hasNext()) {
					EObject o = iter.next();
					if (o instanceof BoundaryEvent && ((BoundaryEvent)o).getAttachedToRef() == elem) {
						retList.add(o);
					}
				}
				retList.addAll(((Activity)elem).getDataInputAssociations());
				retList.addAll(((Activity)elem).getDataOutputAssociations());
			}
		}
		return retList;
	}
	
	public static List<Object> getFlowElementsContainerChildren(FlowElementsContainer container) {
		List<Object> retList = new ArrayList<Object>();
		List<FlowElement> flowElements = new ArrayList<FlowElement>();
		for (FlowElement fe : container.getFlowElements()) {
			if (!(fe instanceof BoundaryEvent))
				flowElements.add(fe);
		}
		
		if (container.getLaneSets().size()==0)
			retList.addAll(flowElements);
		else {
			for (LaneSet ls : container.getLaneSets()) {
				retList.addAll(ls.getLanes());
			}
			// only add the flow element if it's not contained in a Lane
			List<Object> laneElements = new ArrayList<Object>();
			for (FlowElement fe : flowElements) {
				boolean inLane = false;
				for (LaneSet ls : container.getLaneSets()) {
					if (isInLane(fe,ls)) {
						inLane = true;
						break;
					}
				}
				if (inLane)
					laneElements.add(fe);
				else
					retList.add(fe);
			}
			
			// don't include any sequence flows that connect flow
			// nodes that are contained in Lanes
			List<SequenceFlow> flows = new ArrayList<SequenceFlow>();
			for (Object fn : laneElements) {
				if (fn instanceof FlowNode) {
					for (SequenceFlow sf : ((FlowNode)fn).getIncoming()) {
						if (
								laneElements.contains(sf.getSourceRef()) &&
								laneElements.contains(sf.getTargetRef()) &&
								!flows.contains(sf)) {
							flows.add(sf);
						}
					}
					for (SequenceFlow sf : ((FlowNode)fn).getOutgoing()) {
						if (
								laneElements.contains(sf.getSourceRef()) &&
								laneElements.contains(sf.getTargetRef()) &&
								!flows.contains(sf)) {
							flows.add(sf);
						}
					}
				}
			}
			retList.removeAll(flows);
		}
		
		// add the list of Artifacts
		if (container instanceof Process) {
			retList.addAll(((Process)container).getArtifacts());
		}
		else if (container instanceof SubProcess) {
			retList.addAll(((SubProcess)container).getArtifacts());
		}
		if (container instanceof SubChoreography) {
			retList.addAll(((SubChoreography)container).getArtifacts());
		}
		if (container instanceof Choreography) {
			// Add Pools as children if the Pool has a Process associated with it,
			// or if the Participant is NOT referenced by a Choreography Activity.
			for (Participant p : ((Choreography)container).getParticipants()) {
				if (p.getProcessRef()!=null)
					retList.add(p);
				else {
					for (FlowElement fe : flowElements) {
						if (fe instanceof ChoreographyActivity) {
							if (!((ChoreographyActivity)fe).getParticipantRefs().contains(p)) {
								retList.add(p);
							}
						}
					}
				}
			}
		}
		return retList;
	}
	
	public static boolean isInLane(FlowElement fe, LaneSet ls) {
		if (ls==null || ls.getLanes().size()==0)
			return false;
		
		for (Lane ln : ls.getLanes()) {
			if (ln.getFlowNodeRefs().contains(fe))
				return true;
			if (isInLane(fe, ln.getChildLaneSet()))
				return true;
		}
		return false;
	}
}