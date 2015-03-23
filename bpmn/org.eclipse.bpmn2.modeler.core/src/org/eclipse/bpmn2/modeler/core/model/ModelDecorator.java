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

package org.eclipse.bpmn2.modeler.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.modeler.core.EDataTypeConversionFactory;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterRegistry;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterUtil;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.utils.Messages;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EDataType.Internal.ConversionDelegate;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.type.AnyType;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.osgi.util.NLS;

/**
 * This class wraps an EPackage and provides methods for dynamic EMF.
 */
/**
 *
 */
public class ModelDecorator {
	final static EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
	public final static String DECORATOR_URI = "http://org.eclipse.bpmn2.modeler.core.decorator"; //$NON-NLS-1$

	protected EPackage ePackage;
	protected static ResourceSet resourceSet;
	protected List<EPackage> relatedEPackages;
	
	/**
	 * Construct a new EPackage for extension classes and features, and add the given
	 * EPackage to our list of related packages. The new EPackage will have the same
	 * namespace URI and prefix, but will be contained in a private ResourceSet,
	 * so there's no danger of contaminating the original EPackage.
	 * 
	 * This allows extension plugins to define their own EMF models the traditional
	 * way (by generating Java implementations classes from an ecore file) but still
	 * supports dynamic extensions to those models.
	 * 
	 * @param pkg
	 */
	public ModelDecorator(EPackage pkg) {
		Assert.isTrue( isValid(pkg) );
		String name = pkg.getName()+" Dynamic Extensions"; //$NON-NLS-1$
		String nsPrefix = pkg.getNsPrefix();
		String nsURI = pkg.getNsURI();

		addRelatedEPackage(pkg);
//		AdapterRegistry.INSTANCE.registerFactory(pkg, AnyTypeAdaptorFactory.INSTANCE);
		
		getResourceSet();
		ePackage = (EPackage) resourceSet.getPackageRegistry().get(nsURI);
		if (ePackage==null) {
			ePackage = createEPackage(name,nsPrefix,nsURI);
			initPackage();
		}
	}
	
	/**
	 * Construct a new EPackage for extension classes and features that will be
	 * defined dynamically.
	 * 
	 * @param name
	 * @param nsPrefix
	 * @param nsURI
	 */
	public ModelDecorator(String name, String nsPrefix, String nsURI) {
		ePackage = (EPackage) getResourceSet().getPackageRegistry().get(nsURI);
		if (ePackage==null) {
			ePackage = createEPackage(name,nsPrefix,nsURI);
		}
		initPackage();
	}
	
	/**
	 * Dispose of our dynamic EPackage and all of its contained classes and features.
	 */
	public void dispose() {
		if (resourceSet!=null) {
			if (ePackage!=null) {
				ModelDecoratorAdapter mda = ModelDecoratorAdapter.getAdapter(ePackage);
				if (mda!=null)
					mda.dispose();
				resourceSet.getPackageRegistry().remove(ePackage.getNsURI());
				EcoreUtil.delete(ePackage);
			}
		}
	}
	
	/**
	 * Construct a private ResourceSet that will contain our dynamic EPackage.
	 * 
	 * @return
	 */
	private static ResourceSet getResourceSet() {
		if (resourceSet==null)
			resourceSet = new ResourceSetImpl();
		return resourceSet;
	}
	
	/**
	 * Initialize our dynamic EPackage:
	 * - set our object factory to create adapted AnyType objects
	 * - add a ModelDecorator adapter to the EPackage so that clients can find us
	 * - add our DataTypeConversion factory for user-defined EDataTypes
	 */
	private void initPackage() {
		ePackage.setEFactoryInstance(new AnyTypeObjectFactory(this));
		ModelDecoratorAdapter.adapt(this);
		List<String> delegates = new ArrayList<String>();
		delegates.add(EDataTypeConversionFactory.DATATYPE_CONVERSION_FACTORY_URI);
		EcoreUtil.setConversionDelegates(ePackage, delegates);
		AdapterRegistry.INSTANCE.registerFactory(ePackage, AnyTypeAdaptorFactory.INSTANCE);
	}
	
	/**
	 * Return our dynamic EPackage.
	 * 
	 * @return
	 */
	public EPackage getEPackage() {
		Assert.isNotNull(ePackage);
		return ePackage;
	}
	
	/**
	 * Return the dynamic EPackage for the given namespace URI.
	 * 
	 * @param nsURI
	 * @return the dynamic EPackage or null if not found.
	 */
	public static EPackage getEPackage(String nsURI) {
		EPackage pkg = (EPackage) getResourceSet().getPackageRegistry().get(nsURI);
		if (pkg!=null)
			return pkg;
		
		// check all related packages in all ModelDecorators in our ResourceSet
		for (Map.Entry<String, Object> entry : getResourceSet().getPackageRegistry().entrySet()) {
			ModelDecorator md = ModelDecoratorAdapter.getModelDecorator((EPackage) entry.getValue());
			for (EPackage p : md.getRelatedEPackages()) {
				if (p.getNsURI().equals(nsURI))
					return p;
			}
		}
		
		return null;
	}
	
	public static ModelDecorator getModelDecorator(String nsURI) {
		EPackage pkg = getEPackage(nsURI);
		if (pkg!=null) {
			ModelDecoratorAdapter mda = AdapterUtil.adapt(pkg, ModelDecoratorAdapter.class);
			if (mda!=null)
				return mda.getModelDecorator();
		}
		return null;
	}
	
	/**
	 * Look up the ModelDecorator from the given feature by using that feature's namespace.
	 * 
	 * @param feature
	 * @return the ModelDecorator that contains the given feature or null if the feature
	 * is not defined.
	 */
	public static ModelDecorator getModelDecorator(EStructuralFeature feature) {
		String nsURI = ExtendedMetaData.INSTANCE.getNamespace(feature);
		return getModelDecorator(nsURI);
	}
	
	/**
	 * Add the given EPackage to the list of related packages.
	 * See the ModelDecorator(EPackage) constructor
	 * 
	 * @param pkg
	 */
	public void addRelatedEPackage(EPackage pkg) {
		if (pkg!=ePackage && !getRelatedEPackages().contains(pkg))
			getRelatedEPackages().add(pkg);
	}
	
	/**
	 * Return the list of related EPackages.
	 * See the ModelDecorator(EPackage) constructor
	 * 
	 * @return a list of EPackage objects. The list may be empty.
	 */
	public List<EPackage> getRelatedEPackages() {
		if (relatedEPackages==null) {
			relatedEPackages = new ArrayList<EPackage>();
		}
		return relatedEPackages;
	}
	
	/**
	 * Create our dynamic EPackage and add it to our private ResourceSet.
	 * 
	 * @param name
	 * @param nsPrefix
	 * @param nsURI
	 * @return the newly created dynamic EPackage
	 */
	private EPackage createEPackage(String name, String nsPrefix, String nsURI) {
		ePackage = theCoreFactory.createEPackage();
		ePackage.setName(name);
		ePackage.setNsPrefix(nsPrefix);
		ePackage.setNsURI(nsURI);
	
		getResourceSet();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMLResourceFactoryImpl()); //$NON-NLS-1$
		resourceSet.getPackageRegistry().put(nsURI, ePackage);

		return ePackage;
	}
	
	/**
	 * Parse a type string to return the list of supertypes. The type string is in
	 * the form
	 * 
	 *    "classname:supertype1,supertype2,..."
	 *  
	 * this method returns the list of strings containing "supertype1", "supertype2", etc.
	 * 
	 * @param type
	 * @return a list of strings or an empty list of no supertypes found.
	 */
	private List<String> getSuperTypes(String type) {
		List<String> supertypes = new ArrayList<String>();
		if (type!=null && type.contains(":")) { //$NON-NLS-1$
			String a[] = type.split(":"); //$NON-NLS-1$
			if (a.length>1) {
				a = a[1].split(","); //$NON-NLS-1$
			}
			else {
				a = a[0].split(","); //$NON-NLS-1$
			}
			for (int i=0; i<a.length; ++i) {
				supertypes.add(a[i]);
			}
		}
		return supertypes;
	}
	
	/**
	 * Parse a type string to return the subclass name. The type string is in
	 * the form
	 * 
	 *    "classname:supertype1,supertype2,..."
	 *  
	 * this method returns the "classname" portion.
	 * 
	 * @param type
	 * @return a string containing only the type name
	 */
	private String getType(String type) {
		if (type!=null && type.contains(":")) { //$NON-NLS-1$
			return type.split(":")[0]; //$NON-NLS-1$
		}
		return type;
	}
	
	/**
	 * Search for the EClassifier whose name is the type string.
	 * 
	 * @param type - a type name string that may contain additional supertype names.
	 * @see getType(String)
	 * @return the EClassifier or null if not found.
	 */
	public EClassifier getEClassifier(String type) {
		type = getType(type);
		EClassifier eClassifier = ePackage.getEClassifier(type);
		if (eClassifier != null) {
			return eClassifier;
		}
		for (EPackage p : getRelatedEPackages()) {
			eClassifier = p.getEClassifier(getType(type));
			if (eClassifier != null) {
				return eClassifier;
			}
		}
		Assert.isTrue(eClassifier==null);
		return null;
	}
	
	/**
	 * Create a dynamic EClassifier from a type string. This will create a new
	 * EEnum if the supertype is an EEnum, or a new EDataType if the supertype
	 * is an EDataType. If no supertype is given, an EClass is created instead.
	 * 
	 * @param type - a type name string that may contain additional supertype names.
	 * @see getType(String)
	 * @return the EClassifier.
	 */
	public EClassifier createEClassifier(String type) {
		EClassifier eClassifier = getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		EClassifier eDataType = null;
		for (String st : getSuperTypes(type)) {
			EClassifier ec = findEClassifier(st);
			if (EDataType.class.isAssignableFrom( ec.getInstanceClass() )) {
				eDataType = ec;
				break;
			}
		}
		
		if (eDataType==null) {
			if (EDataTypeConversionFactory.isFactoryFor(getType(type)))
				return createEDataType(type);
			return createEClass(type);
		}
		
		if (EEnum.class.isAssignableFrom(eDataType.getInstanceClass()))
			eClassifier = theCoreFactory.createEEnum();
		else
			eClassifier = theCoreFactory.createEDataType();

		eClassifier.setName(getType(type));
		ePackage.getEClassifiers().add(eClassifier);

		return eClassifier;
	}
	
	/**
	 * Create a dynamic EEnum literal value for an EEnum type name.
	 * 
	 * @param name - name of the enum literal
	 * @param owningtype - the EEnum type name that owns the newly created literal.
	 * @return a new EEnum literal.
	 */
	public EEnumLiteral createEEnumLiteral(String name, String owningtype) {

		EClassifier eClassifier = getEClassifier(owningtype);
		if (eClassifier==null) {
			eClassifier = createEClassifier(owningtype + ":EEnum"); //$NON-NLS-1$
		}
		if (!(eClassifier instanceof EEnum))
			return null;
		
		return createEEnumLiteral(name, (EEnum)eClassifier);
	}
	
	/**
	 * Create a dynamic EEnum literal value for an EEnum type.
	 * 
	 * @param name - name of the enum literal
	 * @param eEnum - the EEnum type that owns the newly created literal.
	 * @return a new EEnum literal
	 */
	public EEnumLiteral createEEnumLiteral(String name, EEnum eEnum) {
		
		EEnumLiteral literal = theCoreFactory.createEEnumLiteral();
		literal.setLiteral(name);
		literal.setName(name.toUpperCase());
		literal.setValue(eEnum.getELiterals().size());

		eEnum.getELiterals().add(literal);

		return literal;
	}
	
	/**
	 * Search for the EDataType whose name is the type string.
	 * 
	 * @param type - name of an EDataType
	 * @return the EDatatype or null if not found
	 */
	public EDataType getEDataType(String type) {
		EClassifier eClassifier = getEClassifier(type);
		if (eClassifier instanceof EDataType) {
			return (EDataType) eClassifier;
		}
		Assert.isTrue(eClassifier==null);
		return null;
	}
	
	/**
	 * Create a dynamic EDataType from a type string.
	 * 
	 * @param type - name of the EDataType to create.
	 * @return a new EDataType.
	 */
	public EDataType createEDataType(String type) {
		type = getType(type);
		EDataType eDataType = getEDataType(type);
		if (eDataType!=null)
			return eDataType;
		
		eDataType = theCoreFactory.createEDataType();
		eDataType.setName(type);
		
		ePackage.getEClassifiers().add(eDataType);
		// make this look like a DocumentRoot so that it can be added
		// to the containing object's "anyType" feature.
		ExtendedMetaData.INSTANCE.setName(eDataType, ""); //$NON-NLS-1$
		EAnnotation ea = theCoreFactory.createEAnnotation();
		ea.setEModelElement(eDataType);
		ea.setSource(EDataTypeConversionFactory.DATATYPE_CONVERSION_FACTORY_URI);
		ConversionDelegate cd = EDataTypeConversionFactory.INSTANCE.createConversionDelegate(eDataType);
		if (cd!=null) {
			Object value = cd.createFromString(""); //$NON-NLS-1$
			eDataType.setInstanceClass(value.getClass());
		}
		eDataType.getEAnnotations().add(ea);

		return eDataType;
	}

	/**
	 * Search for the EClass whose name is the type string.
	 * 
	 * @param type - name of an EClass
	 * @return the EClass or null if not found
	 */
	public EClass getEClass(String type) {
		EClassifier eClassifier = getEClassifier(type);
		if (eClassifier instanceof EClass) {
			return (EClass) eClassifier;
		}
		Assert.isTrue(eClassifier==null);
		return null;
	}
	
	/**
	 * Create a dynamic EClass from a type string.
	 * 
	 * @param type - a type name string that may contain additional supertype names.
	 * @see getType(String)
	 * @return the EClass.
	 */
	public EClass createEClass(String type) {
		EClass eClass = getEClass(type);
		if (eClass!=null)
			return eClass;
		
		eClass = theCoreFactory.createEClass();
		eClass.setName(getType(type));
		eClass.getESuperTypes().add(XMLTypePackage.eINSTANCE.getAnyType());
		
		ePackage.getEClassifiers().add(eClass);
		for (String st : getSuperTypes(type)) {
			EClassifier eClassifier = findEClassifier(st);
			if (eClassifier instanceof EClass)
				eClass.getESuperTypes().add((EClass) eClassifier);
		}
		
		// make this class look like a DocumentRoot so that it can be added
		// to the containing object's "anyType" feature.
		ExtendedMetaData.INSTANCE.setName(eClass, ""); //$NON-NLS-1$
		eClass.setInstanceClass(AnyType.class);

		return eClass;
	}

	public EStructuralFeature getEStructuralFeature(EObject object, String name) {
		// first check the object's EClass for the feature name
		EClass eClass = object.eClass();
		if (eClass!=null) {
			EStructuralFeature feature = eClass.getEStructuralFeature(name);
			if (feature!=null)
				return feature;
		}
		// if not found, search our dynamic EPackages for a class with the same name
		// and look for the feature name in there
		if (object instanceof ExtensionAttributeValue) {
			object = object.eContainer();
		}
		String type = object.eClass().getName();
		eClass = getEClass(type);
		if (eClass!=null) {
			for (EStructuralFeature feature : eClass.getEAllStructuralFeatures()) {
				if (name.equals(feature.getName()))
					return feature;
				if (name.equals(ExtendedMetaData.INSTANCE.getName(feature)))
					return feature;
			}
		}
		return findEStructuralFeatureInDocumentRoot(name);
	}
	
	/**
	 * Search for an EAttribute with the given name in the specified EClass.
	 * 
	 * @param name - name of the attribute to search for.
	 * @param type - the data type of the attribute.
	 * @param owningtype - name of the EClass that contains the attribute.
	 * @return the EAttribute or null if not found.
	 */
	public EAttribute getEAttribute(String name, String type, String owningtype) {
		EStructuralFeature feature = findEStructuralFeatureInDocumentRoot(name);
		if (feature instanceof EAttribute) {
//			if (type!=null)
//				Assert.isTrue(type.equals(((EAttribute) feature).getEType().getName()) );
			return (EAttribute) feature;
		}
		
		EClass eClass = getEClass(owningtype);
		if (eClass!=null) {
			// the EClass already exists in our EPackage: check if the named feature was already created
			feature = eClass.getEStructuralFeature(name);
			if (feature instanceof EAttribute) {
				if (type!=null) {
					Assert.isTrue(type.equals(((EAttribute) feature).getEType().getName()) );
				}
				return (EAttribute) feature;
			}
			Assert.isTrue(feature==null);
			return null;
		}
		else {
			// if not, check other related packages including the Bpmn2Package
			EClassifier ec = findEClassifier(owningtype);
			if ( !isValid(ec) && ec instanceof EClass ) {
				// the EClass does not belong to us, but if the feature exists in that EClass, use it.
				feature = ((EClass)ec).getEStructuralFeature(name);
				if (feature instanceof EAttribute) {
					return (EAttribute) feature;
				}
			}
		}
		Assert.isTrue(eClass==null);
		return null;
	}
	
	/**
	 * Create a dynamic EAttribute of a given type, and add it the specified EClass.
	 * If the specified EClass does not exist, it will be created.
	 * 
	 * @param name - name of the dynamic attribute.
	 * @param type - type of the attribute.
	 * @param owningtype - the name of the EClass that owns this attribute. 
	 * @param defaultValue - initial default value for the attribute.
	 * @return a new EAttribute
	 */
	public EAttribute createEAttribute(String name, String type, String owningtype, String defaultValue) {
		EAttribute eAttribute = getEAttribute(name,type,owningtype);
		if (eAttribute!=null)
			return eAttribute;

		if (type==null)
			type = "EString"; //$NON-NLS-1$

		// if the class type does not exist, create it in this package
		EClassifier eClassifier = findEClassifier(type);
		if (eClassifier==null) {
			eClassifier = createEClassifier(type);
		}

		// check if owning class is in this package
		EClass eClass = getEClass(owningtype);
		if (eClass==null) {
			// if not, check other related packages including the Bpmn2Package
			EClassifier ec = findEClassifier(owningtype);
			if ( !isValid(ec) ) {
				ec = createEClass(owningtype);
			}
			if (ec instanceof EClass)
				eClass = (EClass) ec;
		}
		Assert.isNotNull(eClass);

		eAttribute = theCoreFactory.createEAttribute();
		eAttribute.setName(name);
		eAttribute.setChangeable(true);
		eAttribute.setUnsettable(true);
		eAttribute.setEType(eClassifier);
		
		eClass.getEStructuralFeatures().add(eAttribute);
		
		ExtendedMetaData.INSTANCE.setNamespace(eAttribute, ePackage.getNsURI());
		ExtendedMetaData.INSTANCE.setFeatureKind(eAttribute, ExtendedMetaData.ATTRIBUTE_FEATURE);
		ExtendedMetaData.INSTANCE.setName(eAttribute, name);
		
		if (eClassifier instanceof EEnum) {
			if (defaultValue!=null) {
				boolean setDefault = true;
				String values[];
				if (defaultValue.contains(","))
					values = defaultValue.split(",");
				else
					values = defaultValue.split(" ");
				for (String v : values) {
					if (setDefault) {
						eAttribute.setDefaultValue(v);
						setDefault = false;
					}
					createEEnumLiteral(v, (EEnum)eClassifier);
				}
			}
		}
		else if (eClassifier instanceof EDataType) {
			if (defaultValue!=null) {
				eAttribute.setDefaultValue(defaultValue);
			}
		}

		return eAttribute;
	}
	
	/**
	 * Search for an EReference with the given name in the specified EClass.
	 * 
	 * @param name - name of the reference to search for.
	 * @param type - the data type of the reference.
	 * @param owningtype - name of the EClass that contains the reference.
	 * @param containment - if true, the EReference is a containment feature; if false, it is a reference.
	 * @param many - if true, the EReference is a list; if false, it is a single value.
	 * @return the EReference or null if not found.
	 */
	public EReference getEReference(String name, String type, String owningtype, boolean containment, boolean many) {
		EStructuralFeature feature = findEStructuralFeatureInDocumentRoot(name);
		if (feature instanceof EReference) {
			if (type!=null)
				Assert.isTrue(type.equals(((EReference) feature).getEType().getName()) );
			Assert.isTrue(containment == ((EReference) feature).isContainment());
			Assert.isTrue(many == ((EReference) feature).isMany());
			return (EReference) feature;
		}
		EClass eClass = getEClass(owningtype);
		if (eClass != null) {
			// the EClass already exists in our EPackage: check if the named feature was already created
			feature = eClass.getEStructuralFeature(name);
			if (feature instanceof EReference) {
				EClassifier eClassifier = findEClassifier(type);
				Assert.isTrue(eClassifier == feature.getEType());
				Assert.isTrue(containment == ((EReference) feature)
						.isContainment());
				Assert.isTrue(many ? ((EReference) feature).getUpperBound() == EStructuralFeature.UNBOUNDED_MULTIPLICITY
						: true);
				return (EReference) feature;
			}
			Assert.isTrue(feature == null);
			return null;
		}
		else {
			// if not, check other related packages including the Bpmn2Package
			EClassifier ec = findEClassifier(owningtype);
			if ( !isValid(ec) && ec instanceof EClass ) {
				// the EClass does not belong to us, but if the feature exists in that EClass, use it.
				feature = ((EClass)ec).getEStructuralFeature(name);
				if (feature instanceof EReference) {
					return (EReference) feature;
				}
			}
		}
		Assert.isTrue(eClass==null);
		return null;
	}
	
	/**
	 * Create a new EReference with the given name in the specified EClass.
	 * If the specified EClass does not exist, it will be created.
	 * 
	 * @param name - name of the reference to create.
	 * @param type - the data type of the reference.
	 * @param owningtype - name of the EClass that contains the reference.
	 * @param containment - if true, the EReference is a containment feature; if false, it is a reference.
	 * @param many - if true, the EReference is a list; if false, it is a single value.
	 * @return a new EReference.
	 */
	public EReference createEReference(String name, String type, String owningtype, boolean containment, boolean many) {
		EReference eReference = getEReference(name,type,owningtype,containment,many);
		if (eReference!=null)
			return eReference;

		// if the class type does not exist, create it in this package
		EClassifier eClassifier = findEClassifier(type);
		if (eClassifier==null) {
			eClassifier = createEClass(type);
		}

		eReference = theCoreFactory.createEReference();
		eReference.setName(name);
		eReference.setChangeable(true);
		eReference.setUnsettable(true);
		eReference.setUnique(true);
		eReference.setContainment(containment);
		if (many)
			eReference.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
		eReference.setEType(eClassifier);
		
		// check if owning class is in this package
		EClass eClass = getEClass(owningtype);
		if (eClass==null) {
			// if not, check other related packages
			EClassifier ec = findEClassifier(owningtype);
			if ( !isValid(ec) ) {
				ec = createEClass(owningtype);
			}
			if (ec instanceof EClass)
				eClass = (EClass) ec;
		}
		Assert.isNotNull(eClass);
		eClass.getEStructuralFeatures().add(eReference);
		
		ExtendedMetaData.INSTANCE.setNamespace(eReference, ePackage.getNsURI());
		ExtendedMetaData.INSTANCE.setFeatureKind(eReference, ExtendedMetaData.ELEMENT_FEATURE);
		ExtendedMetaData.INSTANCE.setName(eReference, name);

		return eReference;
	}
	
	/**
	 * Set an EAnnotation that represents a human readable label for the given named model element.
	 *  
	 * @param element - the named element to be decorated
	 * @param label - the label string
	 */
	public static void setLabel(EModelElement element, String label) {
		// FIXME: we can only decorate dynamic EClass objects.
		// Figure out how to do this for EClasses that are defined in other models.
		if (element instanceof EReference) {
			EReference ref = (EReference) element;
			EClassifier ec = ref.getEType();
			if (isValid(ec.getEPackage()))
				element = ec;
		}
		EAnnotation ea = element.getEAnnotation(DECORATOR_URI);
		if (label!=null && !label.isEmpty()) {
			if (ea==null) {
				ea = theCoreFactory.createEAnnotation();
				ea.setEModelElement(element);
				ea.setSource(DECORATOR_URI);
			}
			ea.getDetails().put("label", label); //$NON-NLS-1$
		}
		else {
			if (ea!=null) {
				element.getEAnnotations().remove(ea);
				EcoreUtil.delete(ea);
			}
		}
	}
	
	/**
	 * Return the label string for the given named model element.
	 * 
	 * @param element - the named element.
	 * @return a text string or null if not set.
	 */
	public static String getLabel(EModelElement element) {
		EAnnotation ea = element.getEAnnotation(DECORATOR_URI);
		if (ea!=null) {
			String label = ea.getDetails().get("label"); //$NON-NLS-1$
			return label;
		}
		return null;
	}

	/**
	 * Check if the given EClassifier is owned by this ModelDecorator.
	 * The requested element can be either in our dynamic EPackage or in a related package.
	 * 
	 * @param eClassifier - the requested element.
	 * @return true if the EClassifier is managed by us, false if not.
	 */
	public boolean isValid(EClassifier eClassifier) {
		EPackage p = eClassifier==null ? null : eClassifier.getEPackage();
		return eClassifier!=null &&
				(p == ePackage || getRelatedEPackages().contains(p));
	}
	
	public static boolean isValid(EPackage pkg) {
		return pkg!=null &&
				pkg != EcorePackage.eINSTANCE &&
				pkg != Bpmn2Package.eINSTANCE &&
				pkg != BpmnDiPackage.eINSTANCE &&
				pkg != DcPackage.eINSTANCE &&
				pkg != DiPackage.eINSTANCE;
	}
	
	/**
	 * Search for an EClassifier with the given name. The search order is as follows:
	 * 1. our own dynamic EPackage
	 * 2. any related packages
	 * 3. the EcorePackage
	 * 4. the BPMN2 packages, including BPMNDI, DI and DC
	 * 
	 * @param type - name of the EClassifier to search for.
	 * @return - an EClassifier if found or null if not found.
	 */
	public EClassifier findEClassifier(String type) {
		// parse out just the class type, excluding super types
		if (type==null)
			return null;
			
		type = getType(type);
		EClassifier eClassifier = null;
		
		if (ePackage!=null) {
			eClassifier = ePackage.getEClassifier(type);
			if (eClassifier!=null)
				return eClassifier;
			eClassifier = findEClassifierInDocumentRoot(ePackage,type);
			if (eClassifier!=null)
				return eClassifier;
		}
		for (EPackage pkg : getRelatedEPackages()) {
			eClassifier = pkg.getEClassifier(type);
			if (eClassifier!=null)
				return eClassifier;
			eClassifier = findEClassifierInDocumentRoot(pkg,type);
			if (eClassifier!=null)
				return eClassifier;
		}
		
		return findEClassifier(null,type);
	}
	
	public EStructuralFeature findEStructuralFeatureInDocumentRoot(String name) {
		if (name==null)
			return null;
			
		EStructuralFeature feature = null;
		
		if (ePackage!=null) {
			feature = findEStructuralFeatureInDocumentRoot(ePackage,name);
			if (feature!=null)
				return feature;
		}
		for (EPackage pkg : getRelatedEPackages()) {
			feature = findEStructuralFeatureInDocumentRoot(pkg,name);
			if (feature!=null)
				return feature;
		}
		
		feature = findEStructuralFeatureInDocumentRoot(Bpmn2Package.eINSTANCE, name);
		if (feature!=null)
			return feature;
		
		return null;
	}

	/**
	 * Search for an EClassifier with the given name. The search order is as follows:
	 * 1. the specified EPackage, if not null
	 * 2. the EcorePackage
	 * 3. the BPMN2 packages, including BPMNDI, DI and DC
	 * 
	 * @param pkg - an optional EPackage to search.
	 * @param type - name of the EClassifier to search for.
	 * @return - an EClassifier if found or null if not found.
	 */
	public static EClassifier findEClassifier(EPackage pkg, String type) {
		if (type==null) {
			return null;
		}
		
		EClassifier eClassifier = null;
		if (pkg!=null) {
			eClassifier = pkg.getEClassifier(type);
			if (eClassifier!=null)
				return eClassifier;
			eClassifier = findEClassifierInDocumentRoot(pkg,type);
			if (eClassifier!=null)
				return eClassifier;
		}
		
		eClassifier = EcorePackage.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		eClassifier = Bpmn2Package.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		eClassifier = findEClassifierInDocumentRoot(Bpmn2Package.eINSTANCE,type);
		if (eClassifier!=null)
			return eClassifier;
		
		eClassifier = BpmnDiPackage.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		eClassifier = findEClassifierInDocumentRoot(BpmnDiPackage.eINSTANCE,type);
		if (eClassifier!=null)
			return eClassifier;
		
		eClassifier = DiPackage.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		eClassifier = DcPackage.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		return null;
	}

	/**
	 * Search for an EClassifier with the given name as an element in the DocumentRoot of
	 * the given EPackage.
	 * 
	 * @param pkg - the EPackage to search.
	 * @param name - name of the EClassifier to search for.
	 * @return - an EClassifier if found or null if not found.
	 */
	private static EClassifier findEClassifierInDocumentRoot(EPackage pkg, String name) {
		EStructuralFeature feature = findEStructuralFeatureInDocumentRoot(pkg, name);
		if (feature!=null)
			return feature.getEType();
		return null;
	}
	
	private static EStructuralFeature findEStructuralFeatureInDocumentRoot(EPackage pkg, String name) {
		try {
			EClass docRoot = (EClass)pkg.getEClassifier("DocumentRoot"); //$NON-NLS-1$
			if (docRoot==null) {
				docRoot = ExtendedMetaData.INSTANCE.getDocumentRoot(pkg);
			}
			if (docRoot!=null) {
				for (EStructuralFeature feature : docRoot.getEAllStructuralFeatures()) {
					if (feature.getEContainingClass().getEPackage()==pkg) {
						if (name.equals(feature.getName()))
							return feature;
						if (name.equals(ExtendedMetaData.INSTANCE.getName(feature)))
							return feature;
					}
				}
			}
		}
		catch (Exception e) {
		}
		return null;
	}
	
	private static EStructuralFeature getAnyAttributeFeature(EObject object) {
		EStructuralFeature anyAttribute = null;
		if (object!=null) {
			EClass eclass = null;
			if (object instanceof EClass)
				eclass = (EClass)object;
			else
				eclass = object.eClass();
			anyAttribute = eclass.getEStructuralFeature("anyAttribute"); //$NON-NLS-1$
		}
		return anyAttribute;
	}
	
	/**
	 * Return the feature with the given name in the specified object's "anyAttribute" feature map.
	 * 
	 * @param object - the EObject to search.
	 * @param name - name of the feature to search for.
	 * @return an EStructuralFeature if found or null if not found.
	 */
	public static EStructuralFeature getAnyAttribute(EObject object, String name) {
		EStructuralFeature anyAttribute = getAnyAttributeFeature(object);
		if (anyAttribute!=null && object.eGet(anyAttribute) instanceof BasicFeatureMap) {
			BasicFeatureMap map = (BasicFeatureMap)object.eGet(anyAttribute);
			for (Entry entry : map) {
				EStructuralFeature feature = entry.getEStructuralFeature();
				if (feature.getName().equals(name))
					return feature;
			}
		}
		return null;
	}

	/**
	 * Return all of the features in the specified object's "anyAttribute" feature map.
	 * 
	 * @param object - the EObject to search.
	 * @return a list of EStructuralFeatures if found or an empty list if not found.
	 */
	public static List<EStructuralFeature> getAnyAttributes(EObject object) {
		List<EStructuralFeature> list = new ArrayList<EStructuralFeature>();
		EStructuralFeature anyAttribute = getAnyAttributeFeature(object);
		if (anyAttribute!=null && object.eGet(anyAttribute) instanceof BasicFeatureMap) {
			BasicFeatureMap map = (BasicFeatureMap)object.eGet(anyAttribute);
			for (Entry entry : map) {
				EStructuralFeature feature = entry.getEStructuralFeature();
				list.add(feature);
			}
		}
		return list;
	}
	
	/**
	 * Create a new attribute in the specified object's "anyAttribute" feature map.
	 * The attribute will be assigned the given namespace, name, type and initial value.
	 * 
	 * @param object - the EObject to be decorated.
	 * @param namespace - namespace of the new attribute.
	 * @param name - name of the new extension attribute.
	 * @param type - data type of the attribute.
	 * @param value - initial value of the attribute.
	 * @return a new EAttribute
	 */
	@SuppressWarnings("unchecked")
	public EStructuralFeature addAnyAttribute(EObject object, String namespace, String name, String type, Object value) {
		EStructuralFeature attr = null;
		EClass eclass;
		if (object instanceof EClass) {
			eclass = (EClass)object;
			object = ExtendedPropertiesAdapter.getDummyObject(eclass);
		}
		else
			eclass = object.eClass();
		EStructuralFeature anyAttribute = getAnyAttributeFeature(object);
		List<BasicFeatureMap.Entry> anyMap = (List<BasicFeatureMap.Entry>)object.eGet(anyAttribute);
		if (anyMap==null)
			return null;
		for (BasicFeatureMap.Entry fe : anyMap) {
			if (fe.getEStructuralFeature() instanceof EAttributeImpl) {
				EAttributeImpl a = (EAttributeImpl) fe.getEStructuralFeature();
				if (namespace.equals(a.getExtendedMetaData().getNamespace()) && name.equals(a.getName())) {
					attr = a;
					break;
				}
			}
		}
		
		// this featuremap can only hold attributes, not elements
		if (type==null)
			type = "E" + value.getClass().getSimpleName(); //$NON-NLS-1$
		EPackage pkg = ModelDecorator.getEPackage(namespace);
		EDataType eDataType = (EDataType)ModelDecorator.findEClassifier(pkg, type);//(EDataType)EcorePackage.eINSTANCE.getEClassifier(type);
		if (eDataType!=null) {
			// value can not be null - use the default value instead
			if (value==null) {
				try {
					value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,"");
				}
				catch (Exception e) {
					// data type converter can't handle empty strings,
					// try creating an empty object using default constructor
					try {
						value = eDataType.getInstanceClass().newInstance();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			if (attr==null) {
				attr = createEAttribute(name, type, eclass.getName(), null);
				anyMap.add( FeatureMapUtil.createEntry(attr, value) );
			}
			else {
				EClassifier dt = attr.getEType();
				if (dt==null || !eDataType.getInstanceClass().isAssignableFrom(dt.getInstanceClass()))
					throw new IllegalArgumentException(
						NLS.bind(
							Messages.ModelUtil_Illegal_Value,
							new Object[] {
								object.eClass().getName(),
								attr.getName(),
								attr.getEType().getName(),
								value.toString()
							}
						)
					);
				anyMap.add( FeatureMapUtil.createEntry(attr, value) );
			}
		}
		else {
			if (attr==null) {
				attr = createEAttribute(name, type, eclass.getName(), null);
			}
			if (value==null) {
				if (attr.getEType() instanceof EDataType) {
					eDataType = (EDataType) attr.getEType();
					value = eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType,"");
				}
			}
			anyMap.add( FeatureMapUtil.createEntry(attr, value) );
		}
		return attr;
	}

	/**
	 * Create a new attribute in the specified object's "anyAttribute" feature map.
	 * The attribute will be assigned the namespace from our dynamic EPackage.
	 * 
	 * @param object - the EObject to be decorated. This SHOULD be a BaseElement.
	 * @param name - name of the new extension attribute.
	 * @param type - data type of the attribute.
	 * @param value - initial value of the attribute.
	 * @return a new EAttribute
	 */
	public EStructuralFeature addAnyAttribute(EObject object, String name, String type, Object value) {
		EPackage pkg = object.eClass().getEPackage();
		String nsURI = pkg.getNsURI();
		return addAnyAttribute(object, nsURI, name, type, value);
	}

	/**
	 * Create a new extension element in the specified BaseElement's extension values container.
	 * 
	 * @param object - the EObject to be decorated. This SHOULD be a BaseElement.
	 * @param feature - name of the new extension element.
	 * @param value - value assigned to the new element.
	 */
	public static void addExtensionAttributeValue(EObject object, EStructuralFeature feature, Object value) {
		addExtensionAttributeValue(object, feature, value, -1, false);
	}

	/**
	 * Create a new extension element in the specified BaseElement's extension values container.
	 * 
	 * @param object - the EObject to be decorated. This SHOULD be a BaseElement.
	 * @param feature - name of the new extension element.
	 * @param value - value assigned to the new element.
	 * @param delay - if true, use an InsertionAdapter to set the feature value, otherwise set it immediately.
	 */
	public static void addExtensionAttributeValue(EObject object, EStructuralFeature feature, Object value, boolean delay) {
		addExtensionAttributeValue(object, feature, value, -1, delay);
	}

	/**
	 * Create a new extension element in the specified BaseElement's extension values container.
	 * 
	 * @param object - the EObject to be decorated. This SHOULD be a BaseElement.
	 * @param feature - name of the new extension element.
	 * @param value - value assigned to the new element.
	 * @param index - if the element is a list, the list index for the value.
	 * @param delay - if true, use an InsertionAdapter to set the feature value, otherwise set it immediately.
	 */
	@SuppressWarnings("unchecked")
	public static void addExtensionAttributeValue(EObject object, EStructuralFeature feature, Object value, int index, boolean delay) {
		if (object instanceof ExtensionAttributeValue)
			object = object.eContainer();
		EStructuralFeature evf = object.eClass().getEStructuralFeature("extensionValues"); //$NON-NLS-1$
		EList<EObject> list = (EList<EObject>)object.eGet(evf);
		
		if (list.size()==0) {
			ExtensionAttributeValue newItem = Bpmn2ModelerFactory.create(ExtensionAttributeValue.class);
			ModelUtil.setID(newItem);
			FeatureMap map = newItem.getValue();
			map.add(feature, value);
			if (delay) {
				InsertionAdapter.add(object, feature, (EObject)value);
			}
			else {
				list.add(newItem);
			}
		}
		else {
			ExtensionAttributeValue oldItem = (ExtensionAttributeValue) list.get(0);
			if (delay) {
				InsertionAdapter.add(object, feature, (EObject)value);
			}
			else {
				FeatureMap map = oldItem.getValue();
				if (!feature.isMany()) {
					// only one of these features is allowed: remove existing one(s)
					for (int i=0; i<map.size(); ++i) {
						Entry entry = map.get(i);
						if (entry.getEStructuralFeature().getName().equals(feature.getName())) {
							map.remove(i--);
						}
					}
					map.add(feature, value);
				}
				else if (index>=0){
				}
				else {
					map.add(feature, value);
				}
			}
		}
	}

	/**
	 * Return a list of all extension elements in the BaseElement's extension values container.
	 * 
	 * @param be - the EObject to search. This SHOULD be a BaseElement.
	 * @return a list of all extension elements or an empty list if none found.
	 */
	public static List<ExtensionAttributeValue> getExtensionAttributeValues(EObject be) {
		if (be instanceof Participant) {
			final Participant participant = (Participant) be;
			if (participant.getProcessRef() == null) {
				if (participant.eContainer() instanceof Collaboration) {
					Collaboration collab = (Collaboration) participant.eContainer();
					if (collab.eContainer() instanceof Definitions) {
						final Definitions definitions = ModelUtil.getDefinitions(collab);
						
						TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(definitions.eResource());
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								Process process = Bpmn2ModelerFactory.create(Process.class);
								participant.setProcessRef(process);
								definitions.getRootElements().add(process);
								ModelUtil.setID(process);
							}
							
						});
						
					}
				}
			}
			return participant.getProcessRef().getExtensionValues();
		}
		if (be instanceof BPMNDiagram) {
			BPMNDiagram diagram = (BPMNDiagram) be;
			BaseElement bpmnElement = diagram.getPlane().getBpmnElement();
			if (bpmnElement instanceof org.eclipse.bpmn2.Process) {
				return bpmnElement.getExtensionValues();
			}
		}
		if (be instanceof BaseElement) {
			return ((BaseElement) be).getExtensionValues();
		}
	
		return new ArrayList<ExtensionAttributeValue>();
	}

	/**
	 * Return a list of all extension elements in the BaseElement's extension values container
	 * that have the specified java type.
	 * 
	 * @param be - the EObject to search. This SHOULD be a BaseElement.
	 * @param clazz - the type of elements to search for.
	 * @return a list of all extension elements or an empty list if none found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllExtensionAttributeValues(EObject object, Class<T> clazz) {
		List<T> results = new ArrayList<T>();
		
		if (object!=null) {
			EStructuralFeature evf = object.eClass().getEStructuralFeature("extensionValues"); //$NON-NLS-1$
			EList<ExtensionAttributeValue> list = (EList<ExtensionAttributeValue>)object.eGet(evf);
			for (ExtensionAttributeValue eav : list) {
				FeatureMap fm = eav.getValue();
				for (Entry e : fm) {
					if (clazz.isInstance(e.getValue())) {
						results.add((T)e.getValue());
					}
				}
			}
		}
		return results;
	}

	/**
	 * Return a list of Objects that are the values of all extension elements specified by the given feature.
	 * 
	 * @param object - the EObject to be searched. This SHOULD be a BaseElement.
	 * @param feature - the EStructuralFeature to search for.
	 * @return a list of Object values for the extension elements.
	 */
	public static List<Object> getAllExtensionAttributeValues(EObject object, EStructuralFeature feature) {
		List<Object> results = new ArrayList<Object>();
		
		if (object!=null) {
			String name = feature.getName();
			EStructuralFeature evf = object.eClass().getEStructuralFeature("extensionValues"); //$NON-NLS-1$
			EList<ExtensionAttributeValue> list = (EList<ExtensionAttributeValue>)object.eGet(evf);
			for (ExtensionAttributeValue eav : list) {
				FeatureMap fm = eav.getValue();
				for (Entry e : fm) {
					if (e.getEStructuralFeature().getName().equals(name)) {
						results.add(e.getValue());
					}
				}
			}
		}
		return results;
	}

	/**
	 * Search the given object for an extension element by name.
	 * 
	 * @param object - the EObject to be searched. This SHOULD be a BaseElement.
	 * @param name - name of the feature to search for.
	 * @return an EStructuralFeature if found, or null if not found.
	 */
	@SuppressWarnings("unchecked")
	public static EStructuralFeature getExtensionAttribute(EObject object, String name) {
		if (object!=null) {
			EStructuralFeature evf = object.eClass().getEStructuralFeature("extensionValues"); //$NON-NLS-1$
			EList<ExtensionAttributeValue> list = (EList<ExtensionAttributeValue>)object.eGet(evf);
			for (ExtensionAttributeValue eav : list) {
				FeatureMap fm = eav.getValue();
				for (Entry e : fm) {
					if (e.getEStructuralFeature().getName().equals(name)) {
						return e.getEStructuralFeature();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Look up a dynamic feature associated with the given EObject by name.
	 * 
	 * @param object - the EObject to check.
	 * @param prefix - a namespace prefix passed in by the XMLHandler - not used here.
	 * @param name - name of the dynamic feature.
	 * @param isElement - true if the feature is an element, false if attribute.
	 * @return an EStructuralFeature if the feature was found, or null if not found.
	 */
	public EStructuralFeature getFeature(EObject object, String prefix, String name, boolean isElement) {
		// search for the object's type in our own package
		EClass eClass = getEClass(object.eClass().getName());
		if (eClass!=null) {
			// found it! check if it has the requested feature
			EStructuralFeature feature = eClass.getEStructuralFeature(name);
			if (feature!=null) {
				if (isElement) {
					if (feature instanceof EReference)
						return feature;
				}
				else {
					if (feature instanceof EAttribute)
						return feature;
				}
			}
		}
		object = object.eContainer();
		return null;
	}

	public EStructuralFeature getFeature(EObject object, String name) {
		EStructuralFeature feature = getAnyAttribute(object, name);
		if (feature!=null)
			return feature;
		feature = getExtensionAttribute(object, name);
		if (feature!=null)
			return feature;

		return null;
	}
}
