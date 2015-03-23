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
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.modeler.core.preferences.ModelEnablements;
import org.eclipse.bpmn2.modeler.core.runtime.ModelEnablementDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ModelExtensionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.ModelExtensionDescriptor.Property;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.ui.diagram.Bpmn2FeatureMap;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.osgi.service.prefs.BackingStoreException;

public class ToolProfilesPreferencesHelper {

	private ModelEnablements modelEnablements;
	private static HashSet<EClass> elementSet = null;
	private TargetRuntime targetRuntime;
	private String profileId;
	private static boolean enableIdAttribute = false;

	private ToolProfilesPreferencesHelper() {
	}
	
	public ToolProfilesPreferencesHelper(TargetRuntime rt, String profileId) {
		this.targetRuntime = rt;
		this.profileId = profileId;
		createElementSet();
	}

	/**
	 * Create the list of EClasses that are candidates for our enable/disable tree.
	 * As a first cut, select only top-level classes from the Bpmn2Package, then
	 * add any classes that were enabled in any of the Target Runtime plugins.
	 */
	private void createElementSet() {
		if (elementSet==null) {
			elementSet = new HashSet<EClass>();
			Bpmn2Package pkg = Bpmn2Package.eINSTANCE;
//			EList<EClassifier> allClassifiers = pkg.getEClassifiers();
//			final List<EClass> elements = new ArrayList<EClass>();
//			for (EClassifier candidate : allClassifiers) {
//				if (candidate instanceof EClass && candidate!=pkg.getDocumentRoot()) {
//					boolean add = true;
//					for (EClassifier ec : allClassifiers) {
//						if (ec!=candidate && ec instanceof EClass) {
//							for (EClass superType : ((EClass)ec).getESuperTypes()) {
//								if (superType == candidate) {
//									add = false;
//									break;
//								}
//							}
//						}
//					}
//					if (add)
//						elements.add((EClass)candidate);
//				}
//			}
//			elementSet.addAll(elements);
			for (Class c : Bpmn2FeatureMap.ALL_SHAPES) {
				elementSet.add((EClass)Bpmn2Package.eINSTANCE.getEClassifier(c.getSimpleName()));
			}
			
//			for (TargetRuntime rt : TargetRuntime.createTargetRuntimes()) {
//				for (ModelEnablementDescriptor med : rt.getModelEnablementDescriptors()) {
//					for (String name : med.getAllEnabled()) {
//						int i = name.indexOf("."); //$NON-NLS-1$
//						if (i>0)
//							name = name.substring(i);
//						EClassifier ec = pkg.getEClassifier(name);
//						if (ec instanceof EClass && !elementSet.contains(ec)) {
//							elementSet.add((EClass)ec);
//						}
//					}
//				}
//			}
		}
	}
	
	public static void setEnableIdAttribute(boolean enabled) {
		enableIdAttribute = enabled;
	}

	public static boolean getEnableIdAttribute() {
		return enableIdAttribute;
	}

	public void setModelEnablements(ModelEnablements me) {
		modelEnablements = me;
		if (modelEnablements!=null)
			modelEnablements.setEnableIdAttribute(enableIdAttribute);
	}

	public ModelEnablements getModelEnablements() {
		return modelEnablements;
	}

	public void copyModelEnablements(ModelEnablements copyMe) {
		if (modelEnablements==null) {
			modelEnablements = new ModelEnablements(targetRuntime, profileId);
		}
		modelEnablements.setEnabledAll(false);
		for (String name : copyMe.getAllEnabled()) {
			modelEnablements.setEnabled(name, true);
		}
	}

	public void clear() {
		if (modelEnablements!=null)
			modelEnablements.setEnabledAll(false);
	}
	
	public List<ModelEnablementTreeEntry> getAllElements() {
		ArrayList<ModelEnablementTreeEntry> ret = new ArrayList<ModelEnablementTreeEntry>();

		for (EClass eClass : elementSet) {
			ModelEnablementTreeEntry entry = new ModelEnablementTreeEntry();
			entry.setElement(eClass);
			boolean enable = isEnabled(eClass);
			entry.setEnabled(enable);
			ret.add(entry);

			HashSet<EStructuralFeature> possibleFeatures = new HashSet<EStructuralFeature>();

			ArrayList<ModelEnablementTreeEntry> children = new ArrayList<ModelEnablementTreeEntry>();

			for (EAttribute a : eClass.getEAllAttributes()) {
				// anyAttribute is always enabled to support
				// extension features.
				if (!"anyAttribute".equals(a.getName())) //$NON-NLS-1$
						possibleFeatures.add(a);
			}

			for (EReference a : eClass.getEAllContainments()) {
				possibleFeatures.add(a);
			}

			for (EReference a : eClass.getEAllReferences()) {
				possibleFeatures.add(a);
			}

			if (enable) {
				for (EStructuralFeature feature : possibleFeatures) {
					ModelEnablementTreeEntry child = new ModelEnablementTreeEntry(feature, entry);
					enable = isEnabled(eClass, feature);
					child.setEnabled(enable);
					children.add(child);
				}
				sortElements(children);
				entry.setChildren(children);
			}
		}
		sortElements(ret);
		return ret;
	}
	
	private ModelEnablementTreeEntry findOrCreateEntry(List<ModelEnablementTreeEntry> entries, ENamedElement elem, ModelEnablementTreeEntry parent) {
		ModelEnablementTreeEntry entry = findEntry(entries,elem,parent);
		if (entry!=null) {
			entry.addFriend(parent);
			return entry;
		}
		return new ModelEnablementTreeEntry(elem, parent);
	}
	
	private ModelEnablementTreeEntry findEntry(List<ModelEnablementTreeEntry> entries, ENamedElement elem, ModelEnablementTreeEntry parent) {
		for (ModelEnablementTreeEntry entry : entries) {
			ModelEnablementTreeEntry thisParent = entry.getParent();
			if (thisParent!=null && parent!=null) {
				if (thisParent.getElement() == parent.getElement()) {
					for (ModelEnablementTreeEntry child : thisParent.getChildren()) {
						ENamedElement thisElem = child.getElement();
						if (thisElem == elem) {
							return child;
						}
					}
				}
			}
			else if (thisParent==parent) {
				if (entry.getElement() == elem)
					return entry;
			}
			else if (thisParent==null) {
				ModelEnablementTreeEntry t = findEntry(entry.getChildren(), elem, parent);
				if (t!=null)
					return t;
			}
		}
		return null;
	}
	
	public List<ModelEnablementTreeEntry> getAllExtensionElements(TargetRuntime rt, ModelEnablementDescriptor me, List<ModelEnablementTreeEntry> bpmnEntries) {
		
		// Fetch all of the <modelExtension> extension point elements defined in the Target Runtime plugin.
		ArrayList<ModelEnablementTreeEntry> bpmnModelExtensions = new ArrayList<ModelEnablementTreeEntry>();
		ModelEnablementTreeEntry bpmnModelExtensionsRoot = new ModelEnablementTreeEntry();
		bpmnModelExtensionsRoot.setEnabled(true);
		bpmnModelExtensionsRoot.setName(Messages.ToolEnablementPreferences_BPMN_Extensions);
		for (Entry<EClass, List<EStructuralFeature>> e : rt.getModelExtensions(1).entrySet()) {
			// create a ModelEnablementTreeEntry for extension EClass
			EClass eClass = e.getKey();
			ModelEnablementTreeEntry entry = new ModelEnablementTreeEntry(eClass, bpmnModelExtensionsRoot);
			// fetch its current enablement state
			entry.setEnabled(isEnabled(eClass));
			// and add it to our list
			bpmnModelExtensions.add(entry);
			ArrayList<ModelEnablementTreeEntry> children = new ArrayList<ModelEnablementTreeEntry>();
			for (EStructuralFeature feature : e.getValue()) {
				ModelEnablementTreeEntry child = findOrCreateEntry(bpmnEntries, feature, entry);
				// set enablement state of the feature:
				// the EClass is that of the parent entry.
				child.setEnabled(isEnabled(eClass, feature));
				children.add(child);
			}
			// add the sorted list to the children of this entry parent
			sortElements(children);
			entry.setChildren(children);
		}		
		sortElements(bpmnModelExtensions);
		bpmnModelExtensionsRoot.setChildren(bpmnModelExtensions);
		
		ArrayList<ModelEnablementTreeEntry> runtimeModelExtensions = new ArrayList<ModelEnablementTreeEntry>();
		ModelEnablementTreeEntry runtimeModelExtensionsRoot = new ModelEnablementTreeEntry();
		runtimeModelExtensionsRoot.setEnabled(true);
		runtimeModelExtensionsRoot.setName(Messages.ToolEnablementPreferences_Target_Extensions);
		for (Entry<EClass, List<EStructuralFeature>> e : rt.getModelExtensions(2).entrySet()) {
			// create a ModelEnablementTreeEntry for extension EClass
			EClass eClass = e.getKey();
			ModelEnablementTreeEntry entry = new ModelEnablementTreeEntry(eClass, runtimeModelExtensionsRoot);
			// fetch its current enablement state
			entry.setEnabled(isEnabled(eClass));
			// and add it to our list
			runtimeModelExtensions.add(entry);
			ArrayList<ModelEnablementTreeEntry> children = new ArrayList<ModelEnablementTreeEntry>();
			for (EStructuralFeature feature : e.getValue()) {
				ModelEnablementTreeEntry child = findOrCreateEntry(bpmnEntries, feature, entry);
				// set enablement state of the feature:
				// the EClass is that of the parent entry.
				child.setEnabled(isEnabled(eClass, feature));
				children.add(child);
			}
			// add the sorted list to the children of this entry parent
			sortElements(children);
			entry.setChildren(children);
		}		
		sortElements(runtimeModelExtensions);
		runtimeModelExtensionsRoot.setChildren(runtimeModelExtensions);

		ArrayList<ModelEnablementTreeEntry> allExtensions = new ArrayList<ModelEnablementTreeEntry>();
		if (!bpmnModelExtensionsRoot.getChildren().isEmpty())
			allExtensions.add(bpmnModelExtensionsRoot);
		if (!runtimeModelExtensionsRoot.getChildren().isEmpty())
		allExtensions.add(runtimeModelExtensionsRoot);
		
		return allExtensions;
	}

	private void sortElements(ArrayList<ModelEnablementTreeEntry> ret) {
		Collections.sort(ret, new Comparator<ModelEnablementTreeEntry>() {

			@Override
			public int compare(ModelEnablementTreeEntry o1, ModelEnablementTreeEntry o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}

		});
	}

	public boolean isEnabled(EClass element) {
		if (modelEnablements==null)
			return false;
		return modelEnablements.isEnabled(element);
	}

	public boolean isEnabled(String name) {
		if (modelEnablements==null)
			return false;
		return modelEnablements.isEnabled(name);
	}

	public boolean isEnabled(EClass c, ENamedElement element) {
		if (modelEnablements==null)
			return false;
		return modelEnablements.isEnabled(c.getName(), element.getName());
	}

	public boolean isEnabled(ModelEnablementTreeEntry entry) {
		if (modelEnablements==null)
			return false;
		return modelEnablements.isEnabled(entry.getPreferenceName());
	}

	public void setEnabled(ModelEnablementTreeEntry entry, boolean enabled) {
		if (modelEnablements!=null) {
			modelEnablements.setEnabled(entry.getPreferenceName(), enabled);
		}
	}

	public void importProfile(String path) throws FileNotFoundException, IOException, BackingStoreException {
		Properties p = new Properties();
		p.load(new FileInputStream(path));
		
		ModelEnablements me = new ModelEnablements(targetRuntime, profileId);

		for (Object key : p.keySet()) {
			Object value = p.get(key);
			if (key instanceof String && value instanceof String) {
				boolean enabled = Boolean.parseBoolean((String) value);
				me.setEnabled((String) key, enabled);
			}
		}
	}

	public void exportProfile(String path) throws BackingStoreException, FileNotFoundException, IOException {
		FileWriter fw = new FileWriter(path);
		boolean writeXml = path.endsWith(".xml"); //$NON-NLS-1$

		List<String> keys = modelEnablements.getAllEnabled();
		Collections.sort(keys);

		ModelEnablementDescriptor med = targetRuntime.getModelEnablements(profileId);
		String profileName = med.getProfileName();
		String description = med.getDescription();
		if (writeXml) {
			fw.write("\t\t<modelEnablement"); //$NON-NLS-1$
			fw.write(" runtimeId=\"" + targetRuntime.getId() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			fw.write(" id=\"" + profileId + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			fw.write(" profile=\"" + profileName + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			fw.write(" description=\"" + description + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			fw.write(">\r\n"); //$NON-NLS-1$
			
			fw.write("\t\t\t<disable object=\"all\"/>\r\n"); //$NON-NLS-1$
		}
		
		for (String k : keys) {
			boolean enable = isEnabled(k);
			if (writeXml) {
				if (enable) {
					if (k.contains(".")) { //$NON-NLS-1$
						String a[] = k.split("\\."); //$NON-NLS-1$
						fw.write("\t\t\t<enable object=\""+ a[0] + "\" feature=\"" + a[1] + "\"/>\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			}
			else
				fw.write(k + "=" + enable + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (writeXml) {
			fw.write("\t</modelEnablement>\r\n"); //$NON-NLS-1$
		}
		
		fw.flush();
		fw.close();
	}
}
