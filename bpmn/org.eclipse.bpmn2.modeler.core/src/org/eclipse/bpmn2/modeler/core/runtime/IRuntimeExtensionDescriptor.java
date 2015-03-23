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
package org.eclipse.bpmn2.modeler.core.runtime;

import org.eclipse.core.resources.IFile;

/**
 * Defines the interface for Target Runtime Extension Descriptor classes.
 */
public interface IRuntimeExtensionDescriptor {

	String getExtensionName();
	void setRuntime(TargetRuntime targetRuntime) throws TargetRuntimeConfigurationException;
	TargetRuntime getRuntime();
	IFile getConfigFile();
	void setConfigFile(IFile configFile);
	void dispose();
}
