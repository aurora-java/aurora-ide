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

import java.util.Collection;

import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.graphiti.features.IFeature;

/**
 * Target Runtime Extension Descriptor class for model enablements. This class
 * controls the visibility of an object and its features in the UI Property
 * Sheets and dialogs. Any object or object feature that is not explicitly
 * "enabled" will not be visible in the UI.
 * 
 * This class encapsulates a so-called "Tool Profile" which defines a set of
 * BPMN2 elements and attributes. When a Tool Profile is selected in the Tool
 * Palette, only those elements and attributes that are enabled in this set will
 * be visible in the Tool Palette and Property Sheets.
 * 
 * Instances of this class correspond to <modelEnablement> extension elements in
 * the extension's plugin.xml See the description of the "modelEnablement"
 * element in the org.eclipse.bpmn2.modeler.runtime extension point schema.
 */
public class ModelEnablementDescriptor extends BaseRuntimeExtensionDescriptor {

	public final static String EXTENSION_NAME = "modelEnablement"; //$NON-NLS-1$

	/** Model Types that are enabled. **/
	private ModelEnablements modelEnablements;
	/** Human-readable Tool Profile name. **/
	private String profileName;
	/** Descriptive text for Tool Palette. **/
	private String description;

	
	public ModelEnablementDescriptor(IConfigurationElement e) {
		super(e);
		TargetRuntime rt = TargetRuntime.getRuntime(e);
		profileName = e.getAttribute("profile"); //$NON-NLS-1$
		description = e.getAttribute("description"); //$NON-NLS-1$
		String ref = e.getAttribute("ref"); //$NON-NLS-1$

		modelEnablements = new ModelEnablements(rt, id);
		
		if (ref!=null) {
			String a[] = ref.split(":"); //$NON-NLS-1$
			rt = TargetRuntime.getRuntime(a[0]);
			String id = a[1];
			initializeFromTargetRuntime(rt, id);
		}
		
		for (IConfigurationElement c : e.getChildren()) {
			String object = c.getAttribute("object"); //$NON-NLS-1$
			String feature = c.getAttribute("feature"); //$NON-NLS-1$
			if (c.getName().equals("enable")) { //$NON-NLS-1$
				setEnabled(object, feature, true);
			} else if (c.getName().equals("disable")) { //$NON-NLS-1$
				setEnabled(object, feature, false);
			}
		}

	}
	
	public String getExtensionName() {
		return EXTENSION_NAME;
	}

	public ModelEnablementDescriptor(TargetRuntime rt, String id) {
		super(rt);
		this.id = id;
		modelEnablements = new ModelEnablements(rt, id);
	}
	
	public void setEnabled(EClass eClass, boolean enabled) {
		modelEnablements.setEnabled(eClass, enabled);
	}
	
	public void setEnabled(String className, boolean enabled) {
		modelEnablements.setEnabled(className, enabled);
	}
	
	public void setEnabled(String className, String featureName, boolean enabled) {
		modelEnablements.setEnabled(className,  featureName, enabled);
	}

	public void initializeFromTargetRuntime(TargetRuntime rt, String id) {
		
		for (ModelEnablementDescriptor med : rt.getModelEnablements()) {
			if (id.equals(med.getId())) {
				Collection<String> enabledClasses = med.modelEnablements.getAllEnabledClasses();
				for (String c : enabledClasses) {
					Collection<String> enabledFeatures = med.modelEnablements.getAllEnabledFeatures(c);
					for (String f : enabledFeatures) {
						setEnabled(c, f, true);
					}
				}
				break;
			}
		}
	}

	public boolean isEnabled(String className, String featureName) {
		return modelEnablements.isEnabled(className, featureName);
	}
	
	public boolean isEnabled(EClass eClass, EStructuralFeature feature) {
		return modelEnablements.isEnabled(eClass, feature);
	}
	
	public boolean isEnabled(EClass eClass) {
		return modelEnablements.isEnabled(eClass);
	}

	public boolean isEnabled(String className) {
		return modelEnablements.isEnabled(className);
	}
	
	public boolean isEnabled(IFeature feature) {
		return modelEnablements.isEnabled(feature);
	}

	public Collection<String> getAllEnabled() {
		return modelEnablements.getAllEnabled();
	}
	
	public String getProfileName() {
		return profileName;
	}
	
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public String getDescription() {
		if (description==null)
			return "";
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
