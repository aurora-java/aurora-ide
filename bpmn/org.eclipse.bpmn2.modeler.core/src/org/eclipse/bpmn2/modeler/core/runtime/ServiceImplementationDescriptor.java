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

package org.eclipse.bpmn2.modeler.core.runtime;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 *
 */
public class ServiceImplementationDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "serviceImplementation"; //$NON-NLS-1$

	protected String name;
	protected String uri;
	
	public ServiceImplementationDescriptor(IConfigurationElement e) {
		super(e);
		name = e.getAttribute("name"); //$NON-NLS-1$
		uri = e.getAttribute("uri"); //$NON-NLS-1$
	}
	
	@Override
	public void setRuntime(TargetRuntime targetRuntime) {
		super.setRuntime(targetRuntime);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public String getExtensionName() {
		return EXTENSION_NAME;
	}
	
	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}
}
