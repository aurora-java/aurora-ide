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

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNLabel;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.graphiti.mm.pictograms.Diagram;

/**
 * A concrete implementation of the interface IEditPartFactory for Trees, which
 * works on a pictogram model.
 */
public class BPMNDiagramTreeEditPartFactory implements EditPartFactory {

	private int id;
	
	/**
	 * Creates a new PictogramsEditPartFactory.
	 */
	public BPMNDiagramTreeEditPartFactory(int id) {
		super();
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 * java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart ret = null;
		if (model instanceof Diagram) {
			ret = new DiagramTreeEditPart(id, (Diagram) model);
		} else if (context instanceof AbstractGraphicsTreeEditPart ){
			EditPart root = context;
			while (root.getParent()!=null)
				root = root.getParent();
			DiagramTreeEditPart dep = (DiagramTreeEditPart)root.getChildren().get(0);
			
			if (model instanceof Participant) {
				ret = new PoolTreeEditPart(dep, (Participant) model);
			} else if (model instanceof Lane) {
				ret = new LaneTreeEditPart(dep, (Lane) model);
			} else if (model instanceof Group) {
				ret = new GroupTreeEditPart(dep, (Group) model);
			} else if (model instanceof Interface) {
				ret = new InterfaceTreeEditPart(dep, (Interface) model);
			} else if (model instanceof Operation) {
				ret = new OperationTreeEditPart(dep, (Operation) model);
			} else if (model instanceof Group) {
				ret = new GroupTreeEditPart(dep, (Group) model);
			} else if (model instanceof Message) {
				ret = new MessageTreeEditPart(dep, (Message) model);
			} else if (model instanceof RootElement) {
				ret = new RootElementTreeEditPart(dep, (RootElement) model);
			} else if (model instanceof FlowElement) {
				ret = new FlowElementTreeEditPart(dep, (FlowElement) model);
			} else if (model instanceof BaseElement) {
				ret = new BaseElementTreeEditPart(dep, (BaseElement) model);
			} else if (model instanceof BPMNDiagram) {
				ret = new BPMNDiagramTreeEditPart(dep, (BPMNDiagram) model);
			} else if (model instanceof BPMNShape) {
				ret = new BPMNShapeTreeEditPart(dep, (BPMNShape) model);
			} else if (model instanceof BPMNEdge) {
				ret = new BPMNEdgeTreeEditPart(dep, (BPMNEdge) model);
			} else if (model instanceof BPMNLabel) {
				ret = new BPMNLabelTreeEditPart(dep, (BPMNLabel) model);
			}
		}
		return ret;
	}
}