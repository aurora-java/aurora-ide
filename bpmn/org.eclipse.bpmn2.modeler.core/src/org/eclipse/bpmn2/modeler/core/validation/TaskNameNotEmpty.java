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
package org.eclipse.bpmn2.modeler.core.validation;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Task;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;

public class TaskNameNotEmpty extends AbstractModelConstraint {

	public TaskNameNotEmpty() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext ctx) {

		EObject eObj = ctx.getTarget();
		EMFEventType eType = ctx.getEventType();
		// In the case of batch mode.
		if (eType == EMFEventType.NULL) {
			if (eObj instanceof Task) {
				Task t = (Task) eObj;
				if (t.getName() == null || t.getName().length() == 0) {
					ctx.addResult(t.eClass().getEStructuralFeature("name")); //$NON-NLS-1$
					return ctx.createFailureStatus(new Object[] { eObj.eClass().getName() });
				}
			}
		}
		return ctx.createSuccessStatus();
	}

}
