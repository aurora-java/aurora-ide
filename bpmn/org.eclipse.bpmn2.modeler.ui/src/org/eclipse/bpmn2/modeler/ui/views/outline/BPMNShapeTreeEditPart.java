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

import org.eclipse.bpmn2.di.BPMNLabel;
import org.eclipse.bpmn2.di.BPMNShape;

public class BPMNShapeTreeEditPart extends AbstractGraphicsTreeEditPart {

	public BPMNShapeTreeEditPart(DiagramTreeEditPart dep, BPMNShape bpmnShape) {
		super(dep, bpmnShape);
	}

	public BPMNShape getBPMNShape() {
		return (BPMNShape) getModel();
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
		BPMNShape bpmnShape = getBPMNShape();
		BPMNLabel bpmnLabel = bpmnShape.getLabel();
		if (bpmnLabel!=null)
			retList.add(bpmnLabel);
		return retList;
	}
	
	@Override
	protected String getText() {
		BPMNShape bpmnShape = getBPMNShape();
		if (bpmnShape.getBpmnElement()==null)
			return super.getText(bpmnShape);
		return super.getText(bpmnShape.getBpmnElement());
	}
}