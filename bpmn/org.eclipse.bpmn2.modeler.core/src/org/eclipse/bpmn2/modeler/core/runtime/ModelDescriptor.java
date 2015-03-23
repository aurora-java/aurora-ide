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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * Target Runtime Extension Descriptor class for EMF extension model definitions to be used with the BPMN2 editor.
 * Instances of this class correspond to <model> extension elements in the extension's plugin.xml
 * See the description of the "model" element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class ModelDescriptor extends BaseRuntimeExtensionDescriptor {
	
	public final static String EXTENSION_NAME = "model"; //$NON-NLS-1$

	protected String uri;
	protected EPackage ePackage;
	protected EFactory eFactory;
	protected ResourceFactoryImpl resourceFactory;
	
	public ModelDescriptor(IConfigurationElement e) {
		super(e);
		// must have at least a namespace URI to associate with this Model Descriptor
		uri = e.getAttribute("uri"); //$NON-NLS-1$
		try {
			// Find the EPackage for this URI if it exists.
			EPackage pkg = EPackage.Registry.INSTANCE.getEPackage(uri);
			if (pkg!=null) {
				setEPackage(pkg);
				setEFactory(getEPackage().getEFactoryInstance());
				if (e.getAttribute("resourceFactory")!=null) { //$NON-NLS-1$
					setResourceFactory((ResourceFactoryImpl) e.createExecutableExtension("resourceFactory")); //$NON-NLS-1$
				}
			}
		}
		catch (Exception e1) {
		}
		
		if (getEPackage()==null) {
			// The plugin does not define its own EPackage, but we still need one
			// to be able to create model objects.
			ModelDescriptor defaultModelDescriptor = TargetRuntime.getDefaultRuntime().getModelDescriptor();
			setEPackage(defaultModelDescriptor.getEPackage());
			setEFactory(defaultModelDescriptor.getEFactory());
			setResourceFactory(defaultModelDescriptor.getResourceFactory());
		}
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public EFactory getEFactory() {
		return eFactory;
	}
	
	public ResourceFactoryImpl getResourceFactory() {
		return resourceFactory;
	}
	
	public EPackage getEPackage() {
		return ePackage;
	}

	public void setEPackage(EPackage ePackage) {
		this.ePackage = ePackage;
	}

	public void setEFactory(EFactory eFactory) {
		this.eFactory = eFactory;
	}

	public void setResourceFactory(ResourceFactoryImpl resourceFactory) {
		this.resourceFactory = resourceFactory;
	}
}