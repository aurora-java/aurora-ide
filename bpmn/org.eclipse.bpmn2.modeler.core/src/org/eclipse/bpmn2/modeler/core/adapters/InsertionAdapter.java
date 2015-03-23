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

package org.eclipse.bpmn2.modeler.core.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * This adapter will insert a new value into its container feature when the
 * owning object's content changes. This allows the UI to construct new objects
 * without inserting them into their container unless the user changes some
 * feature in the new object. Thus, an empty EObject is available for use by the
 * UI for rendering only, without creating an EMF transaction, and hence, a
 * useless entry on the undo stack.
 */
public class InsertionAdapter extends EContentAdapter implements IResourceProvider {
	
	protected Resource resource;
	protected EObject object;
	protected EStructuralFeature feature;
	protected EObject value;
	
	private InsertionAdapter(EObject object, EStructuralFeature feature, EObject value) {
		this.resource = object.eResource();
		this.object = object;
		this.feature = feature;
		this.value = value;
	}
	
	private InsertionAdapter(EObject object, String featureName, EObject value) {
		this(object, object.eClass().getEStructuralFeature(featureName), value);
	}

	/**
	 * Create an InsertionAdapter that will add the value into the given
	 * object's containment feature as soon as some feature in the value is
	 * changed by the user.
	 * <p>
	 * In order for this to work, the object being adapted must be contained in
	 * a Resource, the value must <b>not yet</b> be contained in a Resource, and
	 * the value must be an instance of the feature's EType.
	 * 
	 * @param object the object being adapted
	 * @param feature a containment feature of the object
	 * @param value the value to be inserted
	 * @return the value to be inserted
	 */
	public static EObject add(EObject object, EStructuralFeature feature, EObject value) {
		if (object!=null) {
			value.eAdapters().add(
					new InsertionAdapter(object, feature, value));
		}
		return value;
	}
	
	/**
	 * Convenience method for creating an InsertionAdapter given a feature name.
	 * 
	 * @param object the object being adapted
	 * @param featureName the name of a containment feature of the object
	 * @param value the value to be inserted
	 * @return the value to be inserted
	 */
	public static EObject add(EObject object, String featureName, EObject value) {
		if (object!=null) {
			value.eAdapters().add(
					new InsertionAdapter(object, featureName, value));
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.util.EContentAdapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification notification) {
		if (notification.getNotifier() == value && !(notification.getOldValue() instanceof InsertionAdapter)) {
			// execute if an attribute in the new value has changed
			execute();
		}
		else if (notification.getNotifier()==object && notification.getNewValue()==value) {
			// if the new value has been added to the object, we can remove this adapter
			object.eAdapters().remove(this);
		}
	}

	private void executeChildren(List list) {
		for (Object o : list) {
			if (o instanceof List) {
				executeChildren((List)o);
			}
			else if (o instanceof EObject) {
			    executeIfNeeded((EObject)o);
			}
		}
	}
	
	private void executeChildren(EObject value) {
		// allow other adapters to execute first
		for (EStructuralFeature f : value.eClass().getEAllStructuralFeatures()) {
			try {
				Object v = value.eGet(f);
				if (v instanceof List) {
					executeChildren((List)v);
				}
				else if (v instanceof EObject) {
					executeIfNeeded((EObject)v);
				}
			}
			catch (Exception e) {
				// some getters may throw exceptions - ignore those
			}
		}
		executeIfNeeded(value);
	}
	
	@SuppressWarnings("unchecked")
	private void execute() {
		// if the object into which this value is being added has other adapters execute those first
		executeIfNeeded(object);
		
		// remove this adapter from the value - this adapter is a one-shot deal!
		value.eAdapters().remove(this);

		try {
			Object o = object.eGet(feature);
		}
		catch (Exception e1) {
			try {
				if (value.eClass().getEStructuralFeature(feature.getName())!=null) {
					Object o = value.eGet(feature);
					// this is the inverse add of object into value
					o = value;
					value = object;
					object = (EObject)o;
				}
			}
			catch (Exception e2) {
			}
		}
		// if there are any EObjects contained or referenced by this value, execute those adapters first
		executeChildren(value);
		
		// set the value in the object
		boolean valueChanged = false;
		final EList<EObject> list = feature.isMany() ? (EList<EObject>)object.eGet(feature) : null;
		if (list==null) {
			try {
				valueChanged = object.eGet(feature)!=value;
			}
			catch (Exception e) {
				// feature does not exist, it's a dynamic feature
				valueChanged = true;
			}
		}
		else
			valueChanged = !list.contains(value) || value instanceof ExtensionAttributeValue;
		
		if (valueChanged) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
			if (adapter!=null) {
				adapter.getFeatureDescriptor(feature).setValue(value);
			}
		}
	}
	
	/**
	 * Adds the value to the object's containment feature.
	 * 
	 * @param value
	 */
	public static void executeIfNeeded(EObject value) {
		List<InsertionAdapter> allAdapters = new ArrayList<InsertionAdapter>();
		
		for (Adapter adapter : value.eAdapters()) {
			if (adapter instanceof InsertionAdapter) {
				allAdapters.add((InsertionAdapter)adapter);
			}
		}
		value.eAdapters().removeAll(allAdapters);
		for (InsertionAdapter adapter : allAdapters)
			adapter.execute();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IResourceProvider#getResource()
	 */
	@Override
	public Resource getResource() {
		if (resource==null) {
			Resource res = object.eResource();
			if (res!=null)
				return res;
			InsertionAdapter insertionAdapter = AdapterUtil.adapt(object, InsertionAdapter.class);
			if (insertionAdapter!=null)
				return insertionAdapter.getResource();
		}
		return resource;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.IResourceProvider#setResource(org.eclipse.emf.ecore.resource.Resource)
	 */
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Gets the EMF Resource that contains the given object. If the object has been adapted
	 * for InsertionAdapter, the Resource defined by that adapter is returned.
	 * 
	 * @param object the object.
	 * @return an EMF Resource or null.
	 */
	public static Resource getResource(EObject object) {
		InsertionAdapter adapter = AdapterUtil.adapt(object, InsertionAdapter.class);
		if (adapter!=null) {
			return adapter.getResource();
		}
		if (object!=null)
			return object.eResource();
		return null;
	}
	
	/**
	 * Gets the object managed by this InsertionAdapter.
	 * 
	 * @return the object
	 */
	public EObject getObject() {
		return object;
	}
	
	/**
	 * Gets the object's containment feature managed by this InsertionAdapter
	 * 
	 * @return the containment feature
	 */
	public EStructuralFeature getFeature() {
		return feature;
	}
	
	/**
	 * Gets the object to be inserted into the containment feature.
	 * 
	 * @return the value
	 */
	public EObject getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
	 */
	@Override
	public EditingDomain getEditingDomain() {
		getResource();
		if (resource!=null)
			return AdapterFactoryEditingDomain.getEditingDomainFor(resource);
		return null;
	}
}
