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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.features.activity.task;

import org.eclipse.bpmn2.modeler.core.features.AbstractBpmn2DirectEditingFeature;
import org.eclipse.bpmn2.modeler.core.features.DirectEditBaseElementFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;

public class DirectEditTaskFeature extends AbstractBpmn2DirectEditingFeature {

	public DirectEditTaskFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value.length() < 1) {
			return Messages.DirectEditTaskFeature_Invalid_Empty;
		} else if (value.contains("\n")) {  //$NON-NLS-1$
			return Messages.DirectEditTaskFeature_Invalid_Linebreak;
		}
		return null;
	}
}