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

import java.util.Hashtable;

import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * A Default Resource and Property Provider class for linking an EMF Resource.
 * <p>
 * This adapter links any EObject with an EMF Resource. The adapter is also a
 * Property (name/value pair) holder so it can be used to pass additional
 * information along with the EObject. This is useful for sending object
 * construction details to the BPMN2 Modeler object factory. See
 * {@link Bpmn2ModelerFactory#create(EClass)}
 */
public class ObjectPropertyProvider extends AdapterImpl implements IResourceProvider, IPropertyHolder {

	/** Property key for the EMF Resource that the object will eventually be (or already is) contained in */
	public static final String RESOURCE = "resource"; //$NON-NLS-1$

	protected Hashtable<String, Object> properties = new Hashtable<String, Object>();
	
	/**
	 * Add this adapter to the given EObject and link it with the given Resource.
	 *
	 * @param object the object
	 * @param resource the resource
	 * @return this Resource Provider adapter
	 */
	public static ObjectPropertyProvider adapt(EObject object, Resource resource) {
		ObjectPropertyProvider adapter = getAdapter(object);
		if (adapter!=null)
			adapter.setResource(resource);
		else {
			adapter = new ObjectPropertyProvider(resource);
			object.eAdapters().add(adapter);
		}
		return adapter;
	}
	
	/**
	 * Gets the adapter.
	 *
	 * @param object the object
	 * @return the adapter
	 */
	public static ObjectPropertyProvider getAdapter(EObject object) {
		for (Adapter a : object.eAdapters()) {
			if (a instanceof ObjectPropertyProvider) {
				return (ObjectPropertyProvider)a;
			}
		}
		return null;
	}
	
	/**
	 * Instantiates a new resource provider.
	 *
	 * @param resource the resource
	 */
	protected ObjectPropertyProvider(Resource resource) {
		if (resource!=null)
			setResource(resource);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
	 */
	@Override
	public EditingDomain getEditingDomain() {
		Resource resource = getResource();
		if (resource!=null) {
			EditingDomain result = AdapterFactoryEditingDomain.getEditingDomainFor(resource);
			if (result instanceof TransactionalEditingDomain)
				return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IResourceProvider#getResource()
	 */
	@Override
	public Resource getResource() {
		return (Resource) getProperty(RESOURCE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IResourceProvider#setResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void setResource(Resource resource) {
		setProperty(RESOURCE, resource);
	}

	/**
	 * Sets the property.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void setProperty(String key, Object value) {
		if (value==null)
			properties.remove(key);
		else
			properties.put(key, value);
	}
	
	/**
	 * Gets the property.
	 *
	 * @param key the key
	 * @return the property
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}
	
	/**
	 * Given an EObject always returns the BPMN2 Resource that is associated
	 * with that object. This may involve searching for all Resources in the
	 * ResourceSet that the EObject belongs to. This also searches for a
	 * Resource in the object's {@link InsertionAdapter} if the object is not
	 * yet contained in any Resource.
	 * 
	 * @param object
	 * @return
	 */
	public static Resource getResource(EObject object) {
		Resource resource = null;
		if (object!=null) {
			resource = object.eResource();
			if (resource==null) {
				IResourceProvider rp = AdapterRegistry.INSTANCE.adapt(object, IResourceProvider.class);
				if (rp!=null)
					resource = rp.getResource();
			}
			
			if (resource==null) {
				for (Adapter a : object.eAdapters()) {
					if (a instanceof IResourceProvider) {
						resource = ((IResourceProvider)a).getResource();
						if (resource!=null)
							break;
					}
				}
			}
			
			// make sure we get a BPMN2 Resource
			if (resource!=null && !(resource instanceof Bpmn2Resource)) {
				ResourceSet rs = resource.getResourceSet();
				if (rs!=null) {
					for (Resource r : rs.getResources()) {
						if (r instanceof Bpmn2Resource) {
							resource = r;
							break;
						}
					}
				}
			}
		}
		return resource;
	}
}
