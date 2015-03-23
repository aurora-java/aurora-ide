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
import org.eclipse.bpmn2.Operation;

public class OperationTreeEditPart extends AbstractGraphicsTreeEditPart {

	public OperationTreeEditPart(DiagramTreeEditPart dep, Operation baseElement) {
		super(dep, baseElement);
	}

	public BaseElement getBaseElement() {
		return (BaseElement) getModel();
	}

	public Operation getOperation() {
		return (Operation) getModel();
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
		Operation operation = getOperation();
		if (operation.getInMessageRef()!=null)
			retList.add(operation.getInMessageRef());
		if (operation.getOutMessageRef()!=null)
			retList.add(operation.getOutMessageRef());
		retList.addAll(operation.getErrorRefs());
		return retList;
	}
}