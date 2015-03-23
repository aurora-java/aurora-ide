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

package org.eclipse.bpmn2.modeler.core.runtime;

import java.lang.reflect.Constructor;

import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.features.IFeatureContainer;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Target Runtime Extension Descriptor class for Graphiti FeatureContainer overrides.
 * Instances of this class correspond to <featureContainer> extension elements in the extension's plugin.xml
 * See the description of the "featureContainer" element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class FeatureContainerDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "featureContainer"; //$NON-NLS-1$

	protected String type;
	protected String containerClassName;

	/**
	 * @param rt
	 */
	public FeatureContainerDescriptor(IConfigurationElement e) {
		super(e);
		type = e.getAttribute("type"); //$NON-NLS-1$
		containerClassName = e.getAttribute("class"); //$NON-NLS-1$
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public Class getType() {
		ClassLoader cl = this.getRuntime().getRuntimeExtension().getClass().getClassLoader();
		try {
			return Class.forName(type, true, cl);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public IFeatureContainer getFeatureContainer() {
		try {
			ClassLoader cl = this.getRuntime().getRuntimeExtension().getClass().getClassLoader();
			Constructor ctor = null;
			Class adapterClass = Class.forName(containerClassName, true, cl);
			ctor = adapterClass.getConstructor();
			return (IFeatureContainer)ctor.newInstance();
		} catch (Exception e) {
			Activator.logError(e);
		}
		return null;
	}
}
