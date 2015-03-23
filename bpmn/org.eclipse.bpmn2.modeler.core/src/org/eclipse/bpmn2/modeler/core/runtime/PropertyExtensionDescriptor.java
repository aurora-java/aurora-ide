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
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * Target Runtime Extension Descriptor class for providing extended property adapters for BPMN2 model objects and features.
 * See the ExtendedPropertiesAdapter class for implementation details of an extended property adapter.
 * Instances of this class correspond to <propertyExtension> extension elements in the extension's plugin.xml
 * See the description of the "propertyExtension" element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class PropertyExtensionDescriptor extends BaseRuntimeExtensionDescriptor {
	
	public final static String EXTENSION_NAME = "propertyExtension"; //$NON-NLS-1$

    protected String type;
    protected String adapterClassName;

	/**
	 * @param rt
	 */
	public PropertyExtensionDescriptor(IConfigurationElement e) {
		super(e);
		type = e.getAttribute("type"); //$NON-NLS-1$
		adapterClassName = e.getAttribute("class"); //$NON-NLS-1$
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	@SuppressWarnings("rawtypes")
	public Class getInstanceClass() {
	    if (type == null) {
	        return null;
	    }
		try {
			return Platform.getBundle(configurationElement.getContributor().getName()).loadClass(type);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public ExtendedPropertiesAdapter getAdapter(AdapterFactory adapterFactory, EObject object) {
        if (adapterClassName == null) {
            return null;
        }
		try {
			Constructor ctor = null;
			Class adapterClass = Platform.getBundle(configurationElement.getContributor().getName()).loadClass(adapterClassName);
			EClass eclass = null;
			if (object instanceof EClass) {
				eclass = (EClass)object;
				object = ExtendedPropertiesAdapter.getDummyObject(eclass);
			}
			else {
				eclass = object.eClass();
			}
			ctor = getConstructor(adapterClass, eclass);
			return (ExtendedPropertiesAdapter)ctor.newInstance(adapterFactory, object);
		} catch (Exception e) {
			Activator.logError(e);
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Constructor getConstructor(Class adapterClass, EClass eclass) {
		Constructor ctor = null;
		try {
			ctor = adapterClass.getConstructor(AdapterFactory.class, eclass.getInstanceClass());
		}
		catch (NoSuchMethodException e) {
			for (EClass superClass : eclass.getESuperTypes()) {
				ctor = getConstructor(adapterClass, superClass);
				if (ctor!=null)
					break;
			}
		}
		return ctor;
	}
}
