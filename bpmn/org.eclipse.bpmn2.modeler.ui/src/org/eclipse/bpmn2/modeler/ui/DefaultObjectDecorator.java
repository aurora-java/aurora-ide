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

package org.eclipse.bpmn2.modeler.ui;

import org.eclipse.bpmn2.modeler.core.runtime.IObjectDecorator;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2FeatureMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 *
 */
public class DefaultObjectDecorator implements IObjectDecorator {

	/**
	 * 
	 */
	public DefaultObjectDecorator() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IObjectDecorator#canApply(org.eclipse.emf.ecore.resource.Resource, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean canApply(String id, Resource resource, EObject object) {
		return Bpmn2FeatureMap.ALL_SHAPES.contains(object.eClass().getInstanceClass());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.runtime.IObjectDecorator#apply(org.eclipse.emf.ecore.resource.Resource, java.lang.Object)
	 */
	@Override
	public boolean apply(String id, Resource resource, Object object) {
		return true;
	}
}
