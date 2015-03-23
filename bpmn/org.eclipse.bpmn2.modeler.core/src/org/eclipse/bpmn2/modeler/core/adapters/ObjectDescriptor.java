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

import java.util.Hashtable;

import org.eclipse.bpmn2.modeler.core.features.GraphitiConstants;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.osgi.util.NLS;

/**
 * Item property provider for a specific object. Clients may replace the default
 * implementation by defining their own ExtendPropertiesAdapter and setting
 * their own ObjectDescriptors for objects that need special handling.
 * 
 * See also the <pre><propertyExtension></pre> element of the
 * {@code org.eclipse.bpmn2.modeler.runtime} extension point.
 */
public class ObjectDescriptor<T extends EObject> {

	/** the object managed by this {@code ObjectDescriptor} */
	protected T object;
	/** a default text label used by the UI in Property Sheets and dialogs */
	protected String label;
	/** a default string representation for this object's value */ 
	protected String textValue;
	/** the {@link ExtendedPropertiesAdapter} that owns this {@code ObjectDescriptor} */
	protected ExtendedPropertiesAdapter<T> owner;

	protected Hashtable<String, Object> properties = null;

	public ObjectDescriptor(ExtendedPropertiesAdapter<T> owner, T object) {
		this.owner = owner;
		this.object = object;
	}

//	public ObjectDescriptor(T object) {
//		this.object = object;
//	}
	
	/**
	 * Gets the {@link ExtendedPropertiesAdapter} owner for this ObjectDescriptor.
	 * 
	 * @return the owner
	 */
	public ExtendedPropertiesAdapter<T> getOwner() {
		return owner;
	}

	/**
	 * Sets the {@link ExtendedPropertiesAdapter} owner for this ObjectDescriptor.
	 * 
	 * @param owner
	 */
	public void setOwner(ExtendedPropertiesAdapter<T> owner) {
		this.owner = owner;
	}
	
	/**
	 * Gets the object managed by this ObjectDescriptor.
	 * 
	 * @return the object.
	 */
	public T getObject() {
		return object;
	}
	
	/**
	 * Sets the object.
	 * 
	 * @param object the object.
	 */
	public void setObject(T object) {
		this.object = object;
	}
	
	/**
	 * Sets the Label for this object.
	 * 
	 * @param label the label.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Gets the Label for this object. The default implementation returns the object's type name 
	 * 
	 * @return text label for the object.
	 */
	public String getLabel() {
		String s = ModelDecorator.getLabel(object.eClass());
		if (s!=null) {
			return s;
		}
		if (label==null) {
			EClass eclass = (object instanceof EClass) ?
					(EClass)object :
					object.eClass();
			label = ModelUtil.toCanonicalString(eclass.getName());
		}
		return label;
	}
	
	/**
	 * Sets the text representation of the object managed by this ObjectDescriptor.
	 * 
	 * @param textValue the text string representation of this object.
	 */
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	
	/**
	 * Gets the text representation of the object managed by this ObjectDescriptor.
	 * 
	 * @return a text string representation of this object.
	 */
	public String getTextValue() {
		if (textValue==null) {
			// derive text from feature's value: default behavior is
			// to use the "name" attribute if there is one;
			// if not, use the "id" attribute;
			// fallback is to use the feature's toString()
			String text = ModelUtil.toCanonicalString(object.eClass().getName());
			Object value = null;
			EStructuralFeature f = null;
			f = object.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
			if (f==null) {
				f = ModelDecorator.getAnyAttribute(object, "name"); //$NON-NLS-1$
			}
			if (f!=null) {
				value = object.eGet(f);
				if (value==null || value.toString().isEmpty())
					value = null;
			}
			if (value==null) {
				f = object.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
				if (f!=null) {
					value = object.eGet(f);
					if (value==null || value.toString().isEmpty())
						value = null;
				}
			}
			if (value==null)
				value = NLS.bind(Messages.ObjectDescriptor_Unnamed, text);
			return (String)value;
		}
		return textValue;
	}

	/**
	 * Convenience method for
	 * {@code getPropertyDescriptor(Object,EStructuralFeature)} for returning
	 * the feature Property Descriptor for the object managed by this
	 * ObjectDescriptor.
	 * 
	 * @param feature the feature
	 * @return an ItemPropertyDescriptor.
	 */
	protected IItemPropertyDescriptor getPropertyDescriptor(EStructuralFeature feature) {
		return getPropertyDescriptor(object, feature);
	}

	/**
	 * Gets the EMF-generated Property Descriptor for the given object and feature.
	 * 
	 * @param object the object
	 * @param feature the feature
	 * @return an ItemPropertyDescriptor.
	 */
	protected IItemPropertyDescriptor getPropertyDescriptor(T object, EStructuralFeature feature) {
		ItemProviderAdapter adapter = null;
		for (Adapter a : object.eAdapters()) {
			if (a instanceof ItemProviderAdapter) {
				adapter = (ItemProviderAdapter)a;
				break;
			}
		}
		if (adapter!=null)
			return adapter.getPropertyDescriptor(object, feature);
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object otherObject) {
		EObject thisObject = this.object;
		if (otherObject instanceof EObject) {
			// compare feature values of both EObjects:
			// this should take care of most of the BPMN2 elements
			return ExtendedPropertiesAdapter.compare(thisObject, (EObject)otherObject, false);
		}
		return super.equals(otherObject);
	}

	/**
	 * Convenience method to check if a given object is similar to the object
	 * managed by this ObjectDescriptor.
	 * 
	 * @param other
	 * @return
	 */
	public boolean similar(Object other) {
		EObject thisObject = this.object;
		if (other instanceof EObject) {
			// compare feature values of both EObjects:
			// this should take care of most of the BPMN2 elements
			return ExtendedPropertiesAdapter.compare(thisObject, (EObject)other, true);
		}
		return super.equals(other);
	}
	
	/**
	 * Convenience method to compare the object managed by this ObjectDescriptor
	 * with the given object.
	 * 
	 * @param other another object to compare against this one.
	 * @param similar if true, then ignore IDs when doing the compare.
	 * @return true if the objects are equal.
	 */
	protected boolean compare(EObject other, boolean similar) {
		return ExtendedPropertiesAdapter.compare(object, other, similar);
	}

	/**
	 * Some methods accept java Objects as a context variable. In many cases (especially the
	 * default implementations) the context object must have the same type as the specialized
	 * class. 
	 * 
	 * @param context
	 * @return the context variable if it has the same type as this.object, or this.object if not.
	 */
	@SuppressWarnings("unchecked")
	protected T adopt(Object context) {
		T result = (this.object.getClass().isInstance(context)) ? (T)context : this.object;
		return result;
	}

	/**
	 * Gets the Editing Domain for the given EObject. See also
	 * {@link AdapterFactoryEditingDomain}
	 * 
	 * If an Editing Domain can not be determined for the given context object,
	 * then consult our {@link ExtendedPropertiesAdapter} owner.
	 * 
	 * @param context an EObject which must be contained in an EMF Resource.
	 * @return
	 */
	public TransactionalEditingDomain getEditingDomain(EObject context) {
		T object = adopt(context);
		// check the EObject's contained Resource
		EditingDomain result = AdapterFactoryEditingDomain.getEditingDomainFor(object);
		if (result == null) {
			if (object instanceof IEditingDomainProvider) {
				// the object itself may be a provider
				result = ((IEditingDomainProvider) object).getEditingDomain();
			}
			if (result == null) {
				// check the object's adapters for providers
				IEditingDomainProvider provider = AdapterUtil.adapt(object, IEditingDomainProvider.class);
				if (provider!=null) {
					result = provider.getEditingDomain();
				}
				if (result == null) {
					// finally, check our adapter factory
					result = owner.getEditingDomain();
				}
			}
		}
		// it's gotta be a Transactional Editing Domain or nothing!
		if (result instanceof TransactionalEditingDomain)
			return (TransactionalEditingDomain)result;
		return null;
	}

	/**
	 * Create a new instance of the object that is managed by this
	 * ObjectDescriptor.
	 * 
	 * @param eclass an optional type for the new object. Note that this must be
	 *            a subtype of the feature type as returned by
	 *            {@code getEType()}.
	 * @return the new object.
	 */
	public T createObject(EClass eclass) {
		return createObject(getResource(),eclass);
	}
	
	/**
	 * Gets the EMF Resource managed by our {@link ExtendedPropertiesAdapter}
	 * 
	 * @return and EMF Resource or null if not set.
	 */
	public Resource getResource() {
		return owner.getResource();
	}
	
	/**
	 * Create a new instance of the object that is managed by this ObjectDescriptor.
	 *  
	 * @param resource the EMF Resource in which to create the new object.
	 * @param eclass an optional type for the new object. Note that this must be
	 *            a subtype of the feature type as returned by
	 *            {@code getEType()}.
	 * @return the new object.
	 */
	@SuppressWarnings("unchecked")
	public T createObject(Resource resource, EClass eclass) {
		
		EClass eClass = null;
		if (eclass instanceof EClass) {
			eClass = (EClass)eclass;
		}
		else if (eclass instanceof EObject) {
			eClass = ((EObject)eclass).eClass();
		}
		else {
			eClass = object.eClass();
		}
		Assert.isTrue(object.eClass().isSuperTypeOf(eClass));

		if (resource==null)
			resource = getResource();

		// set the Resource into the Factory's adapter temporarily for use during
		// object construction and initialization (@see ModelExtensionDescriptor)
		EFactory factory = eClass.getEPackage().getEFactoryInstance();
		ObjectPropertyProvider adapter = ObjectPropertyProvider.adapt(factory, resource);
		Object value = owner.getProperty(GraphitiConstants.CUSTOM_ELEMENT_ID);
		if (value!=null)
			adapter.setProperty(GraphitiConstants.CUSTOM_ELEMENT_ID, value);
		T newObject = null;
		synchronized(factory) {
			newObject = (T) factory.create(eClass);
		}
		
		// if the object has an "id", assign it now.
		String id = ModelUtil.setID(newObject,resource);
		// also set a default name
		EStructuralFeature feature = newObject.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature!=null && !newObject.eIsSet(feature)) {
			if (id!=null)
				newObject.eSet(feature, ModelUtil.toCanonicalString(id));
			else {
				String name = ModelUtil.toCanonicalString(newObject.eClass().getName());
				newObject.eSet(feature, NLS.bind(Messages.ObjectDescriptor_New, name));
			}
		}
		
		adapter = ExtendedPropertiesAdapter.adapt(newObject);
		if (adapter!=null)
			adapter.setResource(resource);
		
		return newObject;
	}


	/**
	 * Sets the property.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void setProperty(String key, Object value) {
		if (value==null) {
			if (properties!=null) {
				properties.remove(key);
				if (properties.isEmpty())
					properties = null;
			}
		}
		else {
			if (properties==null)
				properties = new Hashtable<String, Object>();
			properties.put(key, value);
		}
	}
	
	/**
	 * Gets the property.
	 *
	 * @param key the key
	 * @return the property
	 */
	public Object getProperty(String key) {
		if (properties==null)
			return null;
		return properties.get(key);
	}
}
