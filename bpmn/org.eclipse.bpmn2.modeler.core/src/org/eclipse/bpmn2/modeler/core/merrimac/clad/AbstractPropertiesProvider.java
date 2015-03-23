/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.merrimac.clad;

import org.eclipse.emf.ecore.EObject;

/**
 * Provider class for the Default Properties sheet tab.
 * This simply returns a list of properties, containment ELists and references
 * to be rendered on the Default Properties tab. If the DefaultDetailComposite
 * is subclassed and the client does not specify an item provider, the default
 * behavior is to render all structural features for the business object.
 */
public abstract class AbstractPropertiesProvider {
	
	EObject businessObject;
	
	public AbstractPropertiesProvider(EObject object) {
		businessObject = object;
	}

	public abstract String[] getProperties();

	public void setProperties(String[] properties) {
	}
}
