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
package org.eclipse.bpmn2.modeler.ui.property;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.Bpmn2SectionDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.PropertyTabDescriptor;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.BusinessObjectUtil;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptorProvider;

public class PropertyTabDescriptorProvider implements ITabDescriptorProvider {

	// This map caches the Tab Descriptor List. A Tab Descriptor List is built for each EObject and then
	// cached in this map. It is the BPMN2 Editor's responsibility to remove these items from the map
	// when it is disposed().
	// @see PropertyTabDescriptorProvider#disposeTabDescriptors(Resource)
	static Hashtable <EObject, TabDescriptorList> tabDescriptorListMap = new Hashtable <EObject, TabDescriptorList>();
	
	public PropertyTabDescriptorProvider() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITabDescriptor[] getTabDescriptors(IWorkbenchPart part, ISelection selection) {
		
		EObject businessObject = BusinessObjectUtil.getBusinessObjectForSelection(selection);
		if (businessObject==null || businessObject.eResource()==null) {
			return new ITabDescriptor[] {};
		}
		
		// is the Tab Descriptor List already in our cache?
		TabDescriptorList tabDescriptorList = tabDescriptorListMap.get(businessObject);
		if (tabDescriptorList!=null) {
			// Yes! return it.
			return tabDescriptorList.toArray();
		}
		
		// No, we need build the list: get the Target Runtime <propertyTab> contributions
		// and merge with the Default Runtime Tab Descriptors
		List<PropertyTabDescriptor> desc = null;
		TargetRuntime rt = Bpmn2Preferences.getInstance(businessObject.eResource()).getRuntime();
		if (rt!=TargetRuntime.getDefaultRuntime()) {
			desc = TargetRuntime.getDefaultRuntime().buildPropertyTabDescriptors();
			desc.addAll(rt.buildPropertyTabDescriptors());
		}
		else
			desc = rt.buildPropertyTabDescriptors();
		
		// do tab replacement
		ArrayList<PropertyTabDescriptor> replaced = new ArrayList<PropertyTabDescriptor>();
		for (PropertyTabDescriptor d : desc) {
			String replacedId = d.getReplaceTab();
			if (replacedId!=null) {
				String[] replacements = replacedId.split(" "); //$NON-NLS-1$
				// tab replacement is only done if the replacement tab has section descriptors
				// that want the replacement to happen.
				for (String id : replacements) {
					for (Bpmn2SectionDescriptor s : (List<Bpmn2SectionDescriptor>) d.getSectionDescriptors()) {
//						if (s.appliesTo(part, selection)) 
						{
							// ask the section if it wants to replace this tab
							if (s.doReplaceTab(id, part, selection)) {
								// replace the tab whose ID is specified as "replaceTab" in this tab.
								PropertyTabDescriptor replacedTab = TargetRuntime.findPropertyTabDescriptor(id);
								if (replacedTab!=null) {
									replaced.add(replacedTab);
									int i = desc.indexOf(replacedTab);
									if (i>=0) {
										desc.set(i, d);
									}
								}
							}
						}
					}
				}
			}
		}
		if (replaced.size()>0)
			desc.removeAll(replaced);

		for (int i=desc.size()-1; i>=0; --i) {
			PropertyTabDescriptor d = desc.get(i);
			for (int j=i-1; j>=0; --j) {
				if (desc.get(j)==d) {
					desc.remove(i);
					break;
				}
			}
		}
		
		// remove empty tabs
		// also move the advanced tab to end of list
		ArrayList<PropertyTabDescriptor> emptyTabs = new ArrayList<PropertyTabDescriptor>();
		PropertyTabDescriptor advancedPropertyTab = null;
		for (PropertyTabDescriptor d : desc) {
			boolean empty = true;
			for (Bpmn2SectionDescriptor s : (List<Bpmn2SectionDescriptor>) d.getSectionDescriptors()) {
				if (s.appliesTo(part, selection)) {
					empty = false;
				}
				if (s.getSectionClass() instanceof AdvancedPropertySection) {
					advancedPropertyTab = d;
				}
			}
			if (empty) {
				emptyTabs.add(d);
			}
		}
		
		if (emptyTabs.size()>0)
			desc.removeAll(emptyTabs);
		
		if (advancedPropertyTab!=null) {
			if (desc.remove(advancedPropertyTab))
				desc.add(advancedPropertyTab);
		}
		
		// make copies of all tab descriptors to prevent cross-talk between editors
		replaced.clear(); // we'll just reuse an ArrayList from before...
		for (PropertyTabDescriptor td : desc) {
			// Note that the copy() makes the Tab Descriptor IDs and Section IDs unique.
			// This is important because the TabbedPropertySheetPage uses these IDs to
			// look up the Sections.
			String rtid = td.getRuntimeId();
			if (rtid==null || rtid.equals(TargetRuntime.DEFAULT_RUNTIME_ID) || rt.getId().equals(rtid)) {
				if (td.getConfigFile()!=null && !rt.getId().equals(rtid))
					// don't include Default Runtime tabs that were defined in a config file
					// if this isn't the Default Runtime.
					continue;
				// what's left is just the Tab Descriptors defined by the current Target Runtime
				// and the ones from the Default ("None") runtime.
				replaced.add(td.copy());
			}
		}
		
		// save this in the cache.
		if (businessObject!=null) {
			tabDescriptorList = new TabDescriptorList();
			tabDescriptorList.addAll(replaced);
			tabDescriptorListMap.put(businessObject, tabDescriptorList);
			return tabDescriptorList.toArray();
		}
		
		return replaced.toArray(new ITabDescriptor[replaced.size()]);
	}

	/**
	 * This should be called by the editor during dispose() to remove all the items
	 * from the cache.
	 *  
	 * @param resource - the EMF Resource containing the EObjects for which
	 *        Property Tab Descriptors were built.
	 */
	public void disposeTabDescriptors(Resource resource) {
		if (resource!=null) {
			TreeIterator<EObject> iter = resource.getAllContents();
			while (iter.hasNext()) {
				EObject object = iter.next();
				tabDescriptorListMap.remove(object);
			}
		}
		// clean up any dangling EObjects (ones that are not contained in a Resource)
		List<EObject> removed = new ArrayList<EObject>();
		for (EObject o : tabDescriptorListMap.keySet()) {
			if (o.eResource()==null) {
				removed.add(o);
			}
		}
		for (EObject o : removed)
			tabDescriptorListMap.remove(o);
	}
}
