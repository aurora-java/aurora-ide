/*******************************************************************************
 * Copyright (c) 2011, 2012, 2013, 2014 Red Hat, Inc.
 * All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.adapters;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;

/**
 * An EMF Resource provider interface. This is also an Editing Domain provider since
 * the Resource <b>may</b> have a reference to an Editing Domain.
 */
public interface IResourceProvider extends IEditingDomainProvider {
	Resource getResource();
	void setResource(Resource resource);
}
