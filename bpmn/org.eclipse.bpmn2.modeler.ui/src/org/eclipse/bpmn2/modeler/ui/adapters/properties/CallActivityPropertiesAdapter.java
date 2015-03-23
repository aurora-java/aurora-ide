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

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesProvider;
import org.eclipse.bpmn2.modeler.core.utils.ImportUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Bob Brodt
 *
 */
public class CallActivityPropertiesAdapter extends ActivityPropertiesAdapter<CallActivity> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public CallActivityPropertiesAdapter(AdapterFactory adapterFactory, CallActivity object) {
		super(adapterFactory, object);

    	EStructuralFeature ce = Bpmn2Package.eINSTANCE.getCallActivity_CalledElementRef();
    	setProperty(ce, UI_CAN_CREATE_NEW, Boolean.TRUE);
    	setProperty(ce, UI_IS_MULTI_CHOICE, Boolean.TRUE);

    	setFeatureDescriptor(ce,
			new RootElementRefFeatureDescriptor<CallActivity>(this,object,ce) {
    		
				@Override
				public String getLabel() {
					return Messages.CallActivityPropertiesAdapter_Called_Activity;
				}
				
				@Override
				public String getTextValue() {
					CallableElement ce = object.getCalledElementRef();
					if (ce!=null && ce.eIsProxy()) {
						URI uri = ((InternalEObject)ce).eProxyURI();
						if (uri.hasFragment())
							return uri.fragment();
						return uri.lastSegment();
					}
					return super.getTextValue();
				}
				
				@Override
				public Hashtable<String, Object> getChoiceOfValues() {
					Hashtable<String, Object> choices = new Hashtable<String,Object>();
					String label;
					Definitions defs = ModelUtil.getDefinitions(object);

					// first add all local processes and global whatnots
					List<CallableElement> localProcesses = ModelUtil.getAllRootElements(defs, CallableElement.class);
					for (CallableElement elem : localProcesses) {
						label = ExtendedPropertiesProvider.getTextValue(elem) + " (" + elem.getId() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
						choices.put(label, elem);
					}
					
					// add processes defined in imports
					ImportUtil importer = new ImportUtil();
					List<Import> imports = defs.getImports();
					for (Import imp : imports) {
						if (ImportUtil.IMPORT_TYPE_BPMN2.equals(imp.getImportType())) {
							// load the process file and look for callable elements
							Object importedObject = importer.loadImport(imp);
							if (importedObject instanceof DocumentRoot) {
								Definitions importDefs = ((DocumentRoot)importedObject).getDefinitions();
								for (RootElement elem : importDefs.getRootElements()) {
									if (elem instanceof CallableElement) {
										label = ExtendedPropertiesProvider.getTextValue(elem) + " (" + imp.getLocation() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
										choices.put(label, elem);
									}
								}
							}
						}
					}

					// remove the Call Activity's owning CallableElement (can't make recursive calls?)
					EObject parent = object.eContainer();
					while (parent!=null && !(parent instanceof CallableElement)) {
						parent = parent.eContainer();
					}
					List<String> keys = new ArrayList<String>();
					for (Entry<String, Object> entry : choices.entrySet()) {
						if (entry.getValue() == parent)
							keys.add(entry.getKey());
					}
					for (String key : keys)
						choices.remove(key);
					
					return choices;
				}
			}
    	);
	}

}
