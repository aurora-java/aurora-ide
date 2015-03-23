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
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;

public class BaseElementTreeEditPart extends AbstractGraphicsTreeEditPart {

	public BaseElementTreeEditPart(DiagramTreeEditPart dep, BaseElement baseElement) {
		super(dep, baseElement);
	}

	public BaseElement getBaseElement() {
		return (BaseElement) getModel();
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
	protected String getText() {
		// Display the Activity's I/O parameter name for Data Input/Output Associations
		// because DataAssociations don't have a name of their own.
		if (getModel() instanceof DataOutputAssociation) {
			DataOutputAssociation doa = (DataOutputAssociation) getModel();
			if (doa.getSourceRef().size()>0 && doa.getSourceRef().get(0) instanceof DataOutput) {
				DataOutput d = (DataOutput) doa.getSourceRef().get(0);
				return ExtendedPropertiesProvider.getTextValue(d);
			}
		}
		if (getModel() instanceof DataInputAssociation) {
			DataInputAssociation doa = (DataInputAssociation) getModel();
			if (doa.getTargetRef() instanceof DataInput) {
				DataInput d = (DataInput) doa.getTargetRef();
				return ExtendedPropertiesProvider.getTextValue(d);
			}
		}
		
		return super.getText();
	}
}