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

import java.lang.reflect.Constructor;

import org.eclipse.bpmn2.modeler.core.DefaultConversionDelegate;
import org.eclipse.bpmn2.modeler.core.EDataTypeConversionFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;

/**
 * Target Runtime Extension Descriptor class for EMF data type definitions.
 * Instances of this class correspond to <dataType> extension elements in the extension's plugin.xml
 * See the description of the "dataType" element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class DataTypeDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "dataType"; //$NON-NLS-1$

	protected String name;
	protected String delegateClassName;
	
	public DataTypeDescriptor(IConfigurationElement e) {
		super(e);
		name = e.getAttribute("name"); //$NON-NLS-1$
		delegateClassName = e.getAttribute("class"); //$NON-NLS-1$
	}
	
	@Override
	public void dispose() {
		super.dispose();
		EDataTypeConversionFactory.unregisterConversionDelegate(name);
	}

	@Override
	public void setRuntime(TargetRuntime targetRuntime) {
		try {
			super.setRuntime(targetRuntime);
			ConversionDelegate delegate;
			delegate = getConversionDelegate();
			EDataTypeConversionFactory.registerConversionDelegate(name, delegate.getClass());
		} catch (Exception e) {
			throw new TargetRuntimeConfigurationException(targetRuntime,e);
		}
	}

	@Override
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public String getDataTypeName() {
		return name;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ConversionDelegate getConversionDelegate() throws TargetRuntimeConfigurationException {
		try {
			ClassLoader cl = this.getRuntime().getRuntimeExtension().getClass().getClassLoader();
			Constructor ctor = null;
			Class adapterClass = Class.forName(delegateClassName, true, cl);
			ctor = adapterClass.getConstructor();
			return (ConversionDelegate)ctor.newInstance();
		}
		catch (Exception ex1) {
			try {
				Object object = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
				return (DefaultConversionDelegate) object;
			} catch (Exception ex2) {
				throw new TargetRuntimeConfigurationException(targetRuntime,ex2);
			}
		}
	}

}
