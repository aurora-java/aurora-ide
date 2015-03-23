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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectPropertyProvider;
import org.eclipse.bpmn2.modeler.core.model.ModelDecorator;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.bpmn2.modeler.core.utils.SimpleTreeIterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Target Runtime Extension Descriptor class for BPMN2 model extension attributes and elements.
 * Instances of this class correspond to <modelExtension> extension elements in the extension's plugin.xml
 * See the description of the "modelExtension" element in the org.eclipse.bpmn2.modeler.runtime extension point schema. 
 */
@SuppressWarnings("rawtypes")
public class ModelExtensionDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "modelExtension"; //$NON-NLS-1$
	
	/**
	 * Container class for name/value pairs.
	 * Instances of this class correspond to <property> extension elements in the extension's plugin.xml
	 * See the description of the "property" element in the org.eclipse.bpmn2.modeler.runtime
	 * extension point schema. 
	 */
	public static class Property extends SimpleTreeIterator<Property> {
		public Property parent;
		public String name;
		public String label;
		public String description;
		public List<Object>values;
		public String ref;
		public String type;
		boolean isMany;
		
		public Property() {
			this.name = "unknown"; //$NON-NLS-1$
		}
		
		public Property(Property parent, String name, String description) {
			super();
			this.parent = parent;
			this.name = name;
			this.description = description;
		}
		
		public void setType(String t) {
			if (t!=null && t.contains("*")) { //$NON-NLS-1$
				isMany = true;
				t = t.replaceAll("\\*", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			type = t;
		}
		
		public List<Object> getValues() {
			if (values==null) {
				values = new ArrayList<Object>();
			}
			return values;
		}

		public String getFirstStringValue() {

			if (!this.getValues().isEmpty()) {
				// simple attribute - find a String value for it
				for (Object propValue : this.getValues()) {
					if (propValue instanceof String) {
						return (String)propValue;
					}
					else if (propValue instanceof Property) {
						String s = ((Property)propValue).getFirstStringValue();
						if (s!=null)
							return s;
					}
				}
			}
			return null;
		}

		protected List<Property> getChildren() {
			List<Property> children = new ArrayList<Property>();
			if (values!=null) {
				for (Object child : values) {
					if (child instanceof Property)
						children.add((Property)child);
				}
			}
			return children;
		}

		@Override
		public Iterator<Property> iterator() {
			return new TreeIterator(getChildren());
		}
	}

	/**
	 * Container class for property values.
	 * Instances of this class correspond to <value> extension elements in the extension's plugin.xml
	 * See the description of the "value" element in the org.eclipse.bpmn2.modeler.runtime
	 * extension point schema. 
	 */
	public static class Value {
		
		static int ID = 0;
		String id;
		public List<Object>values;
		
		public Value() {
			setDefaultId();
		}
		
		public Value(String id) {
			if (id==null || id.isEmpty())
				setDefaultId();
			else
				this.id = id;
		}
		
		public List<Object> getValues() {
			if (values==null) {
				values = new ArrayList<Object>();
			}
			return values;
		}
		
		private void setDefaultId() {
			id = "V-" + ID++; //$NON-NLS-1$
		}
	}

	/**
	 * Container class for object initialization info.
	 * Object initialization must be performed after and object has been fully
	 * populated with extension attributes and elements by populateObject().
	 * This is necessary because the AnyType object expects the referenced EClass
	 * to be fully constructed when eSet(), eGet() or eIsSet() are invoked on the
	 * EClass features.
	 */
	private class Initializer {
		public ExtendedPropertiesAdapter adapter;
		public EStructuralFeature feature;
		public Object value;

		public Initializer(ExtendedPropertiesAdapter adapter, EStructuralFeature feature, Object value) {
			this.adapter = adapter;
			this.feature = feature;
			this.value = value;
		}
	}
	
	/**
	 * List of Initializers. The list may be partially executed to ensure that
	 * backward references to objects by a Property.ref exist.
	 * See the description of the "property.ref" attribute in the org.eclipse.bpmn2.modeler.runtime
	 * extension point schema. 
	 */
	@SuppressWarnings("serial")
	private class InitializerList extends ArrayList<Initializer> {
		public boolean initialize;
		
		public void add(ExtendedPropertiesAdapter adapter, EStructuralFeature feature, Object value) {
			super.add( new Initializer(adapter, feature, value) );
		}
		
		public void execute() {
			for (Initializer item : this) {
				// Skip initialization of Enums
				if (item.feature.getEType() instanceof EEnum)
					continue;
				if (initialize)
					item.adapter.getFeatureDescriptor(item.feature).setValue(item.value);
			}
			clear();
		}
		
		/**
		 * Run all initializers up to and including the ones for the given object but not beyond.
		 * @param object
		 */
		public boolean execute(EObject object) {
			int last = -1;
			for (int i=size()-1; i>=0; --i) {
				Initializer item = get(i);
				if (item.adapter.getTarget() == object) {
					last = i;
					break;
				}
			}
			
			while (last>=0) {
				Initializer item = get(0);
				remove(0);
				
				// Skip initialization of Enums
				if (item.feature.getEType() instanceof EEnum)
					continue;
				if (initialize)
					item.adapter.getFeatureDescriptor(item.feature).setValue(item.value);
				--last;
			}
			
			return initialize;
		}
	}
	
	/**
	 * This adapter has been deprecated. Use BaseRuntimeExtensionDescriptor#getDescriptor(EObject,Class) instead
	 * @deprecated
	 */
	public class ModelExtensionAdapter extends AdapterImpl {

		ModelExtensionDescriptor descriptor;
		
		public ModelExtensionAdapter(ModelExtensionDescriptor descriptor) {
			super();
			this.descriptor = descriptor;
		}
		
		public Property getProperty(String name) {
			return descriptor.getProperty(name);
		}
		
		public List<Property> getProperties(String path) {
			return descriptor.getProperties(path);
		}
		
		public ModelExtensionDescriptor getDescriptor() {
			return descriptor;
		}
	}

	protected String name;
	protected String uri;
	protected String type;
	protected String description;
	protected IObjectDecorator objectDecorator;
	protected List<Property> properties = new ArrayList<Property>();
	protected ModelDecorator modelDecorator;
	// these are shared instance variables because this ModelExtensionDescriptor is shared
	// among all instances of the BPMN2Editor. We need to take care to clear these values
	// once populateObject() is complete. An alternative would have been to pass these things
	// on the call stack down several stack frames which is costly.
	private Resource containingResource;
	private EObject modelObject;
	private InitializerList initializers = new InitializerList();

	public ModelExtensionDescriptor(IConfigurationElement e) {
		super(e);
		name = e.getAttribute("name"); //$NON-NLS-1$
		uri = e.getAttribute("uri"); //$NON-NLS-1$
		type = e.getAttribute("type"); //$NON-NLS-1$
		description = e.getAttribute("description"); //$NON-NLS-1$
		if (e.getAttribute("decorator")!=null) {
			try {
				objectDecorator = (IObjectDecorator) e.createExecutableExtension("decorator"); //$NON-NLS-1$
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		getModelExtensionProperties(null, this, e);
	}

	@Override
	public void setConfigFile(IFile configFile) {
		super.setConfigFile(configFile);
		if (configFile!=null) {
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(configFile.getProject());
			prefs.loadDefaults(targetRuntime, Bpmn2Preferences.PREF_MODEL_ENABLEMENT);
		}
	}

	public void dispose() {
		// remove the ModelEnablement classes and features that may
		// have been defined in this Model Extension
		if (configFile!=null) {
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(configFile.getProject());
			prefs.unloadDefaults(targetRuntime, Bpmn2Preferences.PREF_MODEL_ENABLEMENT);
		}
		super.dispose();
		if (configFile!=null) {
			Bpmn2Preferences prefs = Bpmn2Preferences.getInstance(configFile.getProject());
			prefs.loadDefaults(targetRuntime, Bpmn2Preferences.PREF_MODEL_ENABLEMENT);
		}
		
		getModelDecorator().dispose();
	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	@Deprecated
	public ModelExtensionDescriptor(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public List<Property> getProperties() {
		return properties;
	}
	
	protected Object getModelExtensionProperties(Property parent, ModelExtensionDescriptor ct, IConfigurationElement e) {
		
		String elem = e.getName();
		if ("value".equals(elem)) { //$NON-NLS-1$
			String id = e.getAttribute("id"); //$NON-NLS-1$
			Value val = new Value(id);
			for (IConfigurationElement c : e.getChildren()) {
				Object propValue = getModelExtensionProperties(parent, null, c);
				val.getValues().add(propValue);
			}
			return val;
		}
		else if ("property".equals(elem)) { //$NON-NLS-1$
			String name = e.getAttribute("name"); //$NON-NLS-1$
			String label = e.getAttribute("label"); //$NON-NLS-1$
			String value = e.getAttribute("value"); //$NON-NLS-1$
			String ref = e.getAttribute("ref"); //$NON-NLS-1$
			String type = e.getAttribute("type"); //$NON-NLS-1$
			String description = e.getAttribute("description"); //$NON-NLS-1$
			Property prop = new Property(parent, name, description);
			prop.label = label;
			prop.setType(type);
			if (value!=null)
				prop.getValues().add(value);
			else if (ref!=null) {
				prop.ref = ref;
			}
			else if(e.getChildren().length > 0){
				Object o = getModelExtensionProperties(prop, null, e.getChildren()[0]);
				if (o instanceof Value)
					prop.getValues().addAll(((Value)o).getValues());
			}
			return prop;
		}
		else {
			for (IConfigurationElement c : e.getChildren()) {
				Object o = getModelExtensionProperties(parent, null, c);
				if (o instanceof Property && ct!=null)
					ct.getProperties().add((Property)o);
			}
		}
		return null;
	}

	/**
	 * Create and initialize an object of the given EClass. Initialization consists
	 * of assigning an ID and setting a default name if the EClass has those features.
	 * 
	 * @param eClass - type of object to create
	 * @return an initialized EObject
	 */
	public EObject createObject(EClass eClass) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eClass);
		if (adapter!=null) {
			adapter.setResource(containingResource);
			return adapter.getObjectDescriptor().createObject(eClass);
		}
		EPackage pkg = eClass.getEPackage();
		return pkg.getEFactoryInstance().create(eClass);
	}
	
	public ModelDecorator getModelDecorator() {
		if (modelDecorator==null) {
			String name = null;
			String nsPrefix = null;
			String nsURI = null;
			// get our EPackage defined in the <model> extension point (if there is one)
			EPackage pkg = getEPackage();
			if (uri==null && pkg == Bpmn2Package.eINSTANCE) {
				// can't decorate the BPMN2 package, so make up an extension URI
				// using the Target Runtime's targetNamespace
				nsURI = getRuntime().
						getRuntimeExtension().
						getTargetNamespace(Bpmn2DiagramType.NONE) + "/ext"; //$NON-NLS-1$
			}
			else if (uri!=null) {
				// This <modelExtension> extension point element defines an EPackage URI.
				// If it's the same as the <model> URI, then use the EPackage defined in <model>
				if (pkg==null || !uri.equals(pkg.getNsURI()))
					nsURI = uri;
			}
			
			if (nsURI!=null) {
				name = URI.createURI(nsURI).lastSegment();
				nsPrefix = name;
				modelDecorator = new ModelDecorator(name, nsPrefix, nsURI);
			}
			else {
				modelDecorator = new ModelDecorator(pkg);
			}
		}
		return modelDecorator;
	}
	
	public EObject populateObject(EObject object, Resource resource, boolean initialize) {
		try {
			containingResource = resource;
			modelObject = object;
			if (containingResource==null)
				containingResource = ObjectPropertyProvider.getResource(object);
			getModelDecorator();
			if (objectDecorator!=null) {
				if (objectDecorator.canApply(id, containingResource,modelObject)) {
					populateObject(modelObject, initialize);
					objectDecorator.apply(id,containingResource,modelObject);
				}
			}
			else {
				populateObject(modelObject, initialize);
			}
		}
		finally {
			containingResource = null;
			object = modelObject;
//			modelObject = null;
		}
		return object;
	}

	// FIXME: this is called in CreateCustomShapeFeature and CreateCustomConnectionFeature
	// this should not be necessary because it's already done in the Bpmn2Modeler factory,
	// but check to make sure. Try to decouple!
	public void populateObject(EObject object, boolean initialize) {
		try {
			modelObject = object;
			initializers.clear();
			initializers.initialize = initialize;
			
			populateObject(object, getProperties());
			adaptObject(object);
			if (initialize) {
				initializers.execute();
			}
		}
		catch (Exception e) {
			Activator.logError(e);
		}
		finally {
			initializers.clear();
//			modelObject = null;
		}
	}
	
	/**
	 * Populate the given EObject with a list of Property objects.
	 * 
	 * @param object - the object to initialize
	 * @param values - list of Property objects
	 */
	private void populateObject(EObject object, List<Property> properties) {
		
		for (Property prop : properties) {
			populateObject(object, prop);
		}
	}
	
	/**
	 * Return the value of the specified feature from the given EObject.
	 * If the feature is a list, return the indexed value.
	 *  
	 * @param object
	 * @param feature
	 * @param index
	 * @return the feature's value
	 */
	private Object getValue(EObject object, EStructuralFeature feature, int index) {
		if (feature.isMany()) {
			return ((EList)object.eGet(feature)).get(index<0 ? 0 : index);
		}
		return object.eGet(feature);
	}
	
	public EClass createEClass(String type) {
		EClass eClass = getModelDecorator().getEClass(type);
		if (eClass==null)
			eClass = getModelDecorator().createEClass(type);
		return eClass;
	}
	
	public EStructuralFeature createEFeature(EClass eClass, Property property) {
		EStructuralFeature feature = eClass.getEStructuralFeature(property.name);
		
		boolean isAttribute = true;
		EClassifier eClassifier = getModelDecorator().findEClassifier(property.type);
		if (eClassifier!=null) {
			if (!(eClassifier instanceof EDataType || eClassifier instanceof EEnum))
				isAttribute = false;
		}
		if (!property.getValues().isEmpty()) {
			if (property.getValues().get(0) instanceof Property)
				isAttribute = false;
		}
		if (property.ref!=null) {
			isAttribute = false;
		}
		boolean isMany = property.isMany;
		boolean isContainment = (property.ref!=null) ? false : true;

		if (isAttribute) {
			if (feature==null)
				feature = getModelDecorator().createEAttribute(
					property.name,
					property.type,
					eClass.getName(),
					property.getFirstStringValue());
		}
		else {
			if (feature==null)
				feature = getModelDecorator().createEReference(
					property.name,
					property.type,
					eClass.getName(),
					isContainment,
					isMany);
		}

		ModelDecorator.setLabel(feature, property.label);
		
		return feature;
	}
	
	private EStructuralFeature getFeature(EObject object, Property property) {
		return createEFeature(object.eClass(), property);
	}

	/**
	 * Populate the given EObject from the Property tree defined in this runtime
	 * plugin's "modelObject" extension point.
	 * 
	 * @param object
	 * @param property
	 */
	private EStructuralFeature populateObject(EObject object, Property property) {

		EObject childObject = null;
		EStructuralFeature childFeature = null;
		EStructuralFeature feature = getFeature(object, property);
		Object firstValue = property.getValues().isEmpty() ? null : property.getValues().get(0);

		if (feature instanceof EAttribute) {
			adaptFeature(object, feature, firstValue, property);
		}
		else if (feature instanceof EReference) {
			EReference ref = (EReference)feature;
			if (property.ref!=null) {
				// navigate down the newly created custom task to find the object reference
				childObject = modelObject;
				String[] segments = property.ref.split("/"); //$NON-NLS-1$
				for (String s : segments) {
					// is the feature an Elist?
					int index = s.indexOf('#');
					if (index>0) {
						index = Integer.parseInt(s.substring(index+1));
						s = s.split("#")[0]; //$NON-NLS-1$
					}
					// run all of the initializers that apply to the current child object
					// so that references can be resolved
					if (initializers.execute(childObject)) {
						childFeature = childObject.eClass().getEStructuralFeature(s);
						childObject = (EObject)getValue(childObject, childFeature, index);
					}
				}
				adaptFeature(object, feature, childObject, property);
			}
			else if (firstValue instanceof Property)
			{
                EClassifier reftype = null;
                if (property.type == null || property.type.length() == 0) {
                	reftype = getModelDecorator().findEClassifier(property.name);
                }
                else {
                	reftype = getModelDecorator().findEClassifier(property.type);
                }
                if (reftype == null || !(reftype instanceof EClass)) {
                    reftype = ref.getEReferenceType();
                }
                childObject = createObject((EClass) reftype);
                if (property.label!=null) {
                	ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(childObject);
                	if (adapter!=null)
                		adapter.getObjectDescriptor().setLabel(property.label);
                }
                adaptFeature(object, feature, childObject, property);
                populateObjectFromValues(childObject, property.getValues());
			}
		}
		return feature;
	}
	
	/**
	 * Populate the given EObject with a list of values which must be Property objects.
	 * 
	 * @param object - the object to initialize
	 * @param values - list of Property values
	 */
	private void populateObjectFromValues(EObject object, List<Object> values) {
		
		for (Object value : values) {
			if (value instanceof Property)
				populateObject(object,(Property)value);
		}
	}

	/**
	 * Return the value of the given root Property name.
	 * 
	 * @param name
	 * @return
	 */
	public Object getPropertyValue(String name) {

		for (Property prop : getProperties()) {
			if (prop.name.equals(name)) {
				if (!prop.getValues().isEmpty()) {
					return prop.getValues().get(0);
				}
			}
		}
		return null;
	}
	
	public List<Property> getProperties(String path) {
		List<Property> result = new ArrayList<Property>();
		List<Property> props = new ArrayList<Property>();
		props.addAll(getProperties());
		String names[] = path.split("/"); //$NON-NLS-1$
		getProperties(props,names,0,result);
		return result;
	}
	
	private void getProperties(List<Property>props, String names[], int index, List<Property>result) {
		String name = names[index];
		for (Property p : props) {
			if (p.name.equals(name)) {
				if (index==names.length-1)
					result.add(p);
				else {
					List<Property>childProps = new ArrayList<Property>();
					for (Object v : p.values) {
						if (v instanceof Property) {
							childProps.add((Property)v);
						}
					}
					getProperties(childProps, names, index+1, result);
				}
			}
		}
	}
	
	public Property getProperty(String name) {
		for (Property prop : getProperties()) {
			if (prop.name.equals(name)) {
				return prop;
			}
		}
		return null;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public ExtendedPropertiesAdapter adaptObject(EObject object) {
		addModelExtensionAdapter(object);
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null) {
			adapter.setProperty(this.getClass().getName(), this);
			if (description!=null)
				adapter.setProperty(ExtendedPropertiesAdapter.LONG_DESCRIPTION, description);
		}
		return adapter;
	}
	
	private void addModelExtensionAdapter(EObject object) {
		if (!object.eAdapters().contains(this))
			object.eAdapters().add( new ModelExtensionAdapter(this) );
	}

	public static ModelExtensionAdapter getModelExtensionAdapter(EObject object) {
		for (Adapter a : object.eAdapters()) {
			if (a instanceof ModelExtensionAdapter) {
				return (ModelExtensionAdapter)a;
			}
		}
		return null;
	}

	private void adaptFeature(EObject object, EStructuralFeature feature, Object value, Property property) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null) {
			// if this is a dynamic feature, delegate access to the feature to the Model Decorator
			FeatureDescriptor fd = adapter.getFeatureDescriptor(feature);
			if (object.eClass().getEStructuralFeature(feature.getName())==null)
				fd.setProperty(ExtendedPropertiesAdapter.IS_EXTENSION_FEATURE, Boolean.TRUE);
			if (property.description!=null)
				adapter.setProperty(feature, ExtendedPropertiesAdapter.LONG_DESCRIPTION, property.description);
			
			if (!(feature.getEType() instanceof EEnum)) // skip enum initialization
				initializers.add(adapter,feature,value);
		}
	}

	private int recursionCounter;
	public boolean isDefined(String className, String featureName) {
		if (className==null || featureName==null) {
			return false;
		}
		if (++recursionCounter>100) {
			Activator.logError(new Exception("Possible infinite recursion in "+this.getClass().getName()+"#isDefined()")); //$NON-NLS-1$ //$NON-NLS-2$
			--recursionCounter;
			return false;
		}
		
		if (className.equals(getType())) {
			if (featureName!=null) {
				for (Property p : getProperties()) {
					if (featureName.equals(p.name)) {
						--recursionCounter;
						return true;
					}
				}
			}
			--recursionCounter;
			return false;
		}
		EClass eClass = getEClass(className);
		if (eClass!=null) {
			for (EClass st : eClass.getEAllSuperTypes()) {
				// TODO: this should fix any infinite recursions but keeping the counter just in case.
				if (st.getName().equals(className))
					continue;
				if (isDefined(st.getName(), featureName)) {
					--recursionCounter;
					return true;
				}
			}
		}
		
		// check types defined within the Properties tree
		for (Property property : getProperties()) {
			if (className.equals(property.type)) {
				if (featureName!=null && property.values!=null) {
					for (Object p : property.values) {
						if (p instanceof Property) {
							if (featureName.equals(((Property)p).name)) {
								--recursionCounter;
								return true;
							}
						}
					}
				}
				--recursionCounter;
				return false;
			}

			Iterator<Property> iter = property.iterator();
			while (iter.hasNext()) {
				Property p = iter.next();
				if (className.equals(p.type)) {
					if (featureName!=null && p.values!=null) {
						for (Object child : p.values) {
							if (child instanceof Property) {
								if (((Property)child).name.equals(featureName)) {
									--recursionCounter;
									return true;
								}
							}
						}
					}
				}
			}
		}

		--recursionCounter;
		return false;
	}

	public EClass getEClass(String className) {
		// try the runtime package first
		EClass eClass = getModelDecorator().getEClass(className);
		
		// then all BPMN2 packages
		if (eClass==null)
			eClass = (EClass)Bpmn2Package.eINSTANCE.getEClassifier(className);
		if (eClass==null)
			eClass = (EClass)BpmnDiPackage.eINSTANCE.getEClassifier(className);
		return eClass;
	}

	public EStructuralFeature getEStructuralFeature(String className, String featureName) {
		EClass eClass = getEClass(className);
		if (eClass!=null)
			return eClass.getEStructuralFeature(featureName);
		return null;
	}
}
