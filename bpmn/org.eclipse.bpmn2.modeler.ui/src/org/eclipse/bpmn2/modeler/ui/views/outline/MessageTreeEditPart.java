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
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.modeler.ui.util.PropertyUtil;
import org.eclipse.swt.graphics.Image;

public class MessageTreeEditPart extends AbstractGraphicsTreeEditPart {

	public MessageTreeEditPart(DiagramTreeEditPart dep, Message baseElement) {
		super(dep, baseElement);
	}

	public BaseElement getBaseElement() {
		return (BaseElement) getModel();
	}

	public Message getMessage() {
		return (Message) getModel();
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
	protected Image getImage() {
		Message message = getMessage();
		if (getParent() instanceof OperationTreeEditPart) {
			Operation operation = ((OperationTreeEditPart)getParent()).getOperation();
			if (operation.getInMessageRef()==message)
				return PropertyUtil.getImage("InMessage"); //$NON-NLS-1$
			if (operation.getOutMessageRef()==message)
				return PropertyUtil.getImage("OutMessage"); //$NON-NLS-1$
		}
		return super.getImage();
	}
}