/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
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

package org.eclipse.bpmn2.modeler.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Provides ToolTip text and longer descriptions of the tool or operation.
 */
public interface IToolTipProvider {

	String getToolTip(Object context, EObject object);
	String getLongDescription(Object context, EObject object);
	String getToolTip(Object context, EObject object, EStructuralFeature feature);
	String getLongDescription(Object context, EObject object, EStructuralFeature feature);
}
