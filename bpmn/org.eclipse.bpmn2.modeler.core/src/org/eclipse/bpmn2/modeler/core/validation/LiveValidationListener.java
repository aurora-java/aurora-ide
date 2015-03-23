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

import java.util.List;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.validation.marker.MarkerUtil;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.emf.validation.service.IValidationListener;
import org.eclipse.emf.validation.service.ValidationEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Bob Brodt
 * 
 */
public class LiveValidationListener implements IValidationListener {

	static ValidationErrorHandler handler;

	public void validationOccurred(ValidationEvent event) {
		reportError(null);
		// only report Live validation events here
		// Batch validation is done by the WST project validator during
		// building.
		if (event.getEvaluationMode() == EvaluationMode.LIVE) {
//			if (event.matches(IStatus.WARNING | IStatus.ERROR | IStatus.CANCEL)) 
			{
				// fabricate a multi-errorList for the MarkerUtil to consume
				List<IConstraintStatus> results = event.getValidationResults();
				MultiStatus multi = new MultiStatus(Activator.getDefault().PLUGIN_ID, 1,
						(IStatus[]) results.toArray(new IStatus[results.size()]), Messages.LiveValidationListener_Title, null);

				for (IStatus s : results) {
					reportError(s);
				}
			}
		}
	}
	
	public static void setValidationErrorHandler(ValidationErrorHandler h) {
		handler = h;
	}

	private void reportError(IStatus s) {
		if (handler==null) {
			String message = (s==null) ? null : s.getMessage();
			ErrorUtils.showErrorMessage(message);
		}
		else
			handler.reportError(s);
	}
}