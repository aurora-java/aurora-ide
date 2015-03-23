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
 * @author Bob Brodt
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.validation;

import java.util.HashSet;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;

public class ValidIdConstraint extends AbstractModelConstraint {
	
	protected static HashSet<EClass> idOptional = new HashSet<EClass>();
	static {
		idOptional.add(Bpmn2Package.eINSTANCE.getDefinitions());
		// TODO: are there other BPMN2 object types whose IDs are optional?
	}
	
	@Override
	public IStatus validate(IValidationContext ctx) {
		EObject eObj = ctx.getTarget();
		EMFEventType eType = ctx.getEventType();
		
		if (!idOptional.contains(eObj.eClass())) {
			// In the case of batch mode.
			if (eType == EMFEventType.NULL) {
				String id = null;
				if (eObj instanceof BaseElement) {
					id = ((BaseElement)eObj).getId(); 
				}
				
				if (!SyntaxCheckerUtils.isNCName(id)) {
					return ctx.createFailureStatus(new Object[] {eObj.eClass().getName()});
				}
			// In the case of live mode.
			} else {
				String newValue = (String) ctx.getFeatureNewValue();
				
				if (!SyntaxCheckerUtils.isNCName(newValue)) {
					return ctx.createFailureStatus(new Object[] {eObj.eClass().getName()});
				}
			}
		}
		
		return ctx.createSuccessStatus();
	}

}
