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

import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

/**
 * The abstract base class for Target Runtime Extension Descriptor classes.
 * This class provides methods for adding and removing instances of its subclasses to the
 * correct list in the TargetRuntime instance - this is done with java reflection in setRuntime()
 * and dispose() respectively.
 * 
 * All subclasses MUST conform as follows:
 * 
 * - define a static String field named EXTENSION_NAME which must be the same as the
 * - implement the method getExtensionName() which MUST return EXTENSION_NAME (unfortunately
 *   java does not allow class fields to be overridden the same as methods) 
 *   org.eclipse.bpmn2.modeler.runtime extension point element that it supports.
 * - define a public constructor that accepts and IConfigurationElement (this comes from
 *   the extension plugin's configuration, i.e. plugin.xml)
 * - optionally override setRuntime() to perform additional class initialization
 * - optionally override dispose() to perform additional cleanup
 * 
 * Extension Descriptor classes 
 */
public abstract class BaseRuntimeExtensionDescriptor implements IRuntimeExtensionDescriptor {

	protected TargetRuntime targetRuntime;
	protected IFile configFile;
	protected long configFileTimestamp;
	protected final IConfigurationElement configurationElement;
	protected String id;

	public static <T extends BaseRuntimeExtensionDescriptor> T getDescriptor(EObject object, Class type) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object);
		if (adapter!=null) {
			return (T)adapter.getProperty(type.getName());
		}
		return null;
	}
	
	public BaseRuntimeExtensionDescriptor() {
		configurationElement = null;
	}
	
	public BaseRuntimeExtensionDescriptor(IConfigurationElement e) {
		configurationElement = e;
		id = e.getAttribute("id"); //$NON-NLS-1$
	}

	public String getId() {
		return id;
	}

	public void dispose() {
		List<IRuntimeExtensionDescriptor> list = targetRuntime.getRuntimeExtensionDescriptors(getExtensionName());
		list.remove(this);
	}

	public BaseRuntimeExtensionDescriptor(TargetRuntime rt) {
		targetRuntime = rt;
		configurationElement = rt.configurationElement;
	}
	
	public IFile getConfigFile() {
		return configFile;
	}

	public void setConfigFile(IFile configFile) {
		this.configFile = configFile;
		if (configFile!=null)
			configFileTimestamp = configFile.getLocalTimeStamp();
		else
			configFileTimestamp = 0;
	}
	
	public long getConfigFileTimestamp() {
		return configFileTimestamp;
	}
	
	public TargetRuntime getRuntime() {
		return targetRuntime;
	}

	public void setRuntime(TargetRuntime targetRuntime) {
		this.targetRuntime = targetRuntime;
		List<IRuntimeExtensionDescriptor> list = targetRuntime.getRuntimeExtensionDescriptors(getExtensionName());
		list.add(this);
	}
	
	public EPackage getEPackage() {
		if (targetRuntime.getModelDescriptor()!=null)
			return targetRuntime.getModelDescriptor().getEPackage();
		return Bpmn2Package.eINSTANCE;
	}

	/**
	 * @param className
	 * @param featureName
	 * @return
	 * @deprecated use {@link ModelExtensionDescriptor#getEStructuralFeature(String, String)} instead
	 */
	public EStructuralFeature getFeature(String className, String featureName) {
		return getFeature(className + "." + featureName); //$NON-NLS-1$
	}
	
	/**
	 * Search the Target Runtime's EPackage for a structural feature with the specified name.
	 * If the feature is not found in the runtime package, search the Bpmn2Package.
	 * 
	 * @param name - name of the feature that specifies both an EClass and an EStructuralFeature
	 *               in the form "EClassName.EStructuralFeatureName"
	 * @return
	 * @deprecated use {@link ModelExtensionDescriptor#getEStructuralFeature(String, String)} instead
	 */
	public EStructuralFeature getFeature(String name) {
		String[] parts = name.split("\\."); //$NON-NLS-1$
		EClass eClass = (EClass)getEPackage().getEClassifier(parts[0]);
		if (eClass==null) {
			eClass = (EClass)Bpmn2Package.eINSTANCE.getEClassifier(parts[0]);
		}
		if (eClass!=null) {
			EStructuralFeature feature = eClass.getEStructuralFeature(parts[1]);
			if (feature!=null) {
				if (ExtendedMetaData.INSTANCE.getFeatureKind(feature) == ExtendedMetaData.UNSPECIFIED_FEATURE) {
					if (feature instanceof EAttribute) {
						ExtendedMetaData.INSTANCE.setFeatureKind(feature,ExtendedMetaData.ATTRIBUTE_FEATURE);
					}
					else {
						ExtendedMetaData.INSTANCE.setFeatureKind(feature,ExtendedMetaData.ELEMENT_FEATURE);
					}
					ExtendedMetaData.INSTANCE.setNamespace(feature, eClass.getEPackage().getNsURI());
					ExtendedMetaData.INSTANCE.setName(feature, feature.getName());
				}
			}
			return feature;
		}
		return null;
	}

	/**
	 * @param name
	 * @return
	 * @deprecated use {@link ModelExtensionDescriptor#getEClass(String)} instead
	 */
	public EClassifier getClassifier(String name) {
		EClassifier eClass = getEPackage().getEClassifier(name);
		if (eClass==null) {
			eClass = Bpmn2Package.eINSTANCE.getEClassifier(name);
		}
		return eClass;
	}
}