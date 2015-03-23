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

package org.eclipse.bpmn2.modeler.core.adapters;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.model.ModelDecoratorAdapter;
import org.eclipse.bpmn2.modeler.core.utils.ErrorUtils;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Item property provider for a specific feature of an object. Clients may replace the default implementation
 * by defining their own ExtendPropertiesAdapter and setting their own FeatureDescriptors for features that
 * need special handling.
 * 
 * See also the <propertyExtension> element of the org.eclipse.bpmn2.modeler.runtime extension point.
 */
public class FeatureDescriptor<T extends EObject> extends ObjectDescriptor<T> {

	/** The feature for this ObjectDescriptor */
	protected EStructuralFeature feature;
	/** Flag that determines if a Text feature should be rendered as a mulitline text widget */
	protected int multiline = 0; // -1 = false, +1 = true, 0 = unset
	/** Default list of values if this feature is a multi-valued object */
	protected Hashtable<String, Object> choiceOfValues; // for static lists
	
	/**
	 * Construct a new FeatureDescriptor for the given feature of the given object.
	 * 
	 * @param owner - the ExtendedPropertiesAdapter that owns this FeatureDescriptor
	 * @param object - an EObject subclass
	 * @param feature - this must be a defined EStructuralFeature of the above EObject.
	 */
	public FeatureDescriptor(ExtendedPropertiesAdapter<T> owner, T object, EStructuralFeature feature) {
		super(owner, object);
		this.feature = feature;
		Assert.isNotNull(object);
		Assert.isNotNull(feature);
	}
	
	/**
	 * Gets the feature whose properties are managed by this class.
	 * 
	 * @return an EStructuralFeature
	 */
	public EStructuralFeature getFeature() {
		return feature;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor#getLabel()
	 */
	public String getLabel() {
		String s = ModelDecorator.getLabel(feature);
		if (s!=null) {
			return s;
		}
		if (label==null) {
			IItemPropertyDescriptor propertyDescriptor = getPropertyDescriptor(feature);
			if (propertyDescriptor != null)
				label = propertyDescriptor.getDisplayName(object);
			else {
				// If the referenced type is an EObject, we'll get an "E Class" label
				// so use the feature name instead.
//				if (feature instanceof EReference && !(getEType().getInstanceClass()==EObject.class))
//					label = ExtendedPropertiesProvider.getLabel(getEType());
//				else
					label = ModelUtil.toCanonicalString(feature.getName());
			}
		}
		return label;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor#getTextValue()
	 */
	@Override
	public String getTextValue() {
		if (textValue==null) {
			String t = null;
			// derive text from feature's value: default behavior is
			// to use the "name" attribute if there is one;
			// if not, use the "id" attribute;
			// fallback is to use the feature's toString()
			EObject o = null;
			EStructuralFeature f = null;
			if (feature!=null) {
				Object value = object.eGet(feature); 
				if (value instanceof EObject) {
					o = (EObject)value;
				}
				else if (value!=null)
					t = value.toString();
			}
			if (t==null && o!=null) {
				f = o.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
				if (f!=null) {
					String name = (String)o.eGet(f);
					if (name!=null && !name.isEmpty())
						t = name;
				}
				else if (o instanceof FormalExpression) {
					t = ModelUtil.getExpressionBody((FormalExpression)o);
				}
			}
			if (t==null && o!=null) {
				f = o.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
				if (f!=null) {
					Object id = o.eGet(f);
					if (id!=null && !id.toString().isEmpty())
						t = id.toString();
				}
			}
			return t == null ? "" /*ModelUtil.getLabel(object)*/ : t; //$NON-NLS-1$
		}
		return textValue == null ? "" : textValue; //$NON-NLS-1$
	}

	/**
	 * Set the list of valid values for this feature. This assumes the feature is
	 * multi-valued.
	 * 
	 * @param choiceOfValues a list of text/value pairs. The text string is displayed by
	 * the UI in a selection list (Combo box) and the value is the actual feature value
	 * that corresponds to that string. 
	 */
	public void setChoiceOfValues(Hashtable<String, Object> choiceOfValues) {
		this.choiceOfValues = choiceOfValues;
	}

	/**
	 * Convenience method to set choice of values from an object list.
	 * See also {@link getChoiceOfValues()}
	 * 
	 * @param values  a list of text/value pairs. The text string is displayed by
	 * the UI in a selection list (Combo box) and the value is the actual feature value
	 * that corresponds to that string.
	 */
	public void setChoiceOfValues(Collection values) {
		if (values!=null) {
			choiceOfValues = new Hashtable<String,Object>();
			Iterator iter = values.iterator();
			while (iter.hasNext()) {
				Object value = iter.next();
				if (value!=null) {
					String text = getChoiceString(value);
					while (choiceOfValues.containsKey(text))
						text += " "; //$NON-NLS-1$
					choiceOfValues.put(text, value);
				}
			}
		}
	}
	
	/**
	 * Returns a list of name-value pairs for display in a combo box or selection list.
	 * The String is what gets displayed in the selection list, while the Object is
	 * implementation-specific: this can be a reference to an element, string or whatever.
	 * The implementation is responsible for interpreting this value by overriding the
	 * setValue() method, and must update the object feature accordingly.
	 * 
	 * @return the list of text/value pairs.
	 */
	public Hashtable<String, Object> getChoiceOfValues() {
		if (choiceOfValues==null) {
			List<String> names = null;
			Collection values = null;
			
			try {
				IItemPropertyDescriptor propertyDescriptor = getPropertyDescriptor(feature);
				if (propertyDescriptor!=null) {
					values = propertyDescriptor.getChoiceOfValues(object);
				}
			}
			catch (Exception e) {
				// ignore exceptions if we fail to resolve proxies;
				// e.g. and instance of a DynamicEObjectImpl with a bogus
				// URI is used for ItemDefinition.structureRef
				// fallback is to do our own search
			}

			if (values==null)
				values = ModelUtil.getAllReachableObjects(object, feature);
			
			if (values!=null) {
				Hashtable<String,Object> choices = new Hashtable<String,Object>();
				Iterator iter = values.iterator();
				while (iter.hasNext()) {
					Object value = iter.next();
					if (value!=null) {
						String text = getChoiceString(value);
						if (text==null)
							text = ""; //$NON-NLS-1$
						while (choices.containsKey(text))
							text += " "; //$NON-NLS-1$
						choices.put(text, value);
					}
				}
				return choices;
			}
		}
		return choiceOfValues;
	}
	
	/**
	 * Gets the text representation of the given object. This uses
	 * {@code ObjectDescriptor#getTextValue()} if the value is an EObject and
	 * can be adapted to an {@link ExtendedPropertiesAdapter}.
	 * 
	 * @param value the value
	 * @return text representation of the value
	 */
	public String getChoiceString(Object value) {
		if (value instanceof EObject) {
			EObject eObject = (EObject)value;
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eObject);
			if (adapter!=null)
				return adapter.getObjectDescriptor().getTextValue();
			return ModelUtil.toCanonicalString( eObject.eClass().getName() );
		}
		return value.toString();
	}

	/**
	 * Sets the MultiLine attribute.
	 * 
	 * @param multiline
	 */
	public void setMultiLine(boolean multiline) {
		this.multiline = multiline ? 1 : -1;
	}
	
	/**
	 * Gets the MultiLine attribute.
	 * 
	 * @return true if the feature should be rendered in a MultiLine text widget,
	 * false if a single line text widget should be used.
	 */
	public boolean isMultiLine() {
		if (multiline==0) {
			IItemPropertyDescriptor propertyDescriptor = getPropertyDescriptor(feature);
			if (propertyDescriptor!=null)
				multiline = propertyDescriptor.isMultiLine(object) ? 1 : -1;
		}
		return multiline == 1;
	}
	
	/**
	 * Check if the feature is a containment list.
	 * 
	 * @return true if the feature is a containment list, false if not.
	 */
	public boolean isList() {
		return
				feature.isMany() &&
				feature instanceof EReference &&
				((EReference)feature).isContainment();
	}

	/**
	 * Gets the feature's type (an EClassifier)
	 * 
	 * @return the feature type
	 */
	public EClassifier getEType() {
		return feature.getEType();
	}

	/**
	 * Create a new instance of the feature and set it in the object managed by
	 * this FeatureDesciptor.
	 * 
	 * @param resource the EMF Resource in which to create the new object.
	 * @param eclass an optional type for the new object in case the feature
	 *            type is abstract. Note that this must be a subtype of the
	 *            feature type as returned by {@code getEType()}.
	 * @return the new object.
	 */
	public EObject createFeature(Resource resource, EClass eclass) {
		EObject newFeature = null;
		if (eclass==null)
			eclass = (EClass)getEType();
		
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eclass);
		if (adapter!=null) {
			if (resource==null)
				resource = object.eResource();
			newFeature = adapter.getObjectDescriptor().createObject(resource, eclass);
			// can we set the new object into the parent object?
			if (newFeature.eContainer()!=null || // the new object is contained somewhere
				feature instanceof EAttribute || // the new object is an attribute
				// the feature is a containment reference which means the this.object owns it
				(feature instanceof EReference && ((EReference)feature).isContainment()))
			{
				if (object.eGet(feature) instanceof List) {
					((List)object.eGet(feature)).add(newFeature);
				}
				else
					object.eSet(feature, newFeature);
			}
		}
		return newFeature;
	}

	// NOTE: getValue() and setValue() must be symmetrical; that is, setValue()
	// must be able to handle the object type returned by getValue(), although
	// setValue() may also know how to convert from other types, e.g. String,
	// Integer, etc.
	/**
	 * Gets the value of the object's feature.
	 * 
	 * @return value of the object's feature.
	 */
	public Object getValue() {
		return getValue(-1);
	}

	/**
	 * Gets the value of the object's feature at the given list index.
	 * 
	 * @param index the list index. If less than 0 returns the first list item.
	 * @return value of the object's feature.
	 */
	public Object getValue(int index) {
		if (hasStructuralFeatureFeature(object, feature)) {
			if (index >= 0 && isList()) {
				return ((List) object.eGet(feature)).get(index);
			}
			return object.eGet(feature);
		}
		if (isAnyAttribute(object, feature)) {
			Object value = null;
			try {
				value = object.eGet(feature);
			} catch (Exception e1) {
			}
			return value;
		}
		if (isExtensionAttribute(object, feature)) {
			List result = ModelDecorator.getAllExtensionAttributeValues(object, feature);
			if (result.size() == 0) {
				return null;
			}
			if (index >= 0)
				return result.get(index);
			return result.get(0);
		}
		return null;
	}

	/**
	 * Gets the List represented by the feature. Note that the feature must be a
	 * list and therefore {@code isList()} must be true.
	 * 
	 * @return a List of values or an empty list.
	 */
	public List<Object> getValueList() {
		if (isList()) {
			return ((List)object.eGet(feature));
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * Sets the value for the object's feature.
	 * 
	 * @param value the value
	 * @return true if the value is valid, false if not or if the value could not be set.
	 */
	public boolean setValue(Object value) {
		return setValue(value, -1);
	}

	
	/**
	 * Sets the value for the object's feature at the given list index.
	 * 
	 * @param value the value
	 * @param index the list index. If less than 0 sets the first list item.
	 * @return true if the value is valid, false if not or if the value could not be set.
	 */
	public boolean setValue(Object value, final int index) {
		try {
			InsertionAdapter.executeIfNeeded(object);
			if (value instanceof EObject) {
				// make sure the new object is added to its control first
				// so that it inherits the control's Resource and EditingDomain
				// before we try to change its value.
				InsertionAdapter.executeIfNeeded((EObject)value);
			}
			if (value instanceof String) {
				// handle String to EDataType conversions
				if (((String) value).isEmpty()) {
					if (!(feature.getDefaultValue() instanceof String))
						value = null;
				}
				else {
					if (getEType() instanceof EDataType) {
						EDataType eDataType = (EDataType)getEType();
						try {
							EFactory factory = eDataType.getEPackage().getEFactoryInstance();
							value = factory.createFromString(eDataType, (String)value);
						}
						catch (Exception e)
						{
							EFactory factory = EcorePackage.eINSTANCE.getEFactoryInstance();
							value = factory.createFromString(eDataType, (String)value);
						}
					}
				}
			}
			
			TransactionalEditingDomain domain = getEditingDomain(object);
			if (domain!=null) {
				final Object v = value;
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						internalSet(object,feature,v, index);
						internalPostSet(v);
					}
				});
			}
			else {
				internalSet(object,feature,value, index);
				internalPostSet(value);
			}
		} catch (Exception e) {
			ErrorUtils.showErrorMessage(e.getMessage());
			Activator.logError(e);
			return false;
		}
		return true;
	}
	
	/**
	 * Check if the given feature in the specified object is a dynamic feature.
	 * 
	 * @param object
	 * @param feature
	 * @return true if the feature is <b>not</b> a dynamic feature, i.e. it is defined in the
	 * containing object's EClass feature list.
	 */
	private boolean hasStructuralFeatureFeature(EObject object, EStructuralFeature feature) {
		String name = feature.getName();
		if (object instanceof EClass)
			return ((EClass)object).getEStructuralFeature(name) != null;
		return object.eClass().getEStructuralFeature(name) != null;
	}
	
	/**
	 * Check if the given feature in the specified object is a dynamic attribute.
	 * 
	 * @param object
	 * @param feature
	 * @return true if the feature is an attribute, and has been defined as an extension
	 * by the Target Runtime plug-in. See also {@code ModelExtensionDescriptor}.
	 */
	private boolean isAnyAttribute(EObject object, EStructuralFeature feature) {
		if (hasStructuralFeatureFeature(object,feature))
			return false;
		String name = feature.getName();
		feature = ModelDecorator.getAnyAttribute(object, name);
		if (feature!=null)
			return true;
		return false;
	}

	/**
	 * Check if the given feature in the specified object is a dynamic element.
	 * 
	 * @param object
	 * @param feature
	 * @return true if the feature is an element, and has been defined as an extension
	 * by the Target Runtime plug-in. See also {@code ModelExtensionDescriptor}.
	 */
	private boolean isExtensionAttribute(EObject object, EStructuralFeature feature) {
		if (hasStructuralFeatureFeature(object,feature))
			return false;
		String name = feature.getName();
		feature = ModelDecorator.getExtensionAttribute(object, name);
		if (feature!=null)
			return true;
		return false;
	}

	private ModelDecorator getModelDecorator() {
		ModelDecorator modelDecorator = ModelDecoratorAdapter.getModelDecorator(feature.getEContainingClass().getEPackage());
		return modelDecorator;
	}
	
	/**
	 * Set the value of the feature managed by this FeatureDescriptor. If the
	 * feature is a dynamic feature, the value is set in either the
	 * "anyAttribute" feature map if it is an attribute, or in the
	 * {@code BaseElementImpl.extensionValues} container.
	 * 
	 * This method may be overridden and will be wrapped in an EMF Transaction by
	 * {@link setValue(Object,int)) if necessary.
	 * 
	 * @param object the object
	 * @param feature the feature. This must be an extension defined by the Target Runtime.
	 * @param value the value to set for the object feature
	 * @param index the list index if the feature is a list. If this is less than 0, set the first list item.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void internalSet(T object, EStructuralFeature feature, Object value, int index) {
		if (hasStructuralFeatureFeature(object,feature) || isList()) {
			if (feature.isMany()) {
				// NB: setting a List item to null into a List will clear the List!
				if (value==null)
					((List)object.eGet(feature)).clear();
				else if (index<0)
					((List)object.eGet(feature)).add(value);
				else
					((List)object.eGet(feature)).set(index,value);
			}
			else
				object.eSet(feature, value);
		}
		else {
			// the feature does not exist in this object, so we either need to
			// create an "anyAttribute" entry or, if the object is an ExtensionAttributeValue,
			// create an entry in its "value" feature map.
			String name = feature.getName();
			if (feature instanceof EAttribute) {
				EStructuralFeature f = ModelDecorator.getAnyAttribute(object, name);
				if (f!=null) {
					object.eSet(f, value);
				}
				else {
					String namespace = ExtendedMetaData.INSTANCE.getNamespace(feature);
					String type = feature.getEType().getName();
					ModelDecorator modelDecorator = getModelDecorator();
					if (modelDecorator==null)
						modelDecorator = getModelDecorator();
					modelDecorator.addAnyAttribute(object, namespace, name, type, value);
				}
			}
			else {
				// FIXME: access to ExtensionAttributeValues MUST go through the ModelExtensionDescriptor's
				// modelDecorator so that we can properly find, and optionally create and initialize
				// the EPackage that contains the extensions
				ModelDecorator.addExtensionAttributeValue(object, feature, value, index, false);
			}
		}
	}
	
	/**
	 * Performs additional initialization of the new feature value if necessary.
	 * 
	 * The default implementation initializes the ID of the new feature value, if it has one.
	 * 
	 * This method may be overridden and will be wrapped in an EMF Transaction by
	 * {@link setValue(Object,int)) if necessary.
	 * 
	 * @param value the feature value that was set.
	 */
	protected void internalPostSet(Object value) {
		if (value instanceof EObject) {
			ModelUtil.setID((EObject)value);
			if (value instanceof RootElement && ((RootElement)value).eContainer()==null) {
				// stuff all root elements into Definitions.rootElements
				final Definitions definitions = ModelUtil.getDefinitions(object);
				if (definitions!=null) {
					if (!definitions.getRootElements().contains(value))
						definitions.getRootElements().add((RootElement)value);
				}
			}
		}
	}

	/**
	 * Unsets the feature by setting it to its default value.
	 */
	public void unset() {
		setValue(feature.getDefaultValue());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Object thisValue = object.eGet(feature);
		
		if (thisValue==null && obj==null)
			return true;
		
		if (thisValue instanceof EObject && obj instanceof EObject) {
			return ExtendedPropertiesAdapter.compare((EObject)thisValue, (EObject)obj, false);
		}
		
		if (thisValue!=null && obj!=null)
			return thisValue.equals(obj);
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor#similar(java.lang.Object)
	 */
	@Override
	public boolean similar(Object obj) {
		Object thisValue = object.eGet(feature);
		
		if (thisValue==null && obj==null)
			return true;
		
		if (thisValue instanceof EObject && obj instanceof EObject) {
			return ExtendedPropertiesAdapter.compare((EObject)thisValue, (EObject)obj, true);
		}
		
		if (thisValue!=null && obj!=null)
			return thisValue.equals(obj);
		
		return false;
	}

	/**
	 * This method has been deprecated. Use {@code getChoiceOfValues()} instead.
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public Hashtable<String, Object> getChoiceOfValues(Object context) {
		return getChoiceOfValues();
	}

}
